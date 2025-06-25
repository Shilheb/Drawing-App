package com.drawing.observer;

import com.drawing.model.shapes.Shape;

/**
 * Interface Observer pour les changements dans le dessin
 */
public interface DrawingObserver {
    
    /**
     * Appelé quand une forme est ajoutée au dessin
     */
    void onShapeAdded(Shape shape);
    
    /**
     * Appelé quand une forme est supprimée du dessin
     */
    void onShapeRemoved(Shape shape);
    
    /**
     * Appelé quand une forme est modifiée
     */
    void onShapeModified(Shape shape);
    
    /**
     * Appelé quand le dessin est effacé
     */
    void onDrawingCleared();
    
    /**
     * Appelé quand le dessin est sauvegardé
     */
    void onDrawingSaved(String filename);
    
    /**
     * Appelé quand un dessin est chargé
     */
    void onDrawingLoaded(String filename);
}
