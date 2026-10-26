package net.devpull.chinook.analytics;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static net.devpull.chinook.analytics.ChinookDataset.*;

public class ChinookDataSetTest {
    @Test
    void le_jeu_de_donnees_est_complet() {
        assertThat(ARTISTS).hasSize(275);
        assertThat(ALBUMS).hasSize(347);
        assertThat(TRACKS).hasSize(3503);
        assertThat(INVOICES).hasSize(412);
        assertThat(INVOICE_LINES).hasSize(2240);
    }
}
