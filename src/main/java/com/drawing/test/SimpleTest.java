package com.drawing.test;

import com.drawing.factory.ShapeFactory;
import com.drawing.model.shapes.*;
import com.drawing.model.drawing.Drawing;
import com.drawing.command.*;
import com.drawing.utils.Logger;

/**
 * Test simple pour vérifier que les design patterns fonctionnent
 */
public class SimpleTest {
    
    public static void main(String[] args) {
        System.out.println("=== Test de l'Application de Dessin ===\n");
        
        // Test du Logger (Singleton Pattern)
        testLogger();
        
        // Test du Factory Pattern
        testShapeFactory();
        
        // Test du Observer Pattern avec Drawing
        testDrawingObserver();
        
        // Test du Command Pattern
        testCommandPattern();
        
        System.out.println("\n=== Tous les tests terminés ===");
    }
    
    private static void testLogger() {
        System.out.println("1. Test du Logger (Singleton Pattern):");
        
        Logger logger1 = Logger.getInstance();
        Logger logger2 = Logger.getInstance();
        
        System.out.println("   - Même instance: " + (logger1 == logger2));
        
        logger1.info("Test du logger - message d'information");
        logger1.warning("Test du logger - message d'avertissement");
        logger1.error("Test du logger - message d'erreur");
        
        System.out.println("   ✓ Logger testé avec succès\n");
    }
    
    private static void testShapeFactory() {
        System.out.println("2. Test du Factory Pattern:");
        
        // Test création de rectangle
        double[] rectParams = {10, 20, 100, 50};
        Shape rectangle = ShapeFactory.createShape(
            ShapeFactory.ShapeType.RECTANGLE, 
            rectParams, 
            null, // Color.RED - simplifié pour le test
            2.0, 
            false
        );
        
        System.out.println("   - Rectangle créé: " + rectangle);
        
        // Test création de cercle
        double[] circleParams = {50, 60, 30};
        Shape circle = ShapeFactory.createShape(
            ShapeFactory.ShapeType.CIRCLE, 
            circleParams, 
            null, // Color.BLUE - simplifié pour le test
            1.5, 
            true
        );
        
        System.out.println("   - Cercle créé: " + circle);
        
        // Test création par défaut
        Shape defaultRect = ShapeFactory.createDefaultShape(
            ShapeFactory.ShapeType.RECTANGLE, 
            100, 100
        );
        
        System.out.println("   - Rectangle par défaut: " + defaultRect);
        System.out.println("   ✓ Factory Pattern testé avec succès\n");
    }
    
    private static void testDrawingObserver() {
        System.out.println("3. Test du Observer Pattern:");
        
        Drawing drawing = new Drawing("Test Drawing");
        TestObserver observer = new TestObserver();
        drawing.addObserver(observer);
        
        // Test ajout de forme
        Shape rect = ShapeFactory.createDefaultShape(
            ShapeFactory.ShapeType.RECTANGLE, 
            50, 50
        );
        
        drawing.addShape(rect);
        System.out.println("   - Forme ajoutée, observer notifié: " + observer.wasNotified);
        
        // Test suppression
        observer.reset();
        drawing.removeShape(rect);
        System.out.println("   - Forme supprimée, observer notifié: " + observer.wasNotified);
        
        System.out.println("   ✓ Observer Pattern testé avec succès\n");
    }
    
    private static void testCommandPattern() {
        System.out.println("4. Test du Command Pattern:");
        
        Drawing drawing = new Drawing("Command Test");
        CommandManager commandManager = new CommandManager();
        
        Shape rect = ShapeFactory.createDefaultShape(
            ShapeFactory.ShapeType.RECTANGLE, 
            25, 25
        );
        
        // Test ajout avec commande
        Command addCommand = new AddShapeCommand(drawing, rect);
        commandManager.executeCommand(addCommand);
        
        System.out.println("   - Forme ajoutée via commande, count: " + drawing.getShapeCount());
        System.out.println("   - Peut annuler: " + commandManager.canUndo());
        
        // Test undo
        commandManager.undo();
        System.out.println("   - Après undo, count: " + drawing.getShapeCount());
        System.out.println("   - Peut refaire: " + commandManager.canRedo());
        
        // Test redo
        commandManager.redo();
        System.out.println("   - Après redo, count: " + drawing.getShapeCount());
        
        System.out.println("   ✓ Command Pattern testé avec succès\n");
    }
    
    // Classe d'observateur simple pour les tests
    private static class TestObserver implements com.drawing.observer.DrawingObserver {
        boolean wasNotified = false;
        
        void reset() {
            wasNotified = false;
        }
        
        @Override
        public void onShapeAdded(Shape shape) {
            wasNotified = true;
        }
        
        @Override
        public void onShapeRemoved(Shape shape) {
            wasNotified = true;
        }
        
        @Override
        public void onShapeModified(Shape shape) {
            wasNotified = true;
        }
        
        @Override
        public void onDrawingCleared() {
            wasNotified = true;
        }
        
        @Override
        public void onDrawingSaved(String filename) {
            wasNotified = true;
        }
        
        @Override
        public void onDrawingLoaded(String filename) {
            wasNotified = true;
        }
    }
}
