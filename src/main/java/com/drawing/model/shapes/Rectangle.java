package com.drawing.model.shapes;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Classe représentant un rectangle
 */
public class Rectangle extends Shape {
    private double width, height;
    
    public Rectangle(double x, double y, double width, double height, 
                    Color color, double strokeWidth, boolean filled) {
        super(x, y, color, strokeWidth, filled);
        this.width = width;
        this.height = height;
    }
    
    @Override
    public void draw(GraphicsContext gc) {
        if (filled) {
            gc.fillRect(x, y, width, height);
        } else {
            gc.strokeRect(x, y, width, height);
        }
    }
    
    @Override
    public boolean contains(double pointX, double pointY) {
        return pointX >= x && pointX <= x + width && 
               pointY >= y && pointY <= y + height;
    }
    
    @Override
    public Bounds getBounds() {
        return new Bounds(x, y, x + width, y + height);
    }
    
    @Override
    public Shape clone() {
        return new Rectangle(x, y, width, height, color, strokeWidth, filled);
    }
    
    // Getters et setters spécifiques
    public double getWidth() { return width; }
    public void setWidth(double width) { this.width = width; }
    
    public double getHeight() { return height; }
    public void setHeight(double height) { this.height = height; }
    
    @Override
    public String toString() {
        return String.format("Rectangle[x=%.1f, y=%.1f, width=%.1f, height=%.1f, color=%s]", 
                           x, y, width, height, color);
    }
}
