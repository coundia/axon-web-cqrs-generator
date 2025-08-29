package com.groupe2cs.generator.application.dto;

import com.groupe2cs.generator.shared.Utils;
import lombok.*;

import java.io.Serializable;
import java.util.Set;

@Builder
@Getter
@Setter
public class SharedTemplate implements Serializable {
	private String className;
	private String templatePath;
	private Set<String> imports;
	private String output;
	private String packageName;
	private String ext = ".java";

	public SharedTemplate(String className, String templatePath, Set<String> imports, String output) {
		this.className = className;
		this.templatePath = templatePath;
		this.imports = imports;
		this.output = output;
	}

	public SharedTemplate(String className, String templatePath, Set<String> imports, String output, String packageName) {
		this.className = className;
		this.templatePath = templatePath;
		this.imports = imports;
		this.output = output;
		this.packageName = packageName;
	}

	public SharedTemplate(String className, String templatePath, Set<String> imports, String output, String packageName, String ext) {
		this.className = className;
		this.templatePath = templatePath;
		this.imports = imports;
		this.output = output;
		this.packageName = packageName;
		this.ext = ext;
	}
}

