package com.groupe2cs.generator.domain.model;

import com.groupe2cs.generator.shared.Utils;
import lombok.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FieldDefinition implements Serializable {
	private String type;
	private String name;
	private String nameCapitalized;
	private String nameCamelCase;
	private Boolean isId;
	private String relation;
	private Integer size = 250;
	private Boolean unique;
	private Boolean nullable;
	private String nameJpa;
	private String columnDefinition;
	private String defaultValue;
	private Boolean readOnly = false;
	private Boolean IsLowerCase = false;
	private Boolean focus = false;
	private Set<String> indicators = new HashSet<>();
	private String displayName;
	private String confidentiel;
	private String entityType;
	private String inputType;
	private Boolean isFiles = false;

	public String getEntityType() {

		if (entityType == null) {
			return type;
		}

		return entityType;
	}

	public Boolean getIsFiles() {

		if (isFiles == null) {
			return false;
		}

		return isFiles;
	}

	public String getInputType() {

		if (inputType == null) {
			return type;
		}

		return inputType;
	}

	public String getDisplayName() {
		if (displayName == null) {
			return null;
		}

		return Utils.capitalize(displayName);
	}

	public String getLabel() {
		if (displayName == null) {
			return Utils.capitalize(name);
		}

		return Utils.capitalize(displayName);
	}

	public FieldDefinition(String type, String name) {
		this.type = type;
		this.name = name;
		this.nameCapitalized = this.name.substring(0, 1).toUpperCase() + this.name.substring(1);
		this.isId = this.isId();
	}

	public Boolean getReadOnly() {

		if (name.equalsIgnoreCase("createdAt") ||
				name.equalsIgnoreCase("id") ||
				name.equalsIgnoreCase("updatedAt") ||
				name.equalsIgnoreCase("createdBy") ||
				name.equalsIgnoreCase("updatedBy") ||
				name.equalsIgnoreCase("deletedBy") ||
				name.equalsIgnoreCase("reference") ||
				name.equalsIgnoreCase("deletedAt")) {
			return true;
		}

		if (readOnly == null) {
			return false;
		}

		return readOnly;
	}

	public Set<String> getIndicators() {
		if (indicators == null) {
			return new HashSet<>();
		}
		return indicators;
	}

	public Boolean getUnique() {

		if (unique == null) {
			return false;
		}

		return unique;
	}

	public Boolean getNullable() {

		if (nullable == null) {
			return false;
		}

		return nullable;
	}

	public String getType() {


		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNameCapitalized() {
		return nameCapitalized;
	}

	public void setNameCapitalized(String nameCapitalized) {
		this.nameCapitalized = nameCapitalized;
	}

	public String getNameCamelCase() {
		return nameCamelCase;
	}

	public void setNameCamelCase(String nameCamelCase) {
		this.nameCamelCase = nameCamelCase;
	}

	public Boolean getId() {
		return isId;
	}

	public void setId(Boolean id) {
		isId = id;
	}

	public String getRelation() {
		return relation;
	}

	public void setRelation(String relation) {
		this.relation = relation;
	}

	public boolean isDate() {

		return type.equalsIgnoreCase("date") ||
				type.equalsIgnoreCase("datetime")
				;
	}

	public boolean isFilable() {

		return name.contains("id") ||
				name.contains("name") ||
				name.contains("label")
				;
	}

	public boolean isId() {
		return name.equalsIgnoreCase("id");
	}

	public boolean isUnique() {

		if (isId()) {
			return true;
		}
		if (unique == null) {
			return false;
		}

		return unique;
	}

	public boolean isFileType() {
		return
				type.equalsIgnoreCase("MultipartFile") ||
						type.equalsIgnoreCase("FileManager") ||
						type.equalsIgnoreCase("PartFile") ||
						type.equalsIgnoreCase("files") ||
						type.equalsIgnoreCase("image") ||
						type.equalsIgnoreCase("pdf") ||
						type.equalsIgnoreCase("file")
				;
	}

	public String getPrimitiveType() {
		if (!isPrimitiveType()) {
			return "String";
		}

		return type;
	}

	public boolean isPrimitiveType() {
		if (isFileType()) {
			return false;
		}
		String lower = type.toLowerCase();
		return switch (lower) {
			case "string",
				 "int", "integer", "long", "double",
				 "float", "boolean", "short", "char",
				 "byte", "bigdecimal", "bigint",
				 "instant", "localdatetime", "localdate",
				 "java.time.instant", "java.util.date", "java.time.localdate", "java.time.localdatetime" -> true;
			default -> false;
		};
	}

	@Override
	public String toString() {
		return "FieldDefinition{" +
				"type='" + type + '\'' +
				", name='" + name + '\'' +
				", nameCapitalized='" + nameCapitalized + '\'' +
				", nameCamelCase='" + nameCamelCase + '\'' +
				", isId=" + isId +
				", relation='" + relation + '\'' +
				'}';
	}


	public Boolean isManyToOne() {
		if (relation == null) {
			return false;
		}
		return this.getRelation().toLowerCase().contains("manytoone");
	}


	public String getRepository() {
		if (type == null || name == null) return null;

		if (!type.contains(".")) {
			return type + "Repository" + " " + Utils.unCapitalize(name) + "DataRepository";
		}

		String entityClass = type.substring(type.lastIndexOf('.') + 1);
		String repositoryPackage = type.substring(0, type.lastIndexOf('.'))
				.replace(".entity", ".repository");

		return repositoryPackage + "." + entityClass + "Repository" + " " + Utils.unCapitalize(name) + "DataRepository";
	}


	public Boolean hasRepository() {
		return !this.isPrimitiveType()
				;
	}

}
