# Chinook

![CI](https://github.com/justinadjassem/chinook/actions/workflows/ci.yml/badge.svg)

Application de révision Java 17/21 → microservices & Kafka, sur la base Chinook.

## Prérequis
- JDK 21
- Docker

## Démarrer
```bash
./mvnw spring-boot:run

curl localhost:8080/api/catalog/stats
```

## Conventions de commit
feat · fix · chore · docs · test · refactor - ex.
`feat(catalog): endpoint stats`
