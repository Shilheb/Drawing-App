package com.drawing.factory;

import com.drawing.model.shapes.*;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour ShapeFactory
 */
public class ShapeFactoryTest {
    
    @Test
    public void testCreateRectangle() {
        double[] params = {10, 20, 100, 50};
        Shape shape = ShapeFactory.createShape(ShapeFactory.ShapeType.RECTANGLE, 
                                             params, Color.RED, 2.0, false);
        
        assertNotNull(shape);
        assertTrue(shape instanceof Rectangle);
        
        Rectangle rect = (Rectangle) shape;
        assertEquals(10, rect.getX());
        assertEquals(20, rect.getY());
        assertEquals(100, rect.getWidth());
        assertEquals(50, rect.getHeight());
        assertEquals(Color.RED, rect.getColor());
    }
    
    @Test
    public void testCreateCircle() {
        double[] params = {50, 60, 30};
        Shape shape = ShapeFactory.createShape(ShapeFactory.ShapeType.CIRCLE, 
                                             params, Color.BLUE, 1.5, true);
        
        assertNotNull(shape);
        assertTrue(shape instanceof Circle);
        
        Circle circle = (Circle) shape;
        assertEquals(50, circle.getCenterX());
        assertEquals(60, circle.getCenterY());
        assertEquals(30, circle.getRadius());
        assertEquals(Color.BLUE, circle.getColor());
        assertTrue(circle.isFilled());
    }
    
    @Test
    public void testCreateLine() {
        double[] params = {0, 0, 100, 100};
        Shape shape = ShapeFactory.createShape(ShapeFactory.ShapeType.LINE, 
                                             params, Color.GREEN, 3.0, false);
        
        assertNotNull(shape);
        assertTrue(shape instanceof Line);
        
        Line line = (Line) shape;
        assertEquals(0, line.getStartX());
        assertEquals(0, line.getStartY());
        assertEquals(100, line.getEndX());
        assertEquals(100, line.getEndY());
        assertEquals(Color.GREEN, line.getColor());
    }
    
    @Test
    public void testCreateDefaultShapes() {
        for (ShapeFactory.ShapeType type : ShapeFactory.ShapeType.values()) {
            Shape shape = ShapeFactory.createDefaultShape(type, 100, 100);
            assertNotNull(shape, "Default shape should not be null for type: " + type);
            assertEquals(100, shape.getX());
            assertEquals(100, shape.getY());
        }
    }
    
    @Test
    public void testInvalidParameters() {
        double[] invalidParams = {10, 20}; // Pas assez de paramètres pour un rectangle
        
        assertThrows(IllegalArgumentException.class, () -> {
            ShapeFactory.createShape(ShapeFactory.ShapeType.RECTANGLE, 
                                   invalidParams, Color.BLACK, 1.0, false);
        });
    }
}
