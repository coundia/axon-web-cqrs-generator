package com.groupe2cs.generator.domain.model;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.groupe2cs.generator.shared.Utils;
import lombok.*;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EntityDefinition implements Serializable {

	private String name;
	private String entity;
	private String table;
	private List<FieldDefinition> fields;
	private List<String> stack = new ArrayList<>();
	private List<String> skip = new ArrayList<>();
	private String module;
	private Boolean auditable = false;
	private Boolean multiTenant = false;
	private Boolean isGenerated = false;
	private String apiPrefix = "";
	private String defaultValue = "";
	private String title = "";
	private Boolean shared = false;
	private Boolean hasType = false;
	private Boolean hasSummary = false;
	private Boolean hasDate = false;
	private Boolean hasCategory = false;
	private Boolean transactional = false;
	private Boolean isPublic = false;
	private Boolean isAutoSave = true;
	private Boolean isPremium = false;
	private Boolean isChat = false;
	private Boolean hasFiles = false;
	private Boolean isFileManager = false;
	private String bind = "";
	private String header = "";
	private Boolean tauri = false;
	private String plural ;

	public String getPlural() {

		if( plural == null || plural.isEmpty()) {
			return Utils.pluralize(name);
		}
		return plural;
	}

	public  Boolean getTauri() {
		if (tauri == null) {
			return false;
		}

		return tauri;
	}

	public Boolean getIsFileManager(){
		if (isFileManager == null) {
			return false;
		}

		return isFileManager;
	}

	public Boolean getHasFiles(){
		if (hasFiles == null) {
			return false;
		}

		return hasFiles;
	}


	public String getApiPrefix() {
		if (apiPrefix == null) {
			return "";
		}

		return apiPrefix;
	}

	public Boolean getIsPublic() {
		if (isPublic == null) {
			return false;
		}

		return isPublic;
	}

	public String getTitle() {
		if (title == null || title.isEmpty()) {
			return Utils.capitalize(name);
		}

		return  Utils.capitalize(title);
	}

	public Boolean getMultiTenant() {

		if (multiTenant == null) {
			return false;
		}

		return multiTenant;
	}

	public String getName(){
		if (name == null) {
			return "";
		}

		return name;
	}

	public String getEntity() {
		if (entity == null) {
			return getName();
		}

		return entity;
	}

	public Boolean getIsGenerated() {

		if (isGenerated == null) {
			return false;
		}

		return isGenerated;
	}


	public String getTable() {
		return table;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public void setFields(List<FieldDefinition> fields) {
		this.fields = fields;
	}

	public List<FieldDefinition> getFields() {
		return fields.stream()
				.filter(f -> !"oneToMany".equalsIgnoreCase(f.getRelation()))
				.collect(Collectors.toList());
	}

	public List<FieldDefinition> getFieldsWithoutRelations() {
		return fields.stream()
				.filter(f -> !f.isFileType())
				.filter(f -> !"oneToMany".equalsIgnoreCase(f.getRelation()))
				.collect(Collectors.toList());
	}

	public List<FieldDefinition> getFieldsWithRelations() {
		return fields.stream()
				.filter(f -> !f.isFileType())
				.collect(Collectors.toList());
	}

	public List<FieldDefinition> getFieldsWithoutId() {
		return fields.stream()
				.filter(f -> !f.isId())
				.collect(Collectors.toList());
	}

	public List<FieldDefinition> getAllFields() {
		return fields;
	}

	public List<FieldDefinition> getAllFieldsWithoutOneToMany() {
		return fields.stream()
				.filter(f -> !"oneToMany".equalsIgnoreCase(f.getRelation()))
				.collect(Collectors.toList());

	}

	public EntityDefinition(String name, List<FieldDefinition> fields, String table) {
		this.name = name;
		this.fields = fields;
		this.table = table;
	}

	public EntityDefinition(String name, List<FieldDefinition> fields) {
		this.name = name;
		this.fields = fields;
	}

	public static EntityDefinition fromClassName(String className) {
		try {
			Class<?> clazz = Class.forName(className);
			String simpleName = clazz.getSimpleName();
			List<FieldDefinition> fields = List.of(clazz.getDeclaredFields()).stream()
					.map(f -> new FieldDefinition(f.getType().getSimpleName(), f.getName()))
					.collect(Collectors.toList());
			return new EntityDefinition(simpleName, fields);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Entity class not found: " + className);
		}
	}

	public static EntityDefinition fromSource(String className, String sourceRoot) {
		try {
			CompilationUnit cu = getCompilationUnit(className, sourceRoot);
			ClassOrInterfaceDeclaration clazz = cu.getClassByName(getSimpleName(className))
					.orElseThrow(() -> new RuntimeException("Class not found in file: " + className));

			List<FieldDefinition> fields = clazz.getFields().stream()
					.flatMap(f -> f.getVariables().stream())
					.map(v -> new FieldDefinition(v.getType().asString(), v.getNameAsString()))
					.collect(Collectors.toList());

			return new EntityDefinition(clazz.getNameAsString(), fields);
		} catch (Exception e) {
			throw new RuntimeException("Failed to parse source for class 'sourceRoot': " + className, e);
		}
	}

	public static CompilationUnit getCompilationUnit(String className, String sourceRoot) {
		try {
			Path sourceFile = Paths.get(sourceRoot).resolve(className.replace('.', '/') + ".java");
			if (!Files.exists(sourceFile)) {
				throw new IllegalArgumentException("Class file not found: " + sourceFile);
			}
			return StaticJavaParser.parse(sourceFile);
		} catch (IOException e) {
			throw new RuntimeException("Error reading or parsing source file", e);
		}
	}

	public static EntityDefinition fromClass(Class<?> clazz) {
		List<FieldDefinition> fields = List.of(clazz.getDeclaredFields()).stream()
				.map(f -> new FieldDefinition(f.getType().getSimpleName(), f.getName()))
				.collect(Collectors.toList());
		return new EntityDefinition(clazz.getSimpleName(), fields);
	}

	public Map<String, Object> toMap() {
		Map<String, Object> map = new HashMap<>();
		map.put("name", name);
		map.put("fields", fields);
		map.put("aggregateIdField", findIdField());
		map.put("aggregateIdType", findIdType());
		map.put("package", "com.pcoundia.app." + name.toLowerCase());
		return map;
	}

	private String findIdField() {
		return fields.stream()
				.filter(f -> f.getName().equalsIgnoreCase("id"))
				.map(FieldDefinition::getName)
				.findFirst()
				.orElse("id");
	}

	private String findIdType() {
		return fields.stream()
				.filter(f -> f.getName().equalsIgnoreCase("id"))
				.map(FieldDefinition::getType)
				.findFirst()
				.orElse("String");
	}

	private static String getSimpleName(String className) {
		int lastDot = className.lastIndexOf(".");
		return lastDot != -1 ? className.substring(lastDot + 1) : className;
	}

	public String getIdentifier() {
		return "id";
	}

	public List<FieldDefinition> getFieldFiles() {
		return fields.stream()
				.filter(f -> f.isFileType())
				.collect(Collectors.toList());
	}

	public List<FieldDefinition> searchFields() {
		return getFieldsWithoutRelations();
	}

	public boolean hasRelation(String relationType) {
		return fields.stream()
				.anyMatch(f -> relationType.equalsIgnoreCase(f.getRelation()));
	}

	public boolean isInStack(String stack) {

		if (this.stack == null || this.stack.isEmpty()) {
			return false;
		}

		return this.stack.stream()
				.anyMatch(s -> s.equalsIgnoreCase(stack));
	}

	public boolean hasField(String fieldName) {

		if (this.fields == null || this.fields.isEmpty()) {
			return false;
		}
		return fields.stream()
				.anyMatch(f -> f.getName().equalsIgnoreCase(fieldName));
	}

	public boolean isSkipped(String module) {

		if (this.skip == null || this.skip.isEmpty()) {
			return false;
		}

		return this.skip.stream()
				.anyMatch(s -> s.equalsIgnoreCase(module));
	}

	public boolean hasRabbitMq() {
		return this.isInStack("rabbitMq");
	}

	public List<FieldDefinition> getDtoFields() {
		return fields.stream()
				.filter(f -> !"oneToMany".equalsIgnoreCase(f.getRelation()))
				.collect(Collectors.toList());
	}

	public List<FieldDefinition> getEditableFields() {
		return fields.stream()
				.filter(f -> !"oneToMany".equalsIgnoreCase(f.getRelation()))
				.filter(f -> !f.getName().equalsIgnoreCase("createdBy"))
				.filter(f -> !f.getName().equalsIgnoreCase("tenant"))
				.filter(f -> !f.getName().equalsIgnoreCase("updatedAt"))
				.filter(f -> !f.getName().equalsIgnoreCase("createdAt"))
				.filter(f -> !f.getName().equalsIgnoreCase("reference"))
				.filter(f -> !f.isFileType())
				.collect(Collectors.toList());
	}


	public boolean hasManyToOne(){

		return fields.stream()
				.filter(f -> f.getRelation() != null)
				.anyMatch(f -> f.getRelation().equalsIgnoreCase("manyToOne")
				)
				&& !name.equalsIgnoreCase("tenant")
				&& !name.equalsIgnoreCase("createdBy")

				;

	}

	public List<FieldDefinition> getFieldsToDisplay() {
		return fields.stream()
				.filter(f -> {
					String n = f.getName().toLowerCase();
					return n.equalsIgnoreCase("name")
							||  n.equals("title")
							||  n.equals("messages")
							;
				})
				.collect(Collectors.toList());
	}

	public List<FieldDefinition> getFieldWithDisplayName() {
		return fields.stream()
				.filter(
						f -> f.getDisplayName() != null
								&& !f.getDisplayName().isEmpty()
						&& !"manyToOne".equalsIgnoreCase(f.getRelation())
				)
				.collect(Collectors.toList());
	}


	public List<FieldDefinition> getFieldsAmount() {
		return fields.stream()
				.filter(f -> {
					String n = f.getName();
					return n.equalsIgnoreCase("amount")
							|| n.equalsIgnoreCase("price")
							|| n.equalsIgnoreCase("currentBalance")
							;
				})
				.collect(Collectors.toList());
	}


	public void addDefaultFieldIfMissing() {
		List<FieldDefinition> fields = this.fields;

		if (!this.hasField("updatedAt")) {
			fields.add(FieldDefinition.builder()
					.name("updatedAt")
					.type("Date")
					.readOnly(true)
					.nullable(true)
					.build());
		}

		if (!this.hasField("reference")) {
			fields.add(FieldDefinition.builder()
					.name("reference")
					.type("String")
					.readOnly(true)
					.nullable(true)
					.build());
		}
	}


	public Boolean  getHasManyToOne(){
		return fields.stream()
				.filter(f -> f.getRelation() != null)
				.anyMatch(f -> f.getRelation().equalsIgnoreCase("manyToOne")

				)
				&& !name.equalsIgnoreCase("tenant")
				&& !name.equalsIgnoreCase("createdBy");
	}
}
