package com.groupe2cs.generator;

import com.groupe2cs.generator.application.usecase.GroupMainGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CodeGeneratorApplication  {

    final GroupMainGenerator groupMainGenerator;

    public CodeGeneratorApplication(GroupMainGenerator groupMainGenerator

    ) {

        this.groupMainGenerator = groupMainGenerator;
    }

    public static void main(String[] args) {
        SpringApplication.run(CodeGeneratorApplication.class, args);
    }
}
