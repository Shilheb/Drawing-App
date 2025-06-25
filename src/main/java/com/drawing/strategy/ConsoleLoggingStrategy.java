package com.drawing.strategy;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Stratégie de journalisation dans la console
 */
public class ConsoleLoggingStrategy implements LoggingStrategy {
    
    private static final DateTimeFormatter FORMATTER = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    @Override
    public void log(LogLevel level, String message) {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        String logMessage = String.format("[%s] %s - %s", timestamp, level, message);
        
        switch (level) {
            case ERROR:
                System.err.println(logMessage);
                break;
            case WARNING:
                System.out.println("\u001B[33m" + logMessage + "\u001B[0m"); // Jaune
                break;
            case INFO:
                System.out.println("\u001B[32m" + logMessage + "\u001B[0m"); // Vert
                break;
            case DEBUG:
                System.out.println("\u001B[36m" + logMessage + "\u001B[0m"); // Cyan
                break;
            default:
                System.out.println(logMessage);
        }
    }
    
    @Override
    public void close() {
        // Rien à fermer pour la console
    }
}
