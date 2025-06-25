package com.drawing.command;

import com.drawing.model.drawing.Drawing;
import com.drawing.model.shapes.Rectangle;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour CommandManager
 */
public class CommandManagerTest {
    
    private CommandManager commandManager;
    private Drawing drawing;
    private Rectangle testShape;
    
    @BeforeEach
    public void setUp() {
        commandManager = new CommandManager();
        drawing = new Drawing("Test Drawing");
        testShape = new Rectangle(10, 20, 100, 50, Color.RED, 2.0, false);
    }
    
    @Test
    public void testExecuteCommand() {
        AddShapeCommand command = new AddShapeCommand(drawing, testShape);
        
        commandManager.executeCommand(command);
        
        assertEquals(1, drawing.getShapeCount());
        assertTrue(drawing.getShapes().contains(testShape));
        assertTrue(commandManager.canUndo());
        assertFalse(commandManager.canRedo());
    }
    
    @Test
    public void testUndo() {
        AddShapeCommand command = new AddShapeCommand(drawing, testShape);
        commandManager.executeCommand(command);
        
        boolean undone = commandManager.undo();
        
        assertTrue(undone);
        assertEquals(0, drawing.getShapeCount());
        assertFalse(drawing.getShapes().contains(testShape));
        assertFalse(commandManager.canUndo());
        assertTrue(commandManager.canRedo());
    }
    
    @Test
    public void testRedo() {
        AddShapeCommand command = new AddShapeCommand(drawing, testShape);
        commandManager.executeCommand(command);
        commandManager.undo();
        
        boolean redone = commandManager.redo();
        
        assertTrue(redone);
        assertEquals(1, drawing.getShapeCount());
        assertTrue(drawing.getShapes().contains(testShape));
        assertTrue(commandManager.canUndo());
        assertFalse(commandManager.canRedo());
    }
    
    @Test
    public void testMultipleCommands() {
        Rectangle shape1 = new Rectangle(0, 0, 50, 50, Color.RED, 1.0, false);
        Rectangle shape2 = new Rectangle(100, 100, 60, 60, Color.BLUE, 1.0, false);
        
        AddShapeCommand command1 = new AddShapeCommand(drawing, shape1);
        AddShapeCommand command2 = new AddShapeCommand(drawing, shape2);
        
        commandManager.executeCommand(command1);
        commandManager.executeCommand(command2);
        
        assertEquals(2, drawing.getShapeCount());
        
        // Undo dernière commande
        commandManager.undo();
        assertEquals(1, drawing.getShapeCount());
        assertTrue(drawing.getShapes().contains(shape1));
        assertFalse(drawing.getShapes().contains(shape2));
        
        // Undo première commande
        commandManager.undo();
        assertEquals(0, drawing.getShapeCount());
        
        // Redo première commande
        commandManager.redo();
        assertEquals(1, drawing.getShapeCount());
        assertTrue(drawing.getShapes().contains(shape1));
        
        // Redo deuxième commande
        commandManager.redo();
        assertEquals(2, drawing.getShapeCount());
        assertTrue(drawing.getShapes().contains(shape2));
    }
    
    @Test
    public void testRedoStackClearedAfterNewCommand() {
        AddShapeCommand command1 = new AddShapeCommand(drawing, testShape);
        commandManager.executeCommand(command1);
        commandManager.undo();
        
        assertTrue(commandManager.canRedo());
        
        // Exécuter une nouvelle commande
        Rectangle newShape = new Rectangle(200, 200, 30, 30, Color.GREEN, 1.0, false);
        AddShapeCommand command2 = new AddShapeCommand(drawing, newShape);
        commandManager.executeCommand(command2);
        
        // La pile de redo devrait être effacée
        assertFalse(commandManager.canRedo());
        assertTrue(commandManager.canUndo());
    }
    
    @Test
    public void testCannotUndoWhenEmpty() {
        assertFalse(commandManager.canUndo());
        assertFalse(commandManager.undo());
    }
    
    @Test
    public void testCannotRedoWhenEmpty() {
        assertFalse(commandManager.canRedo());
        assertFalse(commandManager.redo());
    }
    
    @Test
    public void testClear() {
        AddShapeCommand command = new AddShapeCommand(drawing, testShape);
        commandManager.executeCommand(command);
        
        assertTrue(commandManager.canUndo());
        
        commandManager.clear();
        
        assertFalse(commandManager.canUndo());
        assertFalse(commandManager.canRedo());
    }
}
