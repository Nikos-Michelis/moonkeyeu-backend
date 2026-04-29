package com.moonkeyeu.core.api.membership.domain.repository;

import com.moonkeyeu.core.api.membership.domain.model.StripeCustomer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StripeCustomerRepository extends JpaRepository<StripeCustomer, String> {
    Optional<StripeCustomer> findStripeCustomerByStripeCustomerId(String stripeCustomerId);
}
