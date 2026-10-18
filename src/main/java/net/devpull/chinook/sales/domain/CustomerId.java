package net.devpull.chinook.sales.domain;

public record CustomerId(Long value) {

    public CustomerId {
        if (value <= 0) {
            throw new IllegalArgumentException("CustomerId doit etre positif : " + value);
        }
    }
}
