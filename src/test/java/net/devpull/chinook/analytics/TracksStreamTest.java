package net.devpull.chinook.analytics;

import org.junit.jupiter.api.Test;

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
}


