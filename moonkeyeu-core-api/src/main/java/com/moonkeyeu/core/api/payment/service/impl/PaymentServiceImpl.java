package com.moonkeyeu.core.api.payment.service.impl;

import com.moonkeyeu.core.api.payment.dto.PaymentRequestDTO;
import com.moonkeyeu.core.api.payment.dto.PaymentResponseDTO;
import com.moonkeyeu.core.api.payment.repository.ProductDAO;
import com.moonkeyeu.core.api.payment.service.PaymentService;
import com.moonkeyeu.core.api.payment.util.CustomerUtil;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.Product;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.stereotype.Service;
@Service
public class PaymentServiceImpl implements PaymentService {
    private final String clientBaseURL = "http://localhost:8081";

    @Override
    public String createSubscription(PaymentRequestDTO requestDTO) throws StripeException {
        // Start by finding existing customer record from Stripe or creating a new one if needed
        Customer customer = CustomerUtil.findOrCreateCustomer(requestDTO.email());

        // Next, create a checkout session by adding the details of the checkout
        SessionCreateParams.Builder paramsBuilder =
                SessionCreateParams.builder()
                        .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                        .setCustomer(customer.getId())
                        .setSuccessUrl(clientBaseURL + "/success?session_id={CHECKOUT_SESSION_ID}")
                        .setCancelUrl(clientBaseURL + "/failure");

        for (Product product : requestDTO.product()) {
            System.out.println(product);
            Product dbProduct = ProductDAO.getProduct(product.getId());
            var defaultPrice = dbProduct.getDefaultPriceObject();

            var recurring = SessionCreateParams.LineItem.PriceData.Recurring.builder()
                    .setInterval(SessionCreateParams.LineItem.PriceData.Recurring.Interval.MONTH)
                    .build();

            var productData = SessionCreateParams.LineItem.PriceData.ProductData.builder()
                    .putMetadata("app_id", product.getId())
                    .setName((product.getName() != null) ? product.getName() : defaultPrice.getNickname() )
                    .build();

            var priceData = SessionCreateParams.LineItem.PriceData.builder()
                    .setCurrency(defaultPrice.getCurrency())
                    .setUnitAmountDecimal(defaultPrice.getUnitAmountDecimal())
                    .setProductData(productData)
                    .setRecurring(recurring)
                    .build();

            var lineItem = SessionCreateParams.LineItem.builder()
                    .setQuantity(1L)
                    .setPriceData(priceData)
                    .build();
            System.out.println(lineItem.getMetadata());
            paramsBuilder.addLineItem(lineItem);
        }

        Session session = Session.create(paramsBuilder.build());

        return session.getUrl();
    }
}
