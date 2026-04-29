package com.moonkeyeu.core.api.membership.payment.service;

import com.stripe.model.Invoice;

public interface StripeInvoiceEventHandler {
    void handleUpcomingInvoice(Invoice invoice);
    void handleInvoiceSucceeded(Invoice invoice);
}
