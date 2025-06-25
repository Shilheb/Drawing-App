package com.drawing.model.shapes;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Classe représentant un cercle
 */
public class Circle extends Shape {
    private double radius;
    
    public Circle(double centerX, double centerY, double radius, 
                 Color color, double strokeWidth, boolean filled) {
        super(centerX, centerY, color, strokeWidth, filled);
        this.radius = radius;
    }
    
    @Override
    public void draw(GraphicsContext gc) {
        double diameter = radius * 2;
        double topLeftX = x - radius;
        double topLeftY = y - radius;
        
        if (filled) {
            gc.fillOval(topLeftX, topLeftY, diameter, diameter);
        } else {
            gc.strokeOval(topLeftX, topLeftY, diameter, diameter);
        }
    }
    
    @Override
    public boolean contains(double pointX, double pointY) {
        double distance = Math.sqrt(Math.pow(pointX - x, 2) + Math.pow(pointY - y, 2));
        return distance <= radius;
    }
    
    @Override
    public Bounds getBounds() {
        return new Bounds(x - radius, y - radius, x + radius, y + radius);
    }
    
    @Override
    public Shape clone() {
        return new Circle(x, y, radius, color, strokeWidth, filled);
    }
    
    // Getters et setters spécifiques
    public double getRadius() { return radius; }
    public void setRadius(double radius) { this.radius = radius; }
    
    public double getCenterX() { return x; }
    public double getCenterY() { return y; }
    
    @Override
    public String toString() {
        return String.format("Circle[centerX=%.1f, centerY=%.1f, radius=%.1f, color=%s]", 
                           x, y, radius, color);
    }
}
