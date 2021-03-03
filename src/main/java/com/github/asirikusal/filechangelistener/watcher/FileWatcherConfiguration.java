package com.github.asirikusal.filechangelistener.watcher;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

@Component
public class FileWatcherConfiguration {

    private final static Logger LOGGER = LogManager.getLogger(FileWatcherConfiguration.class);

    @Value("${file.location}")
    private String validationSchemaLocation;

    @Value("${file.name}")
    private String fileName;

    @Value("${property.file.name}")
    private String propertyFileName;

    @Autowired
    private ResourceLoader resourceLoader;


    @Bean
    public Properties getConfiguration() throws IOException {
        Properties configuration = new Properties();
        initProperties(filePath(propertyFileName), configuration);
        return configuration;
    }

    @Bean
    public String getFileContent() throws IOException {
        String text;
        StringBuffer sb = new StringBuffer();
        try (final Reader reader = new InputStreamReader(getInputStream(filePath(fileName)))) {
            BufferedReader br = new BufferedReader(reader);
            while ((text = br.readLine()) != null) {
                sb.append(text);
            }
        }
        return sb.toString();
    }


    public String filePath(String fileName) throws IOException {
        String filePath;
        if (validationSchemaLocation.equalsIgnoreCase("classpath")) {
            filePath = resourceLoader.getResource("classpath:" + fileName).getFile().getPath();
        } else {
            filePath = new File(fileName).getPath();
        }
        return filePath;
    }

    private InputStream getInputStream(String fileName) throws IOException {
        if (validationSchemaLocation.equalsIgnoreCase("classpath")) {
            Resource resource = resourceLoader.getResource("classpath:" + fileName);
            return resource.getInputStream();
        }
        return new FileInputStream(fileName);
    }

    public void initProperties(String file, Properties configuration) throws IOException {
        configuration.load(getInputStream(file));
    }

}
