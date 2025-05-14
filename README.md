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

###

POST http://127.0.0.1:8071/api/v1/generator/all
Accept: application/x-ndjson
Content-Type: application/json

{
  "outputDir": "/Users/pcoundia/projects/spring-axon-rabbitmq-web-jpa-starter/src/main/java/com/groupe2cs/bizyhub/transactions",
  "definition": {
	"name": "Transaction",
	"table": "transactions",
	"fields": [
	  { "name": "id", "type": "String" },
	  { "name": "reference", "type": "String" },
	  { "name": "amount", "type": "Double" }
	],
    "stack": [
	  "RabbitMq",
      "Security"
    ]
  }
}

###
``` 
Voir le projet geneér pour plus de détails  

Entite (Sale)


[https://github.com/coundia/spring-axon-rabbitmq-web-jpa-starter](https://github.com/coundia/spring-axon-rabbitmq-web-jpa-starter)


⚠️ All rights reserved.

This repository is publicly visible for demonstration purposes only.
You may not use, copy, modify, or distribute any part of this code without explicit permission from the author.
