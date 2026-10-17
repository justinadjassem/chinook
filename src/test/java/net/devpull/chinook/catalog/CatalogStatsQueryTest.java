package net.devpull.chinook.catalog;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CatalogStatsQueryTest {

    @Autowired
    CatalogStatsQuery query;

    @Test
    void les_donnees_chinook_sont_chargees_par_flyway(){
       CatalogStats stats = query.fetch();

       assertThat(stats.artists()).isEqualTo(275);
       assertThat(stats.albums()).isEqualTo(347);
       assertThat(stats.tracks()).isEqualTo(3503);
       assertThat(stats.invoices()).isEqualTo(412);
    }
}
