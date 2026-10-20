package net.devpull.chinook.sales.domain;

import net.devpull.chinook.shared.domain.Money;

public sealed interface PaymentResult {

    InvoiceId invoiceId();

    record Paid(InvoiceId invoiceId, Money paidAmount, String transactionRef) implements PaymentResult {}
    record  Declined(InvoiceId invoiceId, String reason) implements PaymentResult {}
    record Pending(InvoiceId invoiceId) implements PaymentResult {}
    record Refunded(InvoiceId invoiceId, Money refundedAmount, String reason) implements PaymentResult {}
}
