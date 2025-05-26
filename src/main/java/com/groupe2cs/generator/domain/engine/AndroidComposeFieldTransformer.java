package com.groupe2cs.generator.domain.engine;

import com.groupe2cs.generator.domain.model.FieldDefinition;
import com.groupe2cs.generator.shared.Utils;

import java.util.*;

public class AndroidComposeFieldTransformer {

	public static List<Map<String, Object>> transform(List<FieldDefinition> fields, String entityName) {
		List<Map<String, Object>> result = new ArrayList<>();

		for (FieldDefinition field : fields) {
			Map<String, Object> f = new HashMap<>();

			String name = field.getName();
			String kotlinType = toKotlinType(field.getType(), field.getNullable());
			boolean isNullable = Boolean.TRUE.equals(field.getNullable());

			f.put("name", name);
			f.put("nameLowerCase", Utils.unCapitalize(name));
			f.put("nameCapitalized", Utils.capitalize(name));
			f.put("type", kotlinType);
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

			result.add(f);
		}

		return result;
	}

	private static String toKotlinType(String type, Boolean nullable) {
		String baseType = switch (type.toLowerCase()) {
			case "string", "uuid" -> "String";
			case "int", "integer" -> "Int";
			case "long" -> "Long";
			case "double" -> "Double";
			case "float" -> "Float";
			case "boolean" -> "Boolean";
			case "date", "java.time.instant", "java.time.localdatetime" -> "String";
			default -> "Any";
		};
		return nullable != null && nullable ? baseType + "?" : baseType;
	}

	private static String getDefaultValue(FieldDefinition field) {
		if (field.getNullable() != null && field.getNullable()) return "null";
		String kotlinType = toKotlinType(field.getType(), field.getNullable());

		if (field.getDefaultValue() != null) {
			return field.getDefaultValue();
		}

		return switch (kotlinType.replace("?", "")) {
			case "String" -> "\"\"";
			case "Int", "Long" -> "0";
			case "Double", "Float" -> "0.0";
			case "Boolean" -> "false";
			default -> "null";
		};
	}
}
