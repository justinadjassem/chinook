package net.devpull.chinook.catalog.domain;

import net.devpull.chinook.shared.domain.Money;

import java.util.Arrays;

public enum MediaType {
    MPEG_AUDIO(1),
    PROTECTED_AAC_AUDIO(2),
    PROTECTED_MPEG4_VIDEO(3),
    PURCHASED_AAC_AUDIO(4),
    AAC_AUDIO(5);

    private final int id;

    MediaType(int id) {
        this.id = id;
    }

    public int id() {
        return id;
    }

    public static MediaType fromId(int id) {
        return Arrays.stream(values())
                .filter(type -> type.id == id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("media_type_id inconnu : " + id));
    }

    public boolean isVideo() {
        return switch (this) {
            case PROTECTED_MPEG4_VIDEO -> true;
            case MPEG_AUDIO, PROTECTED_AAC_AUDIO, PURCHASED_AAC_AUDIO, AAC_AUDIO -> false;
        };
    }

    public Money standardPrice() {
        return isVideo() ? Money.usd("1.99") : Money.usd("0.99");
    }
}
