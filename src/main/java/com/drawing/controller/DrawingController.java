package com.drawing.controller;

import com.drawing.command.*;
import com.drawing.database.DatabaseManager;
import com.drawing.factory.ShapeFactory;
import com.drawing.model.drawing.Drawing;
import com.drawing.model.shapes.Shape;
import com.drawing.observer.DrawingObserver;
import com.drawing.utils.Logger;
import javafx.scene.paint.Color;

/**
 * Contrôleur principal de l'application de dessin
 * Implémente le pattern MVC
 */
public class DrawingController implements DrawingObserver {
    
    private Drawing currentDrawing;
    private CommandManager commandManager;
    private DatabaseManager databaseManager;
    private Logger logger;
    
    // État actuel de l'outil de dessin
    private ShapeFactory.ShapeType selectedShapeType;
    private Color selectedColor;
    private double selectedStrokeWidth;
    private boolean fillMode;
    
    public DrawingController() {
        this.currentDrawing = new Drawing();
        this.commandManager = new CommandManager();
        this.databaseManager = new DatabaseManager();
        this.logger = Logger.getInstance();
        
        // Configuration par défaut
        this.selectedShapeType = ShapeFactory.ShapeType.RECTANGLE;
        this.selectedColor = Color.BLACK;
        this.selectedStrokeWidth = 2.0;
        this.fillMode = false;
        
        // S'enregistrer comme observateur du dessin
        currentDrawing.addObserver(this);
        
        logger.info("Contrôleur de dessin initialisé");
    }
    
    // Méthodes de gestion des formes
    public void addShape(double x, double y) {
        try {
            Shape shape = ShapeFactory.createDefaultShape(selectedShapeType, x, y);
            shape.setColor(selectedColor);
            shape.setStrokeWidth(selectedStrokeWidth);
            shape.setFilled(fillMode);
            
            Command addCommand = new AddShapeCommand(currentDrawing, shape);
            commandManager.executeCommand(addCommand);
            
            logger.info("Forme ajoutée: " + shape.toString());
        } catch (Exception e) {
            logger.error("Erreur lors de l'ajout de la forme: " + e.getMessage());
        }
    }
    
    public void addShape(double[] parameters) {
        try {
            Shape shape = ShapeFactory.createShape(selectedShapeType, parameters, 
                                                 selectedColor, selectedStrokeWidth, fillMode);
            Command addCommand = new AddShapeCommand(currentDrawing, shape);
            commandManager.executeCommand(addCommand);
            
            logger.info("Forme ajoutée avec paramètres: " + shape.toString());
        } catch (Exception e) {
            logger.error("Erreur lors de l'ajout de la forme: " + e.getMessage());
        }
    }
    
    public void removeShape(Shape shape) {
        if (shape != null) {
            Command removeCommand = new RemoveShapeCommand(currentDrawing, shape);
            commandManager.executeCommand(removeCommand);
            
            logger.info("Forme supprimée: " + shape.toString());
        }
    }
    
    public void removeShapeAt(double x, double y) {
        Shape shape = currentDrawing.findShapeAt(x, y);
        if (shape != null) {
            removeShape(shape);
        }
    }
    
    // Méthodes de gestion des commandes
    public boolean undo() {
        boolean result = commandManager.undo();
        if (result) {
            logger.info("Annulation: " + commandManager.getRedoDescription());
        }
        return result;
    }
    
    public boolean redo() {
        boolean result = commandManager.redo();
        if (result) {
            logger.info("Rétablissement: " + commandManager.getUndoDescription());
        }
        return result;
    }
    
    public boolean canUndo() {
        return commandManager.canUndo();
    }
    
    public boolean canRedo() {
        return commandManager.canRedo();
    }
    
    // Méthodes de gestion des dessins
    public void newDrawing() {
        currentDrawing.removeObserver(this);
        currentDrawing = new Drawing();
        currentDrawing.addObserver(this);
        commandManager.clear();
        
        logger.info("Nouveau dessin créé");
    }
    
    public void newDrawing(String name) {
        currentDrawing.removeObserver(this);
        currentDrawing = new Drawing(name);
        currentDrawing.addObserver(this);
        commandManager.clear();
        
        logger.info("Nouveau dessin créé: " + name);
    }
    
    public boolean saveDrawing() {
        boolean result = databaseManager.saveDrawing(currentDrawing);
        if (result) {
            logger.info("Dessin sauvegardé: " + currentDrawing.getName());
        } else {
            logger.error("Échec de la sauvegarde du dessin: " + currentDrawing.getName());
        }
        return result;
    }
    
    public boolean loadDrawing(String name) {
        Drawing loadedDrawing = databaseManager.loadDrawing(name);
        if (loadedDrawing != null) {
            currentDrawing.removeObserver(this);
            currentDrawing = loadedDrawing;
            currentDrawing.addObserver(this);
            commandManager.clear();
            
            logger.info("Dessin chargé: " + name);
            return true;
        } else {
            logger.error("Échec du chargement du dessin: " + name);
            return false;
        }
    }
    
    public void clearDrawing() {
        currentDrawing.clear();
        commandManager.clear();
        logger.info("Dessin effacé");
    }
    
    // Getters et setters
    public Drawing getCurrentDrawing() {
        return currentDrawing;
    }
    
    public ShapeFactory.ShapeType getSelectedShapeType() {
        return selectedShapeType;
    }
    
    public void setSelectedShapeType(ShapeFactory.ShapeType shapeType) {
        this.selectedShapeType = shapeType;
        logger.debug("Type de forme sélectionné: " + shapeType);
    }
    
    public Color getSelectedColor() {
        return selectedColor;
    }
    
    public void setSelectedColor(Color color) {
        this.selectedColor = color;
        logger.debug("Couleur sélectionnée: " + color);
    }
    
    public double getSelectedStrokeWidth() {
        return selectedStrokeWidth;
    }
    
    public void setSelectedStrokeWidth(double strokeWidth) {
        this.selectedStrokeWidth = strokeWidth;
        logger.debug("Épaisseur de trait sélectionnée: " + strokeWidth);
    }
    
    public boolean isFillMode() {
        return fillMode;
    }
    
    public void setFillMode(boolean fillMode) {
        this.fillMode = fillMode;
        logger.debug("Mode remplissage: " + fillMode);
    }
    
    // Implémentation de DrawingObserver
    @Override
    public void onShapeAdded(Shape shape) {
        // Peut être utilisé pour mettre à jour l'interface
    }
    
    @Override
    public void onShapeRemoved(Shape shape) {
        // Peut être utilisé pour mettre à jour l'interface
    }
    
    @Override
    public void onShapeModified(Shape shape) {
        // Peut être utilisé pour mettre à jour l'interface
    }
    
    @Override
    public void onDrawingCleared() {
        // Peut être utilisé pour mettre à jour l'interface
    }
    
    @Override
    public void onDrawingSaved(String filename) {
        // Peut être utilisé pour mettre à jour l'interface
    }
    
    @Override
    public void onDrawingLoaded(String filename) {
        // Peut être utilisé pour mettre à jour l'interface
    }
    
    // Méthodes utilitaires
    public java.util.List<String> getSavedDrawings() {
        return databaseManager.getDrawingNames();
    }
    
    public void cleanup() {
        databaseManager.close();
        logger.close();
    }
}
