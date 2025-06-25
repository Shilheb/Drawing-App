package com.drawing.command;

import java.util.Stack;

/**
 * Gestionnaire de commandes pour supporter undo/redo
 */
public class CommandManager {
    
    private Stack<Command> undoStack;
    private Stack<Command> redoStack;
    
    public CommandManager() {
        this.undoStack = new Stack<>();
        this.redoStack = new Stack<>();
    }
    
    /**
     * Exécute une commande et l'ajoute à la pile d'annulation
     */
    public void executeCommand(Command command) {
        command.execute();
        undoStack.push(command);
        redoStack.clear(); // Effacer la pile de redo après une nouvelle commande
    }
    
    /**
     * Annule la dernière commande
     */
    public boolean undo() {
        if (!undoStack.isEmpty()) {
            Command command = undoStack.pop();
            command.undo();
            redoStack.push(command);
            return true;
        }
        return false;
    }
    
    /**
     * Refait la dernière commande annulée
     */
    public boolean redo() {
        if (!redoStack.isEmpty()) {
            Command command = redoStack.pop();
            command.execute();
            undoStack.push(command);
            return true;
        }
        return false;
    }
    
    /**
     * Vérifie s'il est possible d'annuler
     */
    public boolean canUndo() {
        return !undoStack.isEmpty();
    }
    
    /**
     * Vérifie s'il est possible de refaire
     */
    public boolean canRedo() {
        return !redoStack.isEmpty();
    }
    
    /**
     * Obtient la description de la prochaine commande à annuler
     */
    public String getUndoDescription() {
        if (canUndo()) {
            return undoStack.peek().getDescription();
        }
        return null;
    }
    
    /**
     * Obtient la description de la prochaine commande à refaire
     */
    public String getRedoDescription() {
        if (canRedo()) {
            return redoStack.peek().getDescription();
        }
        return null;
    }
    
    /**
     * Efface l'historique des commandes
     */
    public void clear() {
        undoStack.clear();
        redoStack.clear();
    }
}
