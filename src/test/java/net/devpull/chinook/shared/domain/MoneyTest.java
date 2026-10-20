package net.devpull.chinook.shared.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Currency;

import static org.assertj.core.api.Assertions.*;

public class MoneyTest {
    @Test
    void double_est_interdit_pour_l_argent() {
        assertThat(0.1 + 0.2).isNotEqualTo(0.3);
        assertThat(new BigDecimal("0.1").add(new BigDecimal("0.2"))).isEqualByComparingTo("0.3");
    }

    @Test
    void ddeux_montants_de_meme_valeur_sont_egaux_quelle_que_soit_l_echelle() {
        assertThat(Money.usd("0.99")).isEqualTo(Money.usd("0.990"));
    }

    @Test
    void dditionne_et_multiplie() {
        Money total = Money.usd("0.99").multiply(2).add(Money.usd("1.99"));
        assertThat(total).isEqualTo(Money.usd("3.97"));
    }

    @Test
    void refuse_un_montant_negatif(){
        assertThatThrownBy(()-> Money.usd("-1"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("negative");
    }

    @Test
    void refuse_d_additionner_deux_devises(){
        Money eur = new Money(BigDecimal.ONE, Currency.getInstance("EUR"));
        assertThatThrownBy(()-> Money.usd("1").add(eur))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
