package com.drawing.strategy;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Stratégie de journalisation dans un fichier
 */
public class FileLoggingStrategy implements LoggingStrategy {
    
    private static final DateTimeFormatter FORMATTER = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    private PrintWriter writer;
    private String filename;
    
    public FileLoggingStrategy(String filename) {
        this.filename = filename;
        try {
            this.writer = new PrintWriter(new FileWriter(filename, true));
        } catch (IOException e) {
            System.err.println("Erreur lors de l'ouverture du fichier de log: " + e.getMessage());
            // Fallback vers la console
            this.writer = new PrintWriter(System.out);
        }
    }
    
    @Override
    public void log(LogLevel level, String message) {
        if (writer != null) {
            String timestamp = LocalDateTime.now().format(FORMATTER);
            String logMessage = String.format("[%s] %s - %s", timestamp, level, message);
            
            writer.println(logMessage);
            writer.flush(); // S'assurer que le message est écrit immédiatement
        }
    }
    
    @Override
    public void close() {
        if (writer != null) {
            writer.close();
        }
    }
    
    public String getFilename() {
        return filename;
    }
}
