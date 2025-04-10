package com.groupe2cs.generator.domain.engine;

import com.groupe2cs.generator.domain.model.FieldDefinition;
import com.groupe2cs.generator.shared.Utils;

import java.util.*;

public class FieldTransformer {

    public static List<Map<String, Object>> transform(List<FieldDefinition> fields, String entityName) {
        List<Map<String, Object>> result = new ArrayList<>();


        for (int i = 0; i < fields.size(); i++) {
            var field = fields.get(i);
            Map<String, Object> f = new HashMap<>();
            f.put("name", field.getName());
            f.put("nameCapitalized", Utils.capitalize(field.getName()));
            f.put("nameLowerCase", Utils.lowerCase(field.getName()));
            f.put("nameCamelCase", Utils.camelCase(field.getName()));
            f.put("type", entityName + Utils.capitalize(field.getName()));
            f.put("realType", field.getType());
            f.put("isId", field.getName().equalsIgnoreCase("id"));

            f.put("isString", field.getType().equalsIgnoreCase("String"));
            f.put("isInteger", field.getType().equalsIgnoreCase("Integer"));
            f.put("isDouble", field.getType().equalsIgnoreCase("Double"));
            f.put("last", i == fields.size() - 1);

            f.put("isPrimitiveType", field.isPrimitiveType());
            f.put("primitiveType", field.getPrimitiveType());
            f.put("isFileType", field.isFileType());
            result.add(f);
        }

        return result;
    }


}
