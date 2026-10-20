package net.devpull.chinook.sales.domain;

import net.devpull.chinook.catalog.domain.TrackId;

import java.util.Objects;

public record OrderItem(TrackId trackId, int quantity) {
    public OrderItem{
        Objects.requireNonNull(trackId, "trackId");
        if (quantity <= 0) throw new IllegalArgumentException("La quantite doit etre superieur a zero" + quantity);
    }

}
