package com.drawing.command;

import com.drawing.model.drawing.Drawing;
import com.drawing.model.shapes.Shape;

/**
 * Commande pour ajouter une forme au dessin
 */
public class AddShapeCommand implements Command {
    
    private Drawing drawing;
    private Shape shape;
    
    public AddShapeCommand(Drawing drawing, Shape shape) {
        this.drawing = drawing;
        this.shape = shape;
    }
    
    @Override
    public void execute() {
        drawing.addShape(shape);
    }
    
    @Override
    public void undo() {
        drawing.removeShape(shape);
    }
    
    @Override
    public String getDescription() {
        return "Ajouter " + shape.getClass().getSimpleName();
    }
}
