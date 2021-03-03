package com.github.asirikusal.filechangelistener.controller;

import java.io.IOException;
import java.util.Properties;

import com.github.asirikusal.filechangelistener.watcher.FileWatcherConfiguration;
import com.github.asirikusal.filechangelistener.watcher.InitializeWatcher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class FileChangeListenerController {

    private final static Logger LOGGER = LogManager.getLogger(FileChangeListenerController.class);

    @Autowired
    private FileWatcherConfiguration configuration;

    @GetMapping("/get-file-content")
    public ResponseEntity<String> getProperties() {
        try {
            return new ResponseEntity<>(configuration.getFileContent(), HttpStatus.ACCEPTED);
        } catch (IOException e) {
            LOGGER.error(e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/get-property-content/{field}")
    public ResponseEntity<String> getProperties(@PathVariable("field") String field) {
        String value;
        try {
            value = configuration.getConfiguration().getProperty(field);
        } catch (Exception e) {
            LOGGER.error(e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("property value " + value, HttpStatus.ACCEPTED);
    }

}
