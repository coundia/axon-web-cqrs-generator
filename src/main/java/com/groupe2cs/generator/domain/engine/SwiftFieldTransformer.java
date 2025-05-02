package com.groupe2cs.generator.domain.engine;

import com.groupe2cs.generator.domain.model.FieldDefinition;
import java.util.*;

public class SwiftFieldTransformer {

	public static List<Map<String, Object>> transform(List<FieldDefinition> fields, String entityName) {
		List<Map<String, Object>> result = new ArrayList<>();

		for (FieldDefinition field : fields) {
			Map<String, Object> f = new HashMap<>();

			String swiftType = toSwiftType(field.getType());

			f.put("name", field.getName());
			f.put("realType", swiftType);
			f.put("nullable", field.getNullable());

			f.put("defaultValue", getDefaultValue(swiftType, field.getNullable()));
			f.put("isDisplayName", field.getName().equalsIgnoreCase("name"));
			f.put("isIcon", field.getName().equalsIgnoreCase("icon"));
			f.put("isUpdatedAt", field.getName().equalsIgnoreCase("updatedAt"));
			f.put("isParentRelation", field.getName().toLowerCase().contains("parent"));
			f.put("isTypeIndicator", field.getName().equalsIgnoreCase("typeCategory"));
			f.put("isDefaultIndicator", field.getName().equalsIgnoreCase("isDefault"));
			f.put("isId", field.isId());

			// Type UI helpers
			f.put("isText", swiftType.equals("String"));
			f.put("isDouble", swiftType.equals("Double"));
			f.put("isInt", swiftType.equals("Int") || swiftType.equals("Int64"));
			f.put("isBool", swiftType.equals("Bool"));
			f.put("isDate", swiftType.equals("Date"));

			result.add(f);
		}

		return result;
	}

	private static String toSwiftType(String type) {
		return switch (type.toLowerCase()) {
			case "string", "uuid" -> "String";
			case "int", "integer" -> "Int";
			case "long" -> "Int64";
			case "double" -> "Double";
			case "boolean" -> "Bool";
			case "java.time.instant", "java.time.localdatetime", "date" -> "Date";
			default -> type;
		};
	}

	private static String getDefaultValue(String swiftType, Boolean nullable) {
		if (Boolean.TRUE.equals(nullable)) return "nil";

		return switch (swiftType) {
			case "String" -> "\"\"";
			case "Int", "Int64" -> "0";
			case "Double" -> "0.0";
			case "Bool" -> "false";
			case "Date" -> "Date()";
			default -> "nil";
		};
	}
}
