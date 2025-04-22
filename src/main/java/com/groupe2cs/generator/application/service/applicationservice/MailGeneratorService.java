package com.groupe2cs.generator.application.service.applicationservice;

import com.groupe2cs.generator.application.dto.SharedTemplate;
import com.groupe2cs.generator.domain.engine.FileWriterService;
import com.groupe2cs.generator.domain.engine.TemplateEngine;
import com.groupe2cs.generator.domain.model.EntityDefinition;
import com.groupe2cs.generator.infrastructure.config.GeneratorProperties;
import com.groupe2cs.generator.shared.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class MailGeneratorService {

	private final TemplateEngine templateEngine;
	private final FileWriterService fileWriterService;
	private final GeneratorProperties generatorProperties;

	public void generate(EntityDefinition definition, String baseDir) {
		String parentDir = Utils.getParent(baseDir);

		String sharedDir = parentDir + "/" + generatorProperties.getSharedPackage();
		String domainDir = sharedDir + "/" + generatorProperties.getDomainPackage();
		String infraDir = sharedDir + "/" + generatorProperties.getInfrastructurePackage();
		String applicationDir = sharedDir + "/" + generatorProperties.getApplicationPackage();
		String presentationDir = sharedDir + "/" + generatorProperties.getPresentationPackage();

		String domainPkg = Utils.getPackage(domainDir);
		String applicationPkg = Utils.getPackage(applicationDir);


		String testDir = Utils.getTestDir(sharedDir) + "/" + generatorProperties.getInfrastructurePackage();


		List<SharedTemplate> templates = List.of(
				new SharedTemplate(
						"MailSender",
						"domain/mailSender.mustache",
						Set.of(),
						domainDir
				),
				new SharedTemplate(
						"MailSenderImpl",
						"infrastructure/mailSender.mustache",
						Set.of(
								domainPkg + ".MailSender"
						),
						infraDir
				),
				new SharedTemplate(

						"NotificationService",
						"application/notificationService.mustache",
						Set.of(
								domainPkg + ".MailSender"
						),
						applicationDir
				),
				new SharedTemplate(
						"FakeMailSender",
						"tests/fakeMailSender.mustache",
						Set.of(
								domainPkg + ".MailSender"
						),
						testDir,
						Utils.getTestPackage(infraDir)
				),
				new SharedTemplate(
						"MailSenderTest",
						"tests/mailSenderTest.mustache",
						Set.of(
								domainPkg + ".MailSender"
						),
						testDir,
						Utils.getTestPackage(infraDir)
				)
				,
				new SharedTemplate(
						"SendTestsMailController",
						"presentation/sendMailController.mustache",
						Set.of(
								domainPkg + ".MailSender",
								applicationPkg+".NotificationService"
						),
						presentationDir
				)
		);

		templates.forEach(template -> generateMailFile(template));
	}

	private void generateMailFile(SharedTemplate template) {
		String packageName = template.getPackageName();
		if (packageName == null || packageName.isEmpty() || packageName.equals("null")) {
			packageName = Utils.getPackage(template.getOutput());
		}

		Map<String, Object> context = new HashMap<>();
		context.put("package", packageName);
		context.put("imports", template.getImports());
		context.put("className", template.getClassName());

		String content = templateEngine.render(template.getTemplatePath(), context);
		fileWriterService.write(template.getOutput(), template.getClassName() + ".java", content);
	}
}
