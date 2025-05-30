package com.groupe2cs.generator.domain.engine;

import com.groupe2cs.generator.domain.model.FieldDefinition;
import com.groupe2cs.generator.shared.Utils;

import java.util.*;

public class SwiftFieldTransformer {

	public static List<Map<String, Object>> transform(List<FieldDefinition> fields, String entityName) {
		List<Map<String, Object>> result = new ArrayList<>();

		for (FieldDefinition field : fields) {
			Map<String, Object> f = new HashMap<>();

			String name = field.getName();
			String swiftType = toSwiftType(field.getType());
			boolean isNullable = Boolean.TRUE.equals(field.getNullable());

			f.put("name", name);
			f.put("nameLowerCase", Utils.unCapitalize(name));
			f.put("nameCapitalized", Utils.capitalize(name));
			f.put("realType", swiftType);
			f.put("nullable", isNullable);
			f.put("defaultValue", getDefaultValue(field));
			f.put("isId", field.isId());

			// UI helpers
			f.put("isDisplayName", field.getDisplayName() != null && !field.getDisplayName().isEmpty());
			f.put("isIcon", name.equalsIgnoreCase("icon"));
			f.put("isUpdatedAt", name.equalsIgnoreCase("updatedAt"));
			//f.put("isParentRelation", name.toLowerCase().contains("parent"));
			f.put("isTypeIndicator", name.toLowerCase().contains("type"));
			f.put("isDefaultIndicator", name.equalsIgnoreCase("isDefault"));

			f.put("isText", swiftType.equals("String"));
			f.put("isDouble", swiftType.equals("Double"));
			f.put("isInt", swiftType.equals("Int") || swiftType.equals("Int64"));
			f.put("isBool", swiftType.equals("Bool"));
			f.put("isDate", swiftType.equals("Date"));
			f.put("isEnum", isEnum(field));

			f.put("readOnly", field.getReadOnly());

			f.put("indicators", field.getIndicators());
			f.put("hasSum", field.getIndicators() != null && field.getIndicators().contains("sum"));

			f.put("nameEqualsReference", name.equals("reference"));

			f.put("displayName", field.getDisplayName());
			f.put("label", field.getLabel());
			f.put("isManyToOne", field.isManyToOne());

			f.put("focus",  field.getFocus());
			f.put("isLowerCase", field.getIsLowerCase());

			f.put("entityType", field.getEntityType());

			f.put("isFiles", field.getIsFiles());

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
			default -> capitalize(type); // assume it's a Swift enum or model
		};
	}

	private static boolean isEnum(FieldDefinition field) {
		return "enum".equalsIgnoreCase(field.getEntityType());
	}

	private static String getDefaultValue(FieldDefinition field) {
		if (field.getNullable()) return "nil";
		String swiftType = toSwiftType(field.getType());

		if(field.getDefaultValue() != null) {
			return field.getDefaultValue();
		}

		return switch (swiftType) {
			case "String" -> "\"\"";
			case "Int", "Int64" -> "0";
			case "Double" -> "0.0";
			case "Bool" -> "false";
			case "Date" -> "Date()";
			default -> swiftType + ".allCases.first!"; // default for enums
		};
	}

	private static String capitalize(String input) {
		if (input == null || input.isEmpty()) return input;
		return input.substring(0, 1).toUpperCase() + input.substring(1);
	}
}
