package net.devpull.chinook.sales.domain;

import java.util.List;
import java.util.Objects;

public record OrderRequest(CustomerId customerId, List<OrderItem> items) {
    public OrderRequest {
        Objects.requireNonNull(customerId, "customerId");
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Une commande contient au moins une piste");
        }
        items = List.copyOf(items);
    }
}
