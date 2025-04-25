package com.groupe2cs.generator.domain.engine;

import com.groupe2cs.generator.domain.model.FieldDefinition;
import com.groupe2cs.generator.shared.Utils;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class FieldTransformer {

    public static List<Map<String, Object>> transform(List<FieldDefinition> fields, String entityName) {
        List<Map<String, Object>> result = new ArrayList<>();

        for (int i = 0; i < fields.size(); i++) {
            FieldDefinition field = fields.get(i);
            Map<String, Object> f = new HashMap<>();

            f.put("name", field.getName());
            f.put("nameCapitalized", Utils.capitalize(field.getName()));
            f.put("nameUnCapitalized", Utils.unCapitalize(field.getName()));
            f.put("nameLowerCase", Utils.unCapitalize(field.getName()));
            f.put("nameCamelCase", Utils.camelCase(field.getName()));
            f.put("type", entityName + Utils.capitalize(field.getName()));
            f.put("realType", field.getType());
            f.put("isId", field.getName().equalsIgnoreCase("id"));
            f.put("last", i == fields.size() - 1);
            f.put("isPrimitiveType", field.isPrimitiveType());
            f.put("primitiveType", field.getPrimitiveType());
            f.put("isFileType", field.isFileType());

            f.put("testValue", getTestValue(field));
            f.put("testDomainValue", getTestDomainValue(field));
            f.put("primitiveValue", getPrimitiveValue(field));

            f.put("hasValidation", true);
            f.put("errorType", "IllegalArgumentException");
            f.put("errorTestValue", getErrorTestValue(field));
            f.put("errorMessage", Utils.capitalize(field.getName()) + " is invalid");
            String exceptionName = entityName + Utils.capitalize(field.getName()) + "NotValid";
            f.put("exceptionName", exceptionName);

            f.put("relation", field.getRelation());

            f.put("isOneToMany", "oneToMany".equalsIgnoreCase(field.getRelation()));
            f.put("isManyToOne", "manyToOne".equalsIgnoreCase(field.getRelation()));

            f.put("unique", field.getUnique());

            f.put("nullable", field.getNullable());
            f.put("isUnique", field.getUnique() || field.isId());

            f.put("size", field.getSize());

            result.add(f);
        }

        return result;
    }

    private static String getTestValue(FieldDefinition field) {
        String primitive = Optional.ofNullable(field.getPrimitiveType()).orElse("").toLowerCase();
        String type = field.getType().toLowerCase();

        if (type.contains("price") || type.contains("amount")) {
            double value = Math.round(ThreadLocalRandom.current().nextDouble(10.0, 1000.0) * 100.0) / 100.0;
            return String.valueOf(value);
        }

        if (type.contains("name") || type.contains("title") || type.contains("facture")) {
            return "\"TestValue" + ThreadLocalRandom.current().nextInt(1000) + "\"";
        }

        if (type.contains("id")) {
            return "\"" + UUID.randomUUID().toString() + "\"";
        }
        if (type.contains("date")) {
            return "java.time.LocalDateTime.now()";
        }
        if(type.contains("instant")) {
            return "java.time.Instant.now().plusSeconds(3600)";
        }

        if(type.contains("url")) {
            return "\"https://example.com/test" + ThreadLocalRandom.current().nextInt(1000) + ".jpg\"";
        }

        if (field.getRelation()!= null && field.getRelation().equalsIgnoreCase("manyToOne")) {

            String typeField = field.getType();

            if(typeField.equalsIgnoreCase("CustomUser")) {
                return "UserFixtures.randomOneViaCommand(commandGateway, userId).getId().value()";
            }

            return typeField+"Fixtures.randomOneViaCommand(commandGateway, userId).getId().value()";
        }

        if(field.getRelation()!= null && field.getRelation().equalsIgnoreCase("oneToMany")) {
            return "null";
        }

        if(type.contains("email")) {
            return "\"test" + ThreadLocalRandom.current().nextInt(1000) + "@example.com\"";
        }

        return switch (primitive) {
            case "int", "integer" -> String.valueOf(ThreadLocalRandom.current().nextInt(1, 100));
            case "long" -> ThreadLocalRandom.current().nextLong(1000, 99999) + "L";
            case "double" -> {
                double d = Math.round(ThreadLocalRandom.current().nextDouble(10.0, 10000.0) * 100.0) / 100.0;
                yield String.valueOf(d);
            }
            case "boolean" -> String.valueOf(ThreadLocalRandom.current().nextBoolean());
            default -> "UUID.randomUUID().toString()";
        };
    }

    private static String getTestDomainValue(FieldDefinition field) {
        String primitive = Optional.ofNullable(field.getPrimitiveType()).orElse("").toLowerCase();
        return switch (primitive) {
            case "int", "integer" -> String.valueOf(ThreadLocalRandom.current().nextInt(1, 100));
            case "long" -> ThreadLocalRandom.current().nextLong(1000, 99999) + "L";
            case "double" -> {
                double d = Math.round(ThreadLocalRandom.current().nextDouble(10.0, 10000.0) * 100.0) / 100.0;
                yield String.valueOf(d);
            }
            case "boolean" -> String.valueOf(ThreadLocalRandom.current().nextBoolean());
            case "java.time.instant" -> "java.time.Instant.now().plusSeconds(3600)";
            case "java.time.localdatetime" -> "java.time.LocalDateTime.now()";
            default -> "UUID.randomUUID().toString()";
        };
    }

    private static String getPrimitiveValue(FieldDefinition field) {
        String primitive = Optional.ofNullable(field.getPrimitiveType()).orElse("").toLowerCase();
        return switch (primitive) {
            case "int", "integer" -> String.valueOf(ThreadLocalRandom.current().nextInt(1, 100));
            case "long" -> ThreadLocalRandom.current().nextLong(1000, 99999) + "L";
            case "double" -> {
                double d = Math.round(ThreadLocalRandom.current().nextDouble(10.0, 10000.0) * 100.0) / 100.0;
                yield String.valueOf(d);
            }
            case "boolean" -> String.valueOf(ThreadLocalRandom.current().nextBoolean());
            case "java.time.instant" -> "java.time.Instant.now().plusSeconds(3600)";
            case "java.time.localdatetime" -> "java.time.LocalDateTime.now()";
            default ->   UUID.randomUUID().toString();
        };
    }


    private static String getErrorTestValue(FieldDefinition field) {
        String primitive = Optional.ofNullable(field.getPrimitiveType()).orElse("").toLowerCase();
        return switch (primitive) {
            case "int", "integer" -> "-1";
            case "long" -> "-999L";
            case "string" -> "\"\"";
            default -> "null";
        };
    }
}
