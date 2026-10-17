<!-- docs/adr/001-socle-technique.md -->
# ADR 001 - Socle technique

Date : 2026-10-17 

## Contexte
Projet d'apprentissage de bout en bout (monolithe puis microservices) sur la base Chinook.
Il faut un socle reproductible sur n'importe quelle machine et proche de la production.

## Décision
- Java 21 (LTS) : tout Java 17 + virtual threads, record patterns, pattern matching pour switch.
- Spring Boot <version relevée à l'étape 2>, généré par start.spring.io.
- Maven + Maven Wrapper : version de Maven figée dans le dépôt.
- PostgreSQL 16 dans Docker Compose, démarré par spring-boot-docker-compose.
- Flyway pour le schéma et les données, `V1` schéma / `V2` données.
- JDBC (JdbcClient) avant JPA : on introduit l'ORM en phase 4 seulement.

## Conséquences
- `docker` est requis pour lancer l'application et les tests.
- Raccourci assumé : les tests utilisent Compose (`skip.in-tests: false`) ; remplacé par Testcontainers en phase 6.
- Aucune modification d'une migration appliquée : toute évolution = nouvelle migration.

## Alternatives écartées
- `ddl-auto=update` : non versionné, non reproductible, dangereux en production.
- H2 en mémoire : SQL différent de PostgreSQL, masque des erreurs.
- Gradle : équivalent ; Maven retenu car plus répandu dans les offres et entretiens Spring.