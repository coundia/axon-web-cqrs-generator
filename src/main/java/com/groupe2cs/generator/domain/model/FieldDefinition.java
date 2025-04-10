package com.groupe2cs.generator.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class FieldDefinition implements Serializable {
    private String type;
    private String name;
    private String nameCapitalized;
    private String nameCamelCase;
    private Boolean isId;

    public FieldDefinition(String type, String name) {
        this.type = type;
        this.name = name;
        this.nameCapitalized = this.name.substring(0,1).toUpperCase()+this.name.substring(1);
        this.isId = this.isId();

    }

    public boolean isDate() {

        return type.equalsIgnoreCase("date") ||
                type.equalsIgnoreCase("datetime")
                ;
    }

    public boolean isFilable() {

        return name.equalsIgnoreCase("id") ||
               name.equalsIgnoreCase("name") ||
               name.equalsIgnoreCase("label")
                ;
    }

    public boolean isId() {
        return name.equalsIgnoreCase("id");
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
        if(!isPrimitiveType()){
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
            case "string", "int", "integer", "long", "double",
                 "float", "boolean", "short", "char" -> true;
            default -> false;
        };
    }

}
