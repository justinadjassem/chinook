package net.devpull.chinook.sales.domain;

public record InvoiceId(Long value) {

    public InvoiceId {
        if (value <= 0) {
            throw new IllegalArgumentException("InvoiceId doit etre positif : " + value);
        }
    }
}
