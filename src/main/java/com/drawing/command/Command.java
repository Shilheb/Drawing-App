package com.drawing.command;

/**
 * Interface Command pour implémenter le pattern Command
 * Permet d'encapsuler les actions et de supporter undo/redo
 */
public interface Command {
    
    /**
     * Exécute la commande
     */
    void execute();
    
    /**
     * Annule la commande
     */
    void undo();
    
    /**
     * Retourne une description de la commande
     */
    String getDescription();
}
