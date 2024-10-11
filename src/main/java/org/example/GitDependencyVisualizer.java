package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class GitDependencyVisualizer {

    private final String repositoryPath;
    private final String fileName;

    public GitDependencyVisualizer(String repositoryPath, String fileName) {
        this.repositoryPath = repositoryPath;
        this.fileName = fileName;
    }


    public List<Map<String, String>> getCommitsForFile() throws Exception {
        ProcessBuilder processBuilder = new ProcessBuilder("git", "-C", repositoryPath, "log", "--pretty=format:%H|%P|%s", "--", fileName);
        Process process = processBuilder.start();
        List<Map<String, String>> commits = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Map<String, String> commitData = parseCommit(line);
                if (commitData != null) {
                    commits.add(commitData);
                }
            }
        }

        return commits;
    }
    private Map<String, String> parseCommit(String commitLine) {
        String[] parts = commitLine.split("\\|", 3);
        if (parts.length < 3) {
            return null;
        }

        Map<String, String> commitData = new HashMap<>();
        commitData.put("hash", parts[0]);
        commitData.put("parents", parts[1]);
        commitData.put("message", sanitizeMessage(parts[2]));

        return commitData;
    }

    private String sanitizeMessage(String message) {
        return message.replaceAll("[:\\-]", " ").replaceAll("[^\\w\\s]", "");
    }
    public String generatePlantUML(List<Map<String, String>> commits) {
        StringBuilder uml = new StringBuilder("@startuml\n");
        for (Map<String, String> commit : commits) {
            String commitHash = commit.get("hash");
            String sanitizedMessage = commit.get("message");

            uml.append(String.format("\"%s\" : \"%s\"\n", commitHash, sanitizedMessage));

            String[] parents = commit.get("parents").split(" ");
            for (String parent : parents) {
                if (!parent.isEmpty()) {
                    uml.append(String.format("\"%s\" --> \"%s\"\n", commitHash, parent));
                }
            }
        }
        uml.append("@enduml");
        return uml.toString();
    }

    public void saveUMLToFile(String umlContent, String outputPath) throws IOException {
        Files.writeString(Path.of(outputPath), umlContent);
    }

    public void visualizeUML(String plantUMLPath, String outputPath) throws Exception {
        ProcessBuilder processBuilder = new ProcessBuilder("java", "-jar", plantUMLPath, outputPath);
        processBuilder.start().waitFor();
    }
    public static void main(String[] args) throws Exception {
        Config config = Config.loadConfig(args[0]);

        GitDependencyVisualizer visualizer = new GitDependencyVisualizer(config.repositoryPath, config.fileName);

        System.out.println("Получение коммитов...");
        List<Map<String, String>> commitData = visualizer.getCommitsForFile();

        if (commitData.isEmpty()) {
            System.out.println("Коммитов для файла " + config.fileName + " не найдено.");
            return;
        }

        System.out.println("Генерация PlantUML...");
        String umlContent = visualizer.generatePlantUML(commitData);

        System.out.println("Сохранение UML в файл...");
        visualizer.saveUMLToFile(umlContent, config.outputPath);

        System.out.println("Визуализация графа...");
        visualizer.visualizeUML(config.graphToolPath, config.outputPath);

        System.out.println("Готово!");
    }
}
