package com.groupe2cs.generator.domain.engine;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileWriterService {

    @Value("${rewrite:true}")
    private boolean rewrite;


    @Value("${force:false}")
    private boolean force;

    public void write(String baseDir, String fileName, String content) {
        try {
            Path directory = Paths.get(baseDir).toAbsolutePath();
            Path path = directory.resolve(fileName);

            Files.createDirectories(path.getParent());

            boolean fileExists = Files.exists(path);

            if (fileExists && FileWriterSkipList.shouldSkip(fileName) && !force) {
                System.out.println("❤️⏭️ Skipped (in skip list): " + path);
                return;
            }

            if (fileExists && !rewrite && !force) {
                System.out.println("⏭️ Skipped (already exists): " + path);
                return;
            }

            Files.writeString(path, content);
            System.out.println("✅ Generated: " + path);
        } catch (IOException e) {
            throw new RuntimeException("❌ Failed to write file: " + fileName, e);
        }
    }
}
