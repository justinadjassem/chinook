package net.devpull.chinook.catalog;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class CatalogStatsQuery {
    private static final String SQL = """
            SELECT
                (SELECT count(*) FROM artist) AS artists,
                (SELECT count(*) FROM album) AS albums,
                (SELECT count(*) FROM track) AS tracks,
                (SELECT count(*) FROM invoice) AS invoices
            """;

    private final JdbcClient jdbc;

    public CatalogStatsQuery(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    public CatalogStats fetch() {
        return jdbc.sql(SQL)
                .query(CatalogStats.class)
                .single();
    }
}
