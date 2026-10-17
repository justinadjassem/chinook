package net.devpull.chinook.catalog;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/catalog")
public class CatalogStatsController {

    private final CatalogStatsQuery query;

    CatalogStatsController(CatalogStatsQuery query) {
        this.query = query;
    }

    @GetMapping("/stats")
    CatalogStats stats() {
        return query.fetch();
    }
}
