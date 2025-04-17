package com.groupe2cs.generator.shared;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Utils {

    public static String capitalize(String value) {
        return value.substring(0, 1).toUpperCase() + value.substring(1);
    }

    public static String getPackage(String fullPath) {
        String normalized = fullPath.replace("\\", "/");

        int index = normalized.indexOf("/src/main/java/");
        if (index >= 0) {
            String packagePath = normalized.substring(index + "/src/main/java/".length());
            return packagePath.replace("/", ".");
        }

        return normalized.replace("/", ".");
    }
    public static  String getRootDir(String outputDir, String entityName) {
        Path path = Paths.get(outputDir);
        Path last = path.getFileName();
        if (last != null && last.toString().equalsIgnoreCase(entityName)) {
            return path.getParent().toString();
        }
        if (last != null && last.toString().equalsIgnoreCase(entityName+"s")) {
            return path.getParent().toString();
        }
        return outputDir;
    }
    public static  String getParent(String outputDir) {
        Path path = Paths.get(outputDir);

        if(path.getParent() != null) {
            return path.getParent().toString();
        }

        return outputDir;
    }


    public static String getTestPackage(String fullPath) {

        String relativePath = getPackage(fullPath);

        String testPath = relativePath.replace("/main/", "/test/");
        testPath = getPackage(testPath);

        return testPath;
    }
    public static String getTestDir(String fullPath) {

        return fullPath.replace("/main/", "/test/");
    }


    public static Object lowerFirst(String name) {
        return Character.toLowerCase(name.charAt(0)) + name.substring(1);
    }

    public static String camelCase(String input) {
        if (input == null || input.isEmpty()) return input;
        String[] parts = input.split("_");
        StringBuilder result = new StringBuilder(parts[0].toLowerCase());
        for (int i = 1; i < parts.length; i++) {
            result.append(parts[i].substring(0,1).toUpperCase());
            result.append(parts[i].substring(1).toLowerCase());
        }
        return result.toString();
    }

    public static String lowerCase(String input) {
        return input.toLowerCase();
    }

    public static String upperCase(String input) {
        return input.toUpperCase();
    }



}
