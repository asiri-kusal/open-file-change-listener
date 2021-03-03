package com.github.asirikusal.filechangelistener.watcher;

import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ConfigurableApplicationContext;

public class ConfigurationChangeListener implements Runnable {

    private final static Logger LOGGER = LogManager.getLogger(ConfigurationChangeListener.class);

    private String configFileName = null;
    private String fullFilePath;
    private FileWatcherConfiguration configuration;

    public ConfigurationChangeListener(String filePath, FileWatcherConfiguration configuration) {
        this.fullFilePath = filePath;
        this.configuration = configuration;
    }

    public void run() {
        try {
            register(this.fullFilePath);
        } catch (IOException e) {
            LOGGER.error(e);
        }

    }

    public void stopThread() {
        Thread.currentThread().isInterrupted();
    }

    private void register(final String file) throws IOException {
        final int lastIndex = file.lastIndexOf("/");
        String dirPath = file.substring(0, lastIndex + 1);
        String fileName = file.substring(lastIndex + 1, file.length());
        this.configFileName = fileName;

        configurationChanged();
        startWatcher(dirPath, fileName);
    }

    private void startWatcher(String dirPath, String file) throws IOException {
        final WatchService watchService = FileSystems.getDefault()
                                                     .newWatchService();
        Path path = Paths.get(dirPath);
        path.register(watchService, ENTRY_MODIFY);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                try {
                    watchService.close();
                } catch (IOException e) {
                    LOGGER.error(e);
                }
            }
        });

        WatchKey key = null;
        while (true) {
            try {
                key = watchService.take();
                for (WatchEvent<?> event : key.pollEvents()) {
                    if (event.context().toString().equals(configFileName)) {
                        configurationChanged();
                    }
                }
                boolean reset = key.reset();
                if (!reset) {
                    LOGGER.info("Could not reset the watch key.");
                    break;
                }
            } catch (Exception e) {
                LOGGER.error("InterruptedException: ", e);
            }
        }
    }

    public void configurationChanged() throws IOException {
        LOGGER.info("Refreshing the configuration.");
        configuration.getConfiguration();
        configuration.getFileContent();
    }


}
