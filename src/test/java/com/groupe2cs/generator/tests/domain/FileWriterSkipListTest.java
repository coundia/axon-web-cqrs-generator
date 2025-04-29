package com.groupe2cs.generator.tests.domain;

import com.groupe2cs.generator.domain.engine.FileWriterSkipList;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FileWriterSkipListTest {

	@Test
	void it_should_return_true_if_file_in_skip_list() {
		boolean result = FileWriterSkipList.shouldSkip("FindByPasswordResetTokenHandler.java");
		assertThat(result).isTrue();
	}

	@Test
	void it_should_return_false_if_file_not_in_skip_list() {
		boolean result = FileWriterSkipList.shouldSkip("src/main/java/SomeOtherFile.java");
		assertThat(result).isFalse();
	}
}
