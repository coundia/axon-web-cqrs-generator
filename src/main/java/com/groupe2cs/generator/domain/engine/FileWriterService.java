package com.groupe2cs.generator.domain.engine;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileWriterService {

    public void write(String baseDir, String fileName, String content) {
        try {
            Path directory = Paths.get(baseDir).toAbsolutePath();
            Path path = directory.resolve(fileName);

            Files.createDirectories(path.getParent());
            Files.writeString(path, content);

            System.out.println("✅ Generated: " + path);
        } catch (IOException e) {
            throw new RuntimeException("❌ Failed to write file: " + fileName, e);
        }
    }
}
