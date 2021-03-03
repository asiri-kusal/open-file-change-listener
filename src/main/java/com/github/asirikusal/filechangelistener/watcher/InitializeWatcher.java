package com.github.asirikusal.filechangelistener.watcher;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

@Service
public class InitializeWatcher {

    private final static Logger LOGGER = LogManager.getLogger(FileWatcherConfiguration.class);

    @Value("${file.name}")
    private String fileName;

    @Value("${property.file.name}")
    private String propertyFileName;

    @Autowired
    private FileWatcherConfiguration configuration;

    private Thread threadForFile;
    private Thread threadForProperty;

    private ConfigurationChangeListener listenerForFile;
    private ConfigurationChangeListener listenerForProperty;

    @Bean
    public void initWatcher() throws IOException {
        listenerForFile = new ConfigurationChangeListener(configuration.filePath(fileName), configuration);
        listenerForProperty = new ConfigurationChangeListener(configuration.filePath(propertyFileName), configuration);
        try {
            threadForFile = new Thread(listenerForFile);
            threadForFile.start();

            threadForProperty = new Thread(listenerForProperty);
            threadForProperty.start();

        } catch (Exception e) {
            LOGGER.error(e);
        }

    }

}
