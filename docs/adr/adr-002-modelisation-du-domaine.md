<!-- docs/adr/002-modelisation-du-domaine.md -->
# ADR 002 — Modélisation du domaine en Java pur

## Contexte
Les règles de vente (prix, total, paiement) doivent être testables sans base ni Spring,
et survivre aux changements de framework des phases suivantes.

## Décision
- Value objects en records (Money, InvoiceLine, identifiants), validés dans le constructeur compact.
- Montants en BigDecimal à 2 décimales (HALF_UP), jamais en double.
- Entités en classes, égalité sur l'identifiant (Invoice).
- Collections copiées avec List.copyOf à la construction.
- Issues fermées modélisées par sealed interface (PaymentResult) ; formats par enum à switch exhaustif.

## Conséquences
- Le domaine n'importe que java.* ; ses tests tournent en millisecondes.
- Il faudra mapper ces objets vers la persistance (phase 4) : c'est le coût assumé.
- Montants négatifs refusés : un remboursement sera modélisé à part, pas par un Money négatif.