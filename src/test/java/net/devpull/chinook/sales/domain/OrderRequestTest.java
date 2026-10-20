package net.devpull.chinook.sales.domain;

import net.devpull.chinook.catalog.domain.TrackId;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

public class OrderRequestTest {

    @Test
    void la_demande_ne_change_pas_si_la_liste_d_origine_change(){
        var items = new ArrayList<>(List.of(new OrderItem(new TrackId(1L), 1)));
        var request = new OrderRequest(new CustomerId(1L), items);

        items.add(new OrderItem(new TrackId(2L), 1));

        assertThat(request.items()).hasSize(1);
    }
}
