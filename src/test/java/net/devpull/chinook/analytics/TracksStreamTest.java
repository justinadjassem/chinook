package net.devpull.chinook.analytics;

import net.devpull.chinook.catalog.domain.MediaType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.devpull.chinook.analytics.ChinookDataset.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class TracksStreamTest {

    @Test
    void un_stream_est_paresseux(){
        var examinees = new AtomicInteger();

        Optional<Rows.TrackRow> premiere = TRACKS.stream()
                .peek(t -> examinees.incrementAndGet())
                .filter(t -> t.milliseconds() > 300_000)
                .findFirst();

        assertThat(premiere).isPresent();
        assertThat(examinees.get()).isLessThan(TRACKS.size());
    }

    @Test
    void un_stream_ne_se_consomme_qu_une_fois() {
        Stream<Rows.TrackRow> stream = TRACKS.stream();
        stream.count();
        assertThatThrownBy(stream::count).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void dix_pistes_rock_les_plus_longues() {
        long rockId = GENRES.stream()
                .filter(g -> g.name().equals("Rock"))
                .map(Rows.GenreRow::id)
                .findFirst()
                .orElseThrow();

        List<Rows.TrackRow> rock = new ArrayList<>();
        for (Rows.TrackRow t : TRACKS) {
            if (t.genreId() != null && t.genreId() == rockId) {
               rock.add(t);
            }
        }
        rock.sort(Comparator.comparingInt(Rows.TrackRow::milliseconds).reversed());
        List<String> parBoucle = new ArrayList<>();
        for (int i = 0; i < Math.min(rock.size(), 10); i++) {
            parBoucle.add(rock.get(i).name());
        }

        List<String> parStream = TRACKS.stream()
                .filter(t -> Objects.equals(t.genreId(), rockId))
                .sorted(Comparator.comparingInt(Rows.TrackRow::milliseconds).reversed())
                .limit(10)
                .map(Rows.TrackRow::name)
                .toList();
        assertThat(parStream).hasSize(10).isEqualTo(parBoucle);
    }

    @Test
    void pist_la_plus_longue() {
        String nom = TRACKS.stream()
                .max(Comparator.comparingInt(Rows.TrackRow::milliseconds))
                .map(Rows.TrackRow::name)
                .orElse("aucune piste");

        System.out.println("Piste la plus longue: " + nom);
        assertThat(nom).isNotEqualTo("aucune piste");
    }

    @Test
    void top_10_artistes_par_nombre_de_pistes(){
        Map<Long, Long> artisteParAlbum = ALBUMS.stream()
                .collect(Collectors.toMap(Rows.AlbumRow::id, Rows.AlbumRow::artistId));
        Map<Long, String> nomArtiste = ARTISTS.stream()
                .collect(Collectors.toMap(Rows.ArtistRow::id, Rows.ArtistRow::name));

        Map<String, Long> compteBoucle = new HashMap<>();
        for (Rows.TrackRow t : TRACKS) {
            if (t.albumId() == null) continue;
            String artiste = nomArtiste.get(artisteParAlbum.get(t.albumId()));
            compteBoucle.merge(artiste, 1L, Long::sum);
        }

        Map<String, Long> compteStream = TRACKS.stream()
                .filter(t-> t.albumId() != null)
                .collect(Collectors.groupingBy(
                        t -> nomArtiste.get(artisteParAlbum.get(t.albumId())),
                        Collectors.counting()
                ));
        assertThat(compteStream).isEqualTo(compteBoucle);

        List<Map.Entry<String, Long>> top10 = compteStream.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(10)
                .toList();

        top10.forEach(e -> System.out.println(e.getKey() + ": " + e.getValue()));
        assertThat(top10).hasSize(10);
    }

    void toMap_sans_fusion_echoue_sur_une_cle_dupliquee() {
        assertThatThrownBy(() -> ARTISTS.stream()
                .collect(Collectors.toMap(a -> a.name().substring(0, 1), Rows.ArtistRow::name)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Duplicate key");

        Map<String, Long> artistesParInitiale = ARTISTS.stream()
                .collect(Collectors.toMap(a -> a.name().substring(0, 1), a -> 1L, Long::sum));

        assertThat(artistesParInitiale.values().stream().mapToLong(Long::longValue).sum()).isEqualTo(275);
    }

    @Test
    void chiffre_d_affaires_par_pays() {
        Map<String, BigDecimal> caParPays = INVOICES.stream()
                .collect(Collectors.groupingBy(
                        Rows.InvoiceRow::billingCountry,
                        Collectors.reducing(BigDecimal.ZERO, Rows.InvoiceRow::total, BigDecimal::add)
                ));

        LinkedHashMap<String, BigDecimal> trie = caParPays.entrySet().stream()
                .sorted(Map.Entry.<String, BigDecimal> comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey, Map.Entry::getValue,
                        (a, b) -> a,
                        LinkedHashMap::new
                ));
        trie.entrySet().stream().limit(5).forEach(System.out::println);
        assertThat(trie.keySet()).first().isEqualTo("USA");
    }

    @Test
    void le_total_de_chaque_facture_egale_la_somme_de_ses_lignes() {
        Map<Long, List<Rows.InvoiceLineRow>> lignesParFacture = INVOICE_LINES.stream()
                .collect(Collectors.groupingBy(Rows.InvoiceLineRow::invoiceId));

        List<Long> incoherentes = INVOICES.stream()
                .filter(inv -> {
                    BigDecimal somme = lignesParFacture.getOrDefault(inv.id(), List.of()).stream()
                            .map(l -> l.unitPrice().multiply(BigDecimal.valueOf(l.quantity())))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    return somme.compareTo(inv.total()) != 0;
                })
                .map(Rows.InvoiceRow::id)
                .toList();

        assertThat(incoherentes).isEmpty();

        // flatMap : toutes les pistes achetées par le client 1, sur toutes ses factures
        int pistesClient1 = INVOICES.stream()
                .filter(inv -> inv.customerId() == 1)
                .flatMap(inv -> lignesParFacture.getOrDefault(inv.id(), List.of()).stream())
                .mapToInt(Rows.InvoiceLineRow::quantity)
                .sum();
        System.out.println("Pistes achetées par le client 1 : " + pistesClient1);
    }

    @Test
    void genre_le_plus_vendu_par_pays() {
        Map<Long, String> paysParFacture = INVOICES.stream()
                .collect(Collectors.toMap(Rows.InvoiceRow::id, Rows.InvoiceRow::billingCountry));
        Map<Long, Long> genreParPiste = TRACKS.stream()
                .filter(t -> t.genreId() != null)
                .collect(Collectors.toMap(Rows.TrackRow::id, Rows.TrackRow::genreId));
        Map<Long, String> nomGenre = GENRES.stream()
                .collect(Collectors.toMap(Rows.GenreRow::id, Rows.GenreRow::name));

        // pays -> (genre -> quantité vendue)
        Map<String, Map<Long, Integer>> quantites = INVOICE_LINES.stream()
                .filter(l -> genreParPiste.containsKey(l.trackId()))
                .collect(Collectors.groupingBy(
                        l -> paysParFacture.get(l.invoiceId()),
                        TreeMap::new,
                        Collectors.groupingBy(
                                l -> genreParPiste.get(l.trackId()),
                                Collectors.summingInt(Rows.InvoiceLineRow::quantity))));

        Map<String, String> genrePrefere = quantites.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> nomGenre.get(Collections.max(e.getValue().entrySet(), Map.Entry.comparingByValue()).getKey()),
                        (a, b) -> a,
                        TreeMap::new));

        genrePrefere.forEach((pays, genre) -> System.out.println(pays + " -> " + genre));
        assertThat(genrePrefere).containsKey("USA");
    }

    @Test
    void pistes_video_et_audio() {
        Map<Boolean, Long> parFormat = TRACKS.stream()
                .collect(Collectors.partitioningBy(
                        t -> {
                            int id = t.mediaTypeId();
                            MediaType type = MediaType.fromId(id);
                            return type.isVideo();
                        },
                        Collectors.counting()));

        System.out.println("Vidéo : " + parFormat.get(true) + ", audio : " + parFormat.get(false));
        assertThat(parFormat.get(true) + parFormat.get(false)).isEqualTo(TRACKS.size());
    }

    record Fourchette(BigDecimal min, BigDecimal max) { }

    @Test
    void fourchette_de_prix_en_un_passage() {
        Fourchette f = TRACKS.stream()
                .map(Rows.TrackRow::unitPrice)
                .collect(Collectors.teeing(
                        Collectors.minBy(Comparator.<BigDecimal>naturalOrder()),
                        Collectors.maxBy(Comparator.<BigDecimal>naturalOrder()),
                        (min, max) -> new Fourchette(min.orElseThrow(), max.orElseThrow())));

        assertThat(f.min()).isEqualByComparingTo("0.99");
        assertThat(f.max()).isEqualByComparingTo("1.99");
    }

    @Test
    void duree_totale_par_album() {
        Map<Long, String> titreAlbum = ALBUMS.stream()
                .collect(Collectors.toMap(Rows.AlbumRow::id, Rows.AlbumRow::title));

        Map<String, Duration> dureeParAlbum = TRACKS.stream()
                .filter(t -> t.albumId() != null)
                .collect(Collectors.groupingBy(
                        t -> titreAlbum.get(t.albumId()),
                        Collectors.collectingAndThen(
                                Collectors.summingLong(Rows.TrackRow::milliseconds),
                                Duration::ofMillis)));

        dureeParAlbum.entrySet().stream()
                .sorted(Map.Entry.<String, Duration>comparingByValue().reversed())
                .limit(3)
                .forEach(e -> System.out.printf("%s : %d:%02d:%02d%n", e.getKey(),
                        e.getValue().toHours(), e.getValue().toMinutesPart(), e.getValue().toSecondsPart()));

        assertThat(dureeParAlbum).isNotEmpty();
    }
}


