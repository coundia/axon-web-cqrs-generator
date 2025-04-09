package com.groupe2cs.generator.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "")
public class GeneratorProperties {

    private String entityName;
    private String outputDir;
    private String sourceRoot;
    private String voPackage;
    private String domainPackage;
    private String commandPackage;
    private String applicationPackage;
    private String infrastructurePackage;
    private String presentationPackage;
    private String servicePackage;
    private String webSocketPackage;
    private String controllerPackage;
    private String repositoryPackage;
    private String mapperPackage;
    private String projectionPackage;
    private String dtoPackage;
    private String eventPackage;
    private String testPackage;
    private String queryPackage;
    private String  queryHandlerPackage;
    private String  exceptionPackage;
    private String  entityPackage;
    private String  sharedPackage;

}
