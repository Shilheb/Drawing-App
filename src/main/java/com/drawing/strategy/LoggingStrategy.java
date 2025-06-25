package com.drawing.strategy;

/**
 * Interface Strategy pour les différentes stratégies de journalisation
 */
public interface LoggingStrategy {
    
    /**
     * Enregistre un message de log
     * @param level Niveau de log (INFO, WARNING, ERROR)
     * @param message Message à enregistrer
     */
    void log(LogLevel level, String message);
    
    /**
     * Ferme les ressources utilisées par la stratégie de logging
     */
    void close();
    
    /**
     * Énumération des niveaux de log
     */
    enum LogLevel {
        INFO, WARNING, ERROR, DEBUG
    }
}
