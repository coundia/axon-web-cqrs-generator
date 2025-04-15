package com.groupe2cs.generator.domain.engine;

import com.groupe2cs.generator.domain.model.FieldDefinition;
import com.groupe2cs.generator.shared.Utils;

import java.util.*;

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
            f.put("errorMessage", Utils.capitalize(field.getName())+" is invalid");
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

        if (type.contains("price")) return "99.99";
        if (type.contains("name") || type.contains("facture")) return "\"TestValue\"";
        if (type.contains("id")) return "\"12345678-aaaa-bbbb-cccc-123456789abc\"";

        return switch (primitive) {
            case "int", "integer" -> "42";
            case "long" -> "9999L";
            case "double" -> "99.99";
            case "boolean" -> "true";
            case "string" -> "\"test\"";
            default -> "\"sample\"";
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
