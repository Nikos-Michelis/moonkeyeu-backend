package com.moonkeyeu.core.api.payment.util;

import com.moonkeyeu.core.api.user.model.User;
import com.moonkeyeu.core.api.user.reporitory.UserRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.CustomerSearchResult;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.CustomerSearchParams;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CustomerUtil {
    private final UserRepository userRepository;

    public Optional<Customer> findCustomerByEmail(String email) throws StripeException {
        CustomerSearchParams params =
                CustomerSearchParams
                        .builder()
                        .setQuery("email:'" + email + "'")
                        .build();

        CustomerSearchResult result = Customer.search(params);

        return Optional.ofNullable(result.getData().get(0));
    }

    @Transactional
    public User findOrCreateStripeCustomer(String email) throws StripeException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (user.getCustomerId() == null) {
            CustomerCreateParams customerCreateParams = CustomerCreateParams.builder()
                    .setEmail(email)
                    .build();

           Customer customer = Customer.create(customerCreateParams);
           user.setCustomerId(customer.getId());
           return userRepository.save(user);
        }

        return user;
    }
}