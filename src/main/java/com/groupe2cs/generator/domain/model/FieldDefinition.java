package com.groupe2cs.generator.domain.model;

import lombok.*;

import java.io.Serializable;

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
    private Boolean unique;
    private Boolean nullable;

    public FieldDefinition(String type, String name) {
        this.type = type;
        this.name = name;
        this.nameCapitalized = this.name.substring(0, 1).toUpperCase() + this.name.substring(1);
        this.isId = this.isId();
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

        if (isId()){
            return true;
        }
        if( unique == null) {
            return false;
        }

        return unique;
    }

    public boolean isFileType() {
        return
                type.equalsIgnoreCase("MultipartFile") ||
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
                    "java.time.instant","java.util.date","java.time.localdate","java.time.localdatetime"
                    -> true;
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
}
