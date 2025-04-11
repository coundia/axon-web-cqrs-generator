package com.groupe2cs.generator.application.dto;

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

}
