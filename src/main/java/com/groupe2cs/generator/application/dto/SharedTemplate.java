package com.groupe2cs.generator.application.dto;

import com.groupe2cs.generator.shared.Utils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SharedTemplate implements Serializable {
    private  String className;
    private  String templatePath;
    private  Set<String> imports;
    private  String output;
    private  String packageName;

    public SharedTemplate(String className, String templatePath, Set<String> imports, String output) {
        this.className = className;
        this.templatePath = templatePath;
        this.imports = imports;
        this.output = output;
    }

}
