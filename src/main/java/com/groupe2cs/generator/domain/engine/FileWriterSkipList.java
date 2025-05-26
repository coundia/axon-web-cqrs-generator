package com.groupe2cs.generator.domain.engine;

import java.util.List;

public class FileWriterSkipList {

	private static final List<String> FILES_TO_SKIP = List.of(
			"FindByPasswordResetTokenHandler.java",
			"UserRepository.java"
			//"FindByApiKeyAppKeyHandler.java"
	);

	public static boolean shouldSkip(String fileName) {
		return FILES_TO_SKIP.stream().anyMatch(fileName::endsWith);
	}
}
