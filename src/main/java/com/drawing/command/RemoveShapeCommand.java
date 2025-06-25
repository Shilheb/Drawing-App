package com.drawing.command;

import com.drawing.model.drawing.Drawing;
import com.drawing.model.shapes.Shape;

/**
 * Commande pour supprimer une forme du dessin
 */
public class RemoveShapeCommand implements Command {
    
    private Drawing drawing;
    private Shape shape;
    
    public RemoveShapeCommand(Drawing drawing, Shape shape) {
        this.drawing = drawing;
        this.shape = shape;
    }
    
    @Override
    public void execute() {
        drawing.removeShape(shape);
    }
    
    @Override
    public void undo() {
        drawing.addShape(shape);
    }
    
    @Override
    public String getDescription() {
        return "Supprimer " + shape.getClass().getSimpleName();
    }
}
