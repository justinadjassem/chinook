package net.devpull.chinook.sales.domain;

import net.devpull.chinook.catalog.domain.TrackId;
import net.devpull.chinook.shared.domain.Money;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class InvoiceTest {
    @Test
    void refuse_des_lignes_dans_des_devises_differentes() {
        var usd = new InvoiceLine(new TrackId(1L), Money.usd("0.99"), 1);
        var eur = new InvoiceLine(new TrackId(2L), new Money(new BigDecimal("0.99"), Currency.getInstance("EUR")), 1);

        assertThatThrownBy(() -> new Invoice(new InvoiceId(1L), new CustomerId(1L), LocalDateTime.now(), List.of(usd,eur)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("même devise");

    }
}
