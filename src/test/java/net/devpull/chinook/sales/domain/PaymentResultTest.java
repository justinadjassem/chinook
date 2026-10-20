package net.devpull.chinook.sales.domain;

import net.devpull.chinook.shared.domain.Money;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PaymentResultTest {
    static  String describeJava17(PaymentResult result){
        if (result instanceof PaymentResult.Paid paid) {
            return "Payee: " + paid.paidAmount().amount();
        }
        if (result instanceof PaymentResult.Declined declined) {
            return "Refusee: " + declined.reason();
        }
        if (result instanceof PaymentResult.Pending) {
            return "En attente";
        }
        if (result instanceof PaymentResult.Refunded refunded) {
            return "Remboursee: " + refunded.refundedAmount().amount() + " (" + refunded.reason() + ")";
        }
        throw new IllegalArgumentException("Cas non prevu : " + result);
    }

    static String describeJava21(PaymentResult result){
        return switch (result) {
            case PaymentResult.Paid(var id, var amount, var ref) -> "Payee: " + amount.amount();
            case PaymentResult.Declined declined -> "Refusee: " + declined.reason();
            case PaymentResult.Pending pending -> "En attente";
            case PaymentResult.Refunded(var id, var refundedAmount, var reason) -> "Remboursee: " + refundedAmount.amount() + " (" + reason + ")";
        };
    }

    private final InvoiceId invoiceId = new InvoiceId(1L);

    @Test
    void decrit_un_paiement_accepte() {
        var paid = new PaymentResult.Paid(invoiceId, Money.usd("3.97"), "TX-42");
        assertThat(describeJava17(paid)).isEqualTo("Payee: 3.97");
        assertThat(describeJava21(paid)).isEqualTo("Payee: 3.97");
    }

    @Test
    void decrit_un_paiement_refuse() {
        var declined = new PaymentResult.Declined(invoiceId, "Fonds insuffisants");
        assertThat(describeJava17(declined)).isEqualTo("Refusee: Fonds insuffisants");
        assertThat(describeJava21(declined)).isEqualTo("Refusee: Fonds insuffisants");
    }

    @Test
    void decrit_un_paiement_en_attente() {
        var pending = new PaymentResult.Pending(invoiceId);
        assertThat(describeJava17(pending)).isEqualTo("En attente");
        assertThat(describeJava21(pending)).isEqualTo("En attente");
    }

    @Test
    void decrit_un_remboursement() {
        var refunded = new PaymentResult.Refunded(invoiceId, Money.usd("0.99"), "Piste en double");
        assertThat(describeJava17(refunded)).isEqualTo("Remboursee: 0.99 (Piste en double)");
        assertThat(describeJava21(refunded)).isEqualTo("Remboursee: 0.99 (Piste en double)");
    }

}
