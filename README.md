# Axon Web Code Generator (CQRS + DDD)

Ce projet est un générateur de code backend basé sur **Spring Boot**, **Axon Framework**, **RabbitMQ**, **PostgreSQL** et **JPA**. Il suit une architecture **CQRS + DDD**, avec génération automatique des fichiers à partir de définitions d'entités.

## ⚙️ Stack Technique

- Spring Boot
- Spring WebMVC
- Spring Data JPA
- Axon Framework (Command/Query/Event)
- RabbitMQ
- PostgreSQL
- Mustache Templates
- Swagger / OpenAPI
- Java 17+

## 🧠 Architecture

- `domain/`: logique métier, agrégats, events, value objects
- `application/`: services, DTOs, command/query handlers
- `infrastructure/`: persistance (JPA), messaging (RabbitMQ)
- `presentation/`: API REST (commandes & queries)
- `templates/`: fichiers mustache pour la génération de code
- `shared/`: utilitaires

## 🚀 Lancement

```bash

./mvnw spring-boot:run

  
```
## usage 

```http


POST http://127.0.0.1:8071/api/v1/generator/all
Accept: application/x-ndjson
Content-Type: application/json

{
  "outputDir": "/Users/pcoundia/projects/spring-axon-rabbitmq-web-jpa-starter/src/main/java/com/groupe2cs/bizyhub/products",
  "definition": {
	"name": "Product",
	"table": "products",
	"fields": [
	  { "name": "id", "type": "String" },
	  { "name": "name", "type": "String" },
	  { "name": "price", "type": "Double" },
	  { "name": "sales", "type": "List<com.groupe2cs.bizyhub.sales.infrastructure.entity.Sale>", "relation":"oneToMany" }
	]
  }
}

###

POST http://127.0.0.1:8071/api/v1/generator/all
Accept: application/x-ndjson
Content-Type: application/json

{
    "outputDir": "/Users/pcoundia/projects/spring-axon-rabbitmq-web-jpa-starter/src/main/java/com/groupe2cs/bizyhub/sales",
    "definition": {
      "name": "Sale",
      "table": "sales",
      "fields": [
        { "name": "id", "type": "String" },
        { "name": "quantity", "type": "Integer" },
        { "name": "total_price", "type": "Double" },
        { "name": "facture", "type": "String" },
        { "name": "Product", "type": "com.groupe2cs.bizyhub.products.infrastructure.entity.Product", "relation":"manyToOne" }
      ]
    }
  }

###


###
``` 
Voir le projet geneér pour plus de détails  

Entite (Sale)


[https://github.com/coundia/spring-axon-rabbitmq-web-jpa-starter](https://github.com/coundia/spring-axon-rabbitmq-web-jpa-starter)