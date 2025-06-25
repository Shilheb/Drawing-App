package com.drawing.model.drawing;

import com.drawing.model.shapes.Shape;
import com.drawing.observer.DrawingObserver;
import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Modèle représentant un dessin
 * Implémente le pattern Observer pour notifier les changements
 */
public class Drawing {
    private List<Shape> shapes;
    private List<DrawingObserver> observers;
    private String name;
    private boolean modified;
    
    public Drawing() {
        this.shapes = new ArrayList<>();
        this.observers = new CopyOnWriteArrayList<>();
        this.name = "Nouveau dessin";
        this.modified = false;
    }
    
    public Drawing(String name) {
        this();
        this.name = name;
    }
    
    // Gestion des observateurs
    public void addObserver(DrawingObserver observer) {
        observers.add(observer);
    }
    
    public void removeObserver(DrawingObserver observer) {
        observers.remove(observer);
    }
    
    private void notifyShapeAdded(Shape shape) {
        for (DrawingObserver observer : observers) {
            observer.onShapeAdded(shape);
        }
    }
    
    private void notifyShapeRemoved(Shape shape) {
        for (DrawingObserver observer : observers) {
            observer.onShapeRemoved(shape);
        }
    }
    
    private void notifyShapeModified(Shape shape) {
        for (DrawingObserver observer : observers) {
            observer.onShapeModified(shape);
        }
    }
    
    private void notifyDrawingCleared() {
        for (DrawingObserver observer : observers) {
            observer.onDrawingCleared();
        }
    }
    
    private void notifyDrawingSaved(String filename) {
        for (DrawingObserver observer : observers) {
            observer.onDrawingSaved(filename);
        }
    }
    
    private void notifyDrawingLoaded(String filename) {
        for (DrawingObserver observer : observers) {
            observer.onDrawingLoaded(filename);
        }
    }
    
    // Gestion des formes
    public void addShape(Shape shape) {
        shapes.add(shape);
        modified = true;
        notifyShapeAdded(shape);
    }
    
    public boolean removeShape(Shape shape) {
        boolean removed = shapes.remove(shape);
        if (removed) {
            modified = true;
            notifyShapeRemoved(shape);
        }
        return removed;
    }
    
    public void modifyShape(Shape shape) {
        modified = true;
        notifyShapeModified(shape);
    }
    
    public void clear() {
        shapes.clear();
        modified = true;
        notifyDrawingCleared();
    }
    
    public List<Shape> getShapes() {
        return new ArrayList<>(shapes);
    }
    
    public int getShapeCount() {
        return shapes.size();
    }
    
    // Rendu du dessin
    public void render(GraphicsContext gc) {
        for (Shape shape : shapes) {
            shape.render(gc);
        }
    }
    
    // Recherche de forme à une position donnée
    public Shape findShapeAt(double x, double y) {
        // Recherche en ordre inverse pour trouver la forme du dessus
        for (int i = shapes.size() - 1; i >= 0; i--) {
            Shape shape = shapes.get(i);
            if (shape.contains(x, y)) {
                return shape;
            }
        }
        return null;
    }
    
    // Getters et setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
        modified = true;
    }
    
    public boolean isModified() {
        return modified;
    }
    
    public void setModified(boolean modified) {
        this.modified = modified;
    }
    
    // Méthodes pour la sauvegarde/chargement
    public void markAsSaved(String filename) {
        modified = false;
        notifyDrawingSaved(filename);
    }
    
    public void markAsLoaded(String filename) {
        modified = false;
        notifyDrawingLoaded(filename);
    }
}
