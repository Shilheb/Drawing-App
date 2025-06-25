package com.drawing.model.drawing;

import com.drawing.model.shapes.Rectangle;
import com.drawing.model.shapes.Circle;
import com.drawing.model.shapes.Shape;
import com.drawing.observer.DrawingObserver;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour Drawing
 */
public class DrawingTest {
    
    private Drawing drawing;
    private TestObserver observer;
    
    @BeforeEach
    public void setUp() {
        drawing = new Drawing("Test Drawing");
        observer = new TestObserver();
        drawing.addObserver(observer);
    }
    
    @Test
    public void testAddShape() {
        Rectangle rect = new Rectangle(10, 20, 100, 50, Color.RED, 2.0, false);
        
        drawing.addShape(rect);
        
        assertEquals(1, drawing.getShapeCount());
        assertTrue(drawing.getShapes().contains(rect));
        assertTrue(drawing.isModified());
        assertTrue(observer.shapeAdded);
    }
    
    @Test
    public void testRemoveShape() {
        Rectangle rect = new Rectangle(10, 20, 100, 50, Color.RED, 2.0, false);
        drawing.addShape(rect);
        observer.reset();
        
        boolean removed = drawing.removeShape(rect);
        
        assertTrue(removed);
        assertEquals(0, drawing.getShapeCount());
        assertFalse(drawing.getShapes().contains(rect));
        assertTrue(observer.shapeRemoved);
    }
    
    @Test
    public void testFindShapeAt() {
        Rectangle rect = new Rectangle(10, 20, 100, 50, Color.RED, 2.0, false);
        Circle circle = new Circle(200, 200, 30, Color.BLUE, 2.0, false);
        
        drawing.addShape(rect);
        drawing.addShape(circle);
        
        // Point dans le rectangle
        Shape found = drawing.findShapeAt(50, 40);
        assertEquals(rect, found);
        
        // Point dans le cercle
        found = drawing.findShapeAt(200, 200);
        assertEquals(circle, found);
        
        // Point nulle part
        found = drawing.findShapeAt(500, 500);
        assertNull(found);
    }
    
    @Test
    public void testClear() {
        Rectangle rect = new Rectangle(10, 20, 100, 50, Color.RED, 2.0, false);
        Circle circle = new Circle(200, 200, 30, Color.BLUE, 2.0, false);
        
        drawing.addShape(rect);
        drawing.addShape(circle);
        observer.reset();
        
        drawing.clear();
        
        assertEquals(0, drawing.getShapeCount());
        assertTrue(drawing.getShapes().isEmpty());
        assertTrue(observer.drawingCleared);
    }
    
    @Test
    public void testObserverNotifications() {
        Rectangle rect = new Rectangle(10, 20, 100, 50, Color.RED, 2.0, false);
        
        // Test ajout
        drawing.addShape(rect);
        assertTrue(observer.shapeAdded);
        
        // Test modification
        observer.reset();
        drawing.modifyShape(rect);
        assertTrue(observer.shapeModified);
        
        // Test suppression
        observer.reset();
        drawing.removeShape(rect);
        assertTrue(observer.shapeRemoved);
    }
    
    // Classe d'observateur de test
    private static class TestObserver implements DrawingObserver {
        boolean shapeAdded = false;
        boolean shapeRemoved = false;
        boolean shapeModified = false;
        boolean drawingCleared = false;
        boolean drawingSaved = false;
        boolean drawingLoaded = false;
        
        void reset() {
            shapeAdded = shapeRemoved = shapeModified = 
            drawingCleared = drawingSaved = drawingLoaded = false;
        }
        
        @Override
        public void onShapeAdded(Shape shape) {
            shapeAdded = true;
        }
        
        @Override
        public void onShapeRemoved(Shape shape) {
            shapeRemoved = true;
        }
        
        @Override
        public void onShapeModified(Shape shape) {
            shapeModified = true;
        }
        
        @Override
        public void onDrawingCleared() {
            drawingCleared = true;
        }
        
        @Override
        public void onDrawingSaved(String filename) {
            drawingSaved = true;
        }
        
        @Override
        public void onDrawingLoaded(String filename) {
            drawingLoaded = true;
        }
    }
}
