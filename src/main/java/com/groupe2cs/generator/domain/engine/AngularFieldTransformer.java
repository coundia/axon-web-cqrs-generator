package com.groupe2cs.generator.domain.engine;

import com.groupe2cs.generator.domain.model.FieldDefinition;
import com.groupe2cs.generator.shared.Utils;

import java.util.*;

public class AngularFieldTransformer {

	public static List<Map<String, Object>> transform(List<FieldDefinition> fields, String entityName) {
		List<Map<String, Object>> result = new ArrayList<>();

		for (FieldDefinition field : fields) {
			Map<String, Object> f = new HashMap<>();

			String name = field.getName();
			String tsType = toTsType(field.getType());
			boolean isNullable = Boolean.TRUE.equals(field.getNullable());

			f.put("name", name);
			f.put("nameLowerCase", Utils.unCapitalize(name));
			f.put("nameCapitalized", Utils.capitalize(name));
			f.put("type", tsType);
			f.put("nullable", isNullable);
			f.put("defaultValue", getDefaultValue(field));
			f.put("isId", field.isId());
			f.put("readOnly", field.getReadOnly());
			f.put("displayName", field.getDisplayName());
			f.put("label", field.getLabel());
			f.put("isManyToOne", field.isManyToOne());
			f.put("focus", field.getFocus());
			f.put("isLowerCase", field.getIsLowerCase());
			f.put("entityType", field.getEntityType());

			f.put("isDate", field.getType().equalsIgnoreCase("date") ||
					field.getEntityType().equalsIgnoreCase("date")
			);
			result.add(f);
		}

		return result;
	}

	private static String toTsType(String type) {
		return switch (type.toLowerCase()) {
			case "string", "uuid" -> "string";
			case "int", "integer", "long" -> "number";
			case "double", "float" -> "number";
			case "boolean" -> "boolean";
			case "date", "java.time.instant", "java.time.localdatetime" -> "string";
			default -> "any";
		};
	}

	private static String getDefaultValue(FieldDefinition field) {

 		String tsType = toTsType(field.getType());

		if (field.getDefaultValue() != null) {
			return field.getDefaultValue();
		}

		return switch (tsType) {
			case "number" -> "0";
			case "boolean" -> "false";
			default -> "\"\"";
		};
	}
}
