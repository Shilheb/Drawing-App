package com.drawing.model.shapes;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Classe abstraite représentant une forme géométrique
 * Utilise le pattern Template Method pour définir la structure commune
 */
public abstract class Shape {
    protected double x, y;
    protected Color color;
    protected double strokeWidth;
    protected boolean filled;
    
    public Shape(double x, double y, Color color, double strokeWidth, boolean filled) {
        this.x = x;
        this.y = y;
        this.color = color;
        this.strokeWidth = strokeWidth;
        this.filled = filled;
    }
    
    // Template method
    public final void render(GraphicsContext gc) {
        setupGraphicsContext(gc);
        draw(gc);
    }
    
    protected void setupGraphicsContext(GraphicsContext gc) {
        gc.setStroke(color);
        gc.setFill(color);
        gc.setLineWidth(strokeWidth);
    }
    
    // Méthode abstraite à implémenter par les sous-classes
    public abstract void draw(GraphicsContext gc);
    
    // Méthode abstraite pour vérifier si un point est dans la forme
    public abstract boolean contains(double x, double y);
    
    // Méthode abstraite pour obtenir les limites de la forme
    public abstract Bounds getBounds();
    
    // Getters et setters
    public double getX() { return x; }
    public void setX(double x) { this.x = x; }
    
    public double getY() { return y; }
    public void setY(double y) { this.y = y; }
    
    public Color getColor() { return color; }
    public void setColor(Color color) { this.color = color; }
    
    public double getStrokeWidth() { return strokeWidth; }
    public void setStrokeWidth(double strokeWidth) { this.strokeWidth = strokeWidth; }
    
    public boolean isFilled() { return filled; }
    public void setFilled(boolean filled) { this.filled = filled; }
    
    // Méthode pour cloner la forme
    public abstract Shape clone();
    
    // Classe interne pour représenter les limites d'une forme
    public static class Bounds {
        private final double minX, minY, maxX, maxY;
        
        public Bounds(double minX, double minY, double maxX, double maxY) {
            this.minX = minX;
            this.minY = minY;
            this.maxX = maxX;
            this.maxY = maxY;
        }
        
        public double getMinX() { return minX; }
        public double getMinY() { return minY; }
        public double getMaxX() { return maxX; }
        public double getMaxY() { return maxY; }
        public double getWidth() { return maxX - minX; }
        public double getHeight() { return maxY - minY; }
    }
}
