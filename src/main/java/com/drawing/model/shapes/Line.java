package com.drawing.model.shapes;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Classe représentant une ligne
 */
public class Line extends Shape {
    private double endX, endY;
    
    public Line(double startX, double startY, double endX, double endY, 
               Color color, double strokeWidth) {
        super(startX, startY, color, strokeWidth, false); // Une ligne ne peut pas être remplie
        this.endX = endX;
        this.endY = endY;
    }
    
    @Override
    public void draw(GraphicsContext gc) {
        gc.strokeLine(x, y, endX, endY);
    }
    
    @Override
    public boolean contains(double pointX, double pointY) {
        // Calcul de la distance d'un point à une ligne
        double A = endY - y;
        double B = x - endX;
        double C = endX * y - x * endY;
        
        double distance = Math.abs(A * pointX + B * pointY + C) / 
                         Math.sqrt(A * A + B * B);
        
        // Vérifier si le point est proche de la ligne (tolérance de 5 pixels)
        return distance <= 5.0;
    }
    
    @Override
    public Bounds getBounds() {
        double minX = Math.min(x, endX);
        double minY = Math.min(y, endY);
        double maxX = Math.max(x, endX);
        double maxY = Math.max(y, endY);
        
        return new Bounds(minX, minY, maxX, maxY);
    }
    
    @Override
    public Shape clone() {
        return new Line(x, y, endX, endY, color, strokeWidth);
    }
    
    // Getters et setters spécifiques
    public double getStartX() { return x; }
    public double getStartY() { return y; }
    
    public double getEndX() { return endX; }
    public void setEndX(double endX) { this.endX = endX; }
    
    public double getEndY() { return endY; }
    public void setEndY(double endY) { this.endY = endY; }
    
    public double getLength() {
        return Math.sqrt(Math.pow(endX - x, 2) + Math.pow(endY - y, 2));
    }
    
    @Override
    public String toString() {
        return String.format("Line[start=(%.1f,%.1f), end=(%.1f,%.1f), color=%s]", 
                           x, y, endX, endY, color);
    }
}
