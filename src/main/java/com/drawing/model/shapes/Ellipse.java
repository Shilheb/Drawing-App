package com.drawing.model.shapes;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Classe représentant une ellipse
 */
public class Ellipse extends Shape {
    private double radiusX, radiusY;
    
    public Ellipse(double centerX, double centerY, double radiusX, double radiusY, 
                  Color color, double strokeWidth, boolean filled) {
        super(centerX, centerY, color, strokeWidth, filled);
        this.radiusX = radiusX;
        this.radiusY = radiusY;
    }
    
    @Override
    public void draw(GraphicsContext gc) {
        double width = radiusX * 2;
        double height = radiusY * 2;
        double topLeftX = x - radiusX;
        double topLeftY = y - radiusY;
        
        if (filled) {
            gc.fillOval(topLeftX, topLeftY, width, height);
        } else {
            gc.strokeOval(topLeftX, topLeftY, width, height);
        }
    }
    
    @Override
    public boolean contains(double pointX, double pointY) {
        // Équation de l'ellipse: (x-h)²/a² + (y-k)²/b² <= 1
        double dx = pointX - x;
        double dy = pointY - y;
        return (dx * dx) / (radiusX * radiusX) + (dy * dy) / (radiusY * radiusY) <= 1;
    }
    
    @Override
    public Bounds getBounds() {
        return new Bounds(x - radiusX, y - radiusY, x + radiusX, y + radiusY);
    }
    
    @Override
    public Shape clone() {
        return new Ellipse(x, y, radiusX, radiusY, color, strokeWidth, filled);
    }
    
    // Getters et setters spécifiques
    public double getRadiusX() { return radiusX; }
    public void setRadiusX(double radiusX) { this.radiusX = radiusX; }
    
    public double getRadiusY() { return radiusY; }
    public void setRadiusY(double radiusY) { this.radiusY = radiusY; }
    
    public double getCenterX() { return x; }
    public double getCenterY() { return y; }
    
    @Override
    public String toString() {
        return String.format("Ellipse[centerX=%.1f, centerY=%.1f, radiusX=%.1f, radiusY=%.1f, color=%s]", 
                           x, y, radiusX, radiusY, color);
    }
}
