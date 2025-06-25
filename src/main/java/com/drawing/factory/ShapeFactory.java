package com.drawing.factory;

import com.drawing.model.shapes.*;
import javafx.scene.paint.Color;

/**
 * Factory pour créer des formes géométriques
 * Implémente le pattern Factory Method
 */
public class ShapeFactory {
    
    public enum ShapeType {
        RECTANGLE, CIRCLE, LINE, ELLIPSE, TRIANGLE
    }
    
    /**
     * Crée une forme selon le type spécifié
     */
    public static Shape createShape(ShapeType type, double[] parameters, 
                                  Color color, double strokeWidth, boolean filled) {
        switch (type) {
            case RECTANGLE:
                if (parameters.length >= 4) {
                    return new Rectangle(parameters[0], parameters[1], 
                                       parameters[2], parameters[3], 
                                       color, strokeWidth, filled);
                }
                break;
                
            case CIRCLE:
                if (parameters.length >= 3) {
                    return new Circle(parameters[0], parameters[1], parameters[2], 
                                    color, strokeWidth, filled);
                }
                break;
                
            case LINE:
                if (parameters.length >= 4) {
                    return new Line(parameters[0], parameters[1], 
                                  parameters[2], parameters[3], 
                                  color, strokeWidth);
                }
                break;
                
            case ELLIPSE:
                if (parameters.length >= 4) {
                    return new Ellipse(parameters[0], parameters[1], 
                                     parameters[2], parameters[3], 
                                     color, strokeWidth, filled);
                }
                break;
                
            case TRIANGLE:
                if (parameters.length >= 6) {
                    return new Triangle(parameters[0], parameters[1], 
                                      parameters[2], parameters[3], 
                                      parameters[4], parameters[5], 
                                      color, strokeWidth, filled);
                }
                break;
        }
        
        throw new IllegalArgumentException("Paramètres invalides pour le type de forme: " + type);
    }
    
    /**
     * Crée une forme par défaut selon le type
     */
    public static Shape createDefaultShape(ShapeType type, double x, double y) {
        Color defaultColor = Color.BLACK;
        double defaultStrokeWidth = 2.0;
        boolean defaultFilled = false;
        
        switch (type) {
            case RECTANGLE:
                return new Rectangle(x, y, 100, 60, defaultColor, defaultStrokeWidth, defaultFilled);
                
            case CIRCLE:
                return new Circle(x, y, 30, defaultColor, defaultStrokeWidth, defaultFilled);
                
            case LINE:
                return new Line(x, y, x + 100, y + 50, defaultColor, defaultStrokeWidth);
                
            case ELLIPSE:
                return new Ellipse(x, y, 80, 50, defaultColor, defaultStrokeWidth, defaultFilled);
                
            case TRIANGLE:
                return new Triangle(x, y, x + 50, y - 60, x + 100, y, 
                                  defaultColor, defaultStrokeWidth, defaultFilled);
                
            default:
                throw new IllegalArgumentException("Type de forme non supporté: " + type);
        }
    }
    
    /**
     * Obtient tous les types de formes disponibles
     */
    public static ShapeType[] getAvailableShapeTypes() {
        return ShapeType.values();
    }
}
