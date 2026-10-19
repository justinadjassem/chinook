package net.devpull.chinook.sales.domain;

import net.devpull.chinook.shared.domain.Money;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public final class Invoice {
    private final InvoiceId id;
    private final CustomerId customerId;
    private final LocalDateTime issuedAt;
    private final List<InvoiceLine> lines;

    public Invoice(InvoiceId id, CustomerId customerId, LocalDateTime issuedAt, List<InvoiceLine> lines) {
        this.id = Objects.requireNonNull(id, "id");
        this.customerId = Objects.requireNonNull(customerId, "customerId");
        this.issuedAt = Objects.requireNonNull(issuedAt, "issuedAt");
        if (lines == null || lines.isEmpty()) {
            throw new IllegalArgumentException("Une facture a au moins une ligne");
        }

        long currencies = lines.stream()
                .map(line -> line.unitPrice().currency())
                .distinct()
                .count();

        if (currencies > 1) {
            throw new IllegalArgumentException("Toutes les lignes d'une facture doivent être dans la même devise");
        }
        this.lines = List.copyOf(lines);
    }

    public Money total() {
        return lines.stream()
                .map(InvoiceLine::lineTotal)
                .reduce(Money::add)
                .orElseThrow();
    }

    public InvoiceId id() {
        return id;
    }

    public CustomerId customerId() {
        return customerId;
    }

    public LocalDateTime issuedAt() {
        return issuedAt;
    }

    public List<InvoiceLine> lines() {
        return lines;
    }

    @Override
    public boolean equals(Object o) {
       return o instanceof Invoice other && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
