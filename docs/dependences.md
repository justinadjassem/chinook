# Dépendances

| Dépendance | Rôle | Alternative écartée | Phase |
| --- | --- | --- | --- |
| spring-boot-starter-web | API REST, Tomcat embarqué | WebFlux (réactif inutile ici) | 0 |
| spring-boot-starter-validation | Valider les entrées à la frontière | Validation manuelle | 0 |
| spring-boot-starter-jdbc | DataSource, JdbcClient | JPA (phase 4) | 0 |
| postgresql | Driver JDBC | H2 | 0 |
| flyway (+ module PostgreSQL) | Migrations versionnées | ddl-auto | 0 |
| spring-boot-docker-compose | Démarre PostgreSQL au lancement | Lancer la base à la main | 0 |
| springdoc-openapi-starter-webmvc-ui | Swagger UI, contrat OpenAPI | Postman seul | 0 |
| spring-boot-starter-test | JUnit 5, AssertJ, Mockito | — | 0 |