package com.moonkeyeu.core.api.membership.domain.repository;

import com.moonkeyeu.core.api.membership.domain.model.StripeInvoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StripeInvoiceRepository extends JpaRepository<StripeInvoice, Long> {}
