package com.drawing.model.shapes;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Classe représentant un triangle
 */
public class Triangle extends Shape {
    private double x2, y2, x3, y3;
    
    public Triangle(double x1, double y1, double x2, double y2, double x3, double y3,
                   Color color, double strokeWidth, boolean filled) {
        super(x1, y1, color, strokeWidth, filled);
        this.x2 = x2;
        this.y2 = y2;
        this.x3 = x3;
        this.y3 = y3;
    }
    
    @Override
    public void draw(GraphicsContext gc) {
        double[] xPoints = {x, x2, x3};
        double[] yPoints = {y, y2, y3};
        
        if (filled) {
            gc.fillPolygon(xPoints, yPoints, 3);
        } else {
            gc.strokePolygon(xPoints, yPoints, 3);
        }
    }
    
    @Override
    public boolean contains(double pointX, double pointY) {
        // Utilise les coordonnées barycentriques pour déterminer si le point est dans le triangle
        double denominator = ((y2 - y3) * (x - x3) + (x3 - x2) * (y - y3));
        if (Math.abs(denominator) < 1e-10) return false;
        
        double a = ((y2 - y3) * (pointX - x3) + (x3 - x2) * (pointY - y3)) / denominator;
        double b = ((y3 - y) * (pointX - x3) + (x - x3) * (pointY - y3)) / denominator;
        double c = 1 - a - b;
        
        return a >= 0 && b >= 0 && c >= 0;
    }
    
    @Override
    public Bounds getBounds() {
        double minX = Math.min(Math.min(x, x2), x3);
        double minY = Math.min(Math.min(y, y2), y3);
        double maxX = Math.max(Math.max(x, x2), x3);
        double maxY = Math.max(Math.max(y, y2), y3);
        
        return new Bounds(minX, minY, maxX, maxY);
    }
    
    @Override
    public Shape clone() {
        return new Triangle(x, y, x2, y2, x3, y3, color, strokeWidth, filled);
    }
    
    // Getters et setters spécifiques
    public double getX1() { return x; }
    public double getY1() { return y; }
    
    public double getX2() { return x2; }
    public void setX2(double x2) { this.x2 = x2; }
    
    public double getY2() { return y2; }
    public void setY2(double y2) { this.y2 = y2; }
    
    public double getX3() { return x3; }
    public void setX3(double x3) { this.x3 = x3; }
    
    public double getY3() { return y3; }
    public void setY3(double y3) { this.y3 = y3; }
    
    @Override
    public String toString() {
        return String.format("Triangle[p1=(%.1f,%.1f), p2=(%.1f,%.1f), p3=(%.1f,%.1f), color=%s]", 
                           x, y, x2, y2, x3, y3, color);
    }
}
