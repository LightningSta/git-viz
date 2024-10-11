package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class Config {
    public String graphToolPath;
    public String repositoryPath;
    public String outputPath;
    public String fileName;

    public static Config loadConfig(String filePath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(new File(filePath), Config.class);
    }
}
