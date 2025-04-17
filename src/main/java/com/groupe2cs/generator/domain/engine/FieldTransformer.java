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
            f.put("nameLowerCase", Utils.lowerCase(field.getName()));
            f.put("nameCamelCase", Utils.camelCase(field.getName()));
            f.put("type", entityName + Utils.capitalize(field.getName()));
            f.put("realType", field.getType());
            f.put("isId", field.getName().equalsIgnoreCase("id"));
            f.put("last", i == fields.size() - 1);
            f.put("isPrimitiveType", field.isPrimitiveType());
            f.put("primitiveType", field.getPrimitiveType());
            f.put("isFileType", field.isFileType());

            f.put("testValue", getTestValue(field));
            f.put("hasValidation", true);
            f.put("errorType", "IllegalArgumentException");
            f.put("errorTestValue", getErrorTestValue(field));
            f.put("errorMessage", Utils.capitalize(field.getName()) + " is invalid");
            String exceptionName = entityName + Utils.capitalize(field.getName()) + "NotValid";
            f.put("exceptionName", exceptionName);

            f.put("relation", field.getRelation());

            f.put("isOneToMany", "oneToMany".equalsIgnoreCase(field.getRelation()));
            f.put("isManyToOne", "manyToOne".equalsIgnoreCase(field.getRelation()));

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

        if(field.getRelation()!=null) {
            return "null";
        }

        return switch (primitive) {
            case "int", "integer" -> String.valueOf(ThreadLocalRandom.current().nextInt(1, 100));
            case "long" -> ThreadLocalRandom.current().nextLong(1000, 99999) + "L";
            case "double" -> {
                double d = Math.round(ThreadLocalRandom.current().nextDouble(10.0, 10000.0) * 100.0) / 100.0;
                yield String.valueOf(d);
            }
            case "boolean" -> String.valueOf(ThreadLocalRandom.current().nextBoolean());
            case "string" -> "UUID.randomUUID().toString()";
            default -> "null";
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
