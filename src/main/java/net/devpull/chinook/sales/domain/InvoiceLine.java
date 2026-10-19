package net.devpull.chinook.sales.domain;

import net.devpull.chinook.catalog.domain.TrackId;
import net.devpull.chinook.shared.domain.Money;

import java.util.Objects;

public record InvoiceLine(TrackId trackId, Money unitPrice, int quantity) {

    public InvoiceLine{
        Objects.requireNonNull(trackId, "trackId");
        Objects.requireNonNull(unitPrice, "unitPrice");
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantité doit être > 0 : " + quantity);
        }
    }

    public Money lineTotal() {
        return unitPrice.multiply(quantity);
    }
}
