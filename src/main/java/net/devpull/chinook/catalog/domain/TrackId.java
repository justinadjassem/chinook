package net.devpull.chinook.catalog.domain;

public record TrackId(Long value) {
    public TrackId{
        if (value <= 0) {
            throw new IllegalArgumentException("TrackId doit etre positif : " + value);
        }
    }
}
