package com.drawing.utils;

import com.drawing.strategy.*;

/**
 * Gestionnaire de journalisation utilisant le pattern Singleton
 * et le pattern Strategy pour changer de stratégie de logging
 */
public class Logger {
    
    private static Logger instance;
    private LoggingStrategy strategy;
    
    private Logger() {
        // Stratégie par défaut : console
        this.strategy = new ConsoleLoggingStrategy();
    }
    
    public static Logger getInstance() {
        if (instance == null) {
            synchronized (Logger.class) {
                if (instance == null) {
                    instance = new Logger();
                }
            }
        }
        return instance;
    }
    
    public void setStrategy(LoggingStrategy strategy) {
        if (this.strategy != null) {
            this.strategy.close();
        }
        this.strategy = strategy;
    }
    
    public void info(String message) {
        if (strategy != null) {
            strategy.log(LoggingStrategy.LogLevel.INFO, message);
        }
    }
    
    public void warning(String message) {
        if (strategy != null) {
            strategy.log(LoggingStrategy.LogLevel.WARNING, message);
        }
    }
    
    public void error(String message) {
        if (strategy != null) {
            strategy.log(LoggingStrategy.LogLevel.ERROR, message);
        }
    }
    
    public void debug(String message) {
        if (strategy != null) {
            strategy.log(LoggingStrategy.LogLevel.DEBUG, message);
        }
    }
    
    public void close() {
        if (strategy != null) {
            strategy.close();
        }
    }
    
    // Méthodes de convenance pour changer de stratégie
    public void useConsoleLogging() {
        setStrategy(new ConsoleLoggingStrategy());
    }
    
    public void useFileLogging(String filename) {
        setStrategy(new FileLoggingStrategy(filename));
    }
    
    public void useDatabaseLogging(String databaseUrl) {
        setStrategy(new DatabaseLoggingStrategy(databaseUrl));
    }
}
