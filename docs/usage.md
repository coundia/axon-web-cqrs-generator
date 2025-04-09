cd cqrs-generator/

```sh
cd cqrs-generator/

mvn spring-boot:run

curl -N -X POST http://localhost:8070/api/v1/generator/all \
  -H "Content-Type: application/json" \
  -H "Accept: application/x-ndjson" \
  -d '{
    "outputDir": "target/generated",
    "definition": {
      "name": "Product",
      "table": "products",
      "fields": [
        { "name": "id", "type": "String" },
        { "name": "name", "type": "String" },
        { "name": "price", "type": "Double" }
      ]
    }
  }'

```
