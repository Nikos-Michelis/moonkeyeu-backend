package com.moonkeyeu.core.api.membership.payment.service.impl;

import com.moonkeyeu.core.api.membership.payment.dto.PaymentRequestDTO;
import com.moonkeyeu.core.api.membership.payment.service.StripeInvoiceService;
import com.moonkeyeu.core.api.membership.payment.util.CustomerUtil;
import com.moonkeyeu.core.api.settings.exceptions.stripe.CustomerNotFoundException;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.Invoice;
import com.stripe.model.InvoiceCollection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class StripeInvoiceServiceImpl implements StripeInvoiceService {
    private final CustomerUtil customerUtil;

    @Override
    public List<Map<String, String>> getInvoices(PaymentRequestDTO requestDTO) throws StripeException {
        Customer customer = customerUtil.findCustomerByEmail(requestDTO.email())
                .orElseThrow(() -> new CustomerNotFoundException("Stripe Customer Not Found."));

        Map<String, Object> invoiceSearchParams = new HashMap<>();
        invoiceSearchParams.put("customer", customer.getId());
        InvoiceCollection invoices = Invoice.list(invoiceSearchParams);

        List<Map<String, String>> response = new ArrayList<>();
        for (Invoice invoice : invoices.getData()) {
            HashMap<String, String> map = new HashMap<>();
            map.put("number", invoice.getNumber());
            map.put("amount", String.valueOf((invoice.getTotal() / 100f)));
            map.put("url", invoice.getInvoicePdf());

            response.add(map);
        }

        return response;
    }
}
