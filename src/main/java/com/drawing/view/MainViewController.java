package com.drawing.view;

import com.drawing.controller.DrawingController;
import com.drawing.model.shapes.Shape;
import com.drawing.observer.DrawingObserver;
import com.drawing.factory.ShapeFactory;
import com.drawing.utils.Logger;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.stage.FileChooser;
import javafx.embed.swing.SwingFXUtils;
import javax.imageio.ImageIO;
import java.io.File;

/**
 * Contrôleur de vue principal pour l'interface JavaFX
 */
public class MainViewController implements Initializable, DrawingObserver {
    
    // Éléments FXML
    @FXML private Canvas drawingCanvas;
    @FXML private VBox shapeButtonsContainer;
    @FXML private ColorPicker colorPicker;
    @FXML private Slider strokeWidthSlider;
    @FXML private Label strokeWidthLabel;
    @FXML private CheckBox fillCheckBox;
    @FXML private Label statusLabel;
    @FXML private Label coordinatesLabel;
    @FXML private MenuItem undoMenuItem;
    @FXML private MenuItem redoMenuItem;
    @FXML private Button deleteButton;
    
    // Boutons de formes
    @FXML private ToggleButton rectangleButton;
    @FXML private ToggleButton circleButton;
    @FXML private ToggleButton lineButton;
    @FXML private ToggleButton ellipseButton;
    @FXML private ToggleButton triangleButton;
    @FXML private ToggleButton nodeButton;
    @FXML private ToggleButton edgeButton;
    
    // Dimension controls
    @FXML private VBox dimensionBox;
    @FXML private HBox rectangleDimensions;
    @FXML private TextField rectWidthField;
    @FXML private TextField rectHeightField;
    @FXML private HBox circleDimensions;
    @FXML private TextField circleRadiusField;
    @FXML private HBox ellipseDimensions;
    @FXML private TextField ellipseRadiusXField;
    @FXML private TextField ellipseRadiusYField;
    @FXML private HBox lineDimensions;
    @FXML private TextField lineEndXField;
    @FXML private TextField lineEndYField;
    @FXML private HBox triangleDimensions;
    @FXML private TextField triangleX2Field;
    @FXML private TextField triangleY2Field;
    @FXML private TextField triangleX3Field;
    @FXML private TextField triangleY3Field;
    @FXML private TextField nodeAField;
    @FXML private TextField nodeBField;
    
    private DrawingController controller;
    private ToggleGroup shapeToggleGroup;
    private boolean deleteMode = false;
    private GraphicsContext gc;
    
    // Selected shape for editing
    private Shape selectedShape = null;

    // --- Drag and drop support ---
    private double dragOffsetX = 0;
    private double dragOffsetY = 0;
    private boolean dragging = false;

    // --- Interactive shape creation ---
    private boolean creatingShape = false;
    private double creationStartX, creationStartY;
    private Shape previewShape = null;

    // --- Resize handles ---
    private static final double HANDLE_SIZE = 8;
    private enum Handle { NONE, TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT }
    private Handle activeHandle = Handle.NONE;

    // --- Graph drawing support ---
    private enum ToolMode { SHAPE, NODE, EDGE }
    private ToolMode toolMode = ToolMode.SHAPE;
    private List<GraphNode> graphNodes = new java.util.ArrayList<>();
    private List<GraphEdge> graphEdges = new java.util.ArrayList<>();
    private GraphNode selectedNodeA = null, selectedNodeB = null;
    private GraphNode edgeStartNode = null;

    // Graph node/edge classes
    private static class GraphNode {
        double x, y;
        int id;
        GraphNode(double x, double y, int id) { this.x = x; this.y = y; this.id = id; }
    }
    private static class GraphEdge {
        GraphNode from, to;
        double weight;
        GraphEdge(GraphNode from, GraphNode to, double weight) { this.from = from; this.to = to; this.weight = weight; }
    }

    // --- Shortest path calculation ---
    private List<GraphEdge> shortestPath = new java.util.ArrayList<>();
    // Call this method from a button/menu to compute and highlight the shortest path
    private void computeAndHighlightShortestPath(GraphNode start, GraphNode end) {
        shortestPath.clear();
        if (start == null || end == null) return;
        // Dijkstra's algorithm
        java.util.Map<GraphNode, Double> dist = new java.util.HashMap<>();
        java.util.Map<GraphNode, GraphEdge> prevEdge = new java.util.HashMap<>();
        java.util.Set<GraphNode> visited = new java.util.HashSet<>();
        java.util.PriorityQueue<GraphNode> queue = new java.util.PriorityQueue<>((a, b) -> Double.compare(dist.getOrDefault(a, Double.POSITIVE_INFINITY), dist.getOrDefault(b, Double.POSITIVE_INFINITY)));
        for (GraphNode n : graphNodes) dist.put(n, Double.POSITIVE_INFINITY);
        dist.put(start, 0.0);
        queue.add(start);
        while (!queue.isEmpty()) {
            GraphNode u = queue.poll();
            if (!visited.add(u)) continue;
            if (u == end) break;
            for (GraphEdge e : graphEdges) {
                if (e.from == u) {
                    double alt = dist.get(u) + e.weight;
                    if (alt < dist.get(e.to)) {
                        dist.put(e.to, alt);
                        prevEdge.put(e.to, e);
                        queue.add(e.to);
                    }
                }
            }
        }
        // Reconstruct path
        GraphNode curr = end;
        while (curr != start && prevEdge.containsKey(curr)) {
            GraphEdge e = prevEdge.get(curr);
            shortestPath.add(0, e);
            curr = e.from;
        }
        redrawCanvas();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        controller = new DrawingController();
        controller.getCurrentDrawing().addObserver(this);
        
        gc = drawingCanvas.getGraphicsContext2D();
        
        setupShapeButtons();
        setupCanvas();
        setupControls();
        
        updateUI();
        
        Logger.getInstance().info("Interface utilisateur initialisée");
    }
    
    private void setupShapeButtons() {
        shapeToggleGroup = new ToggleGroup();
        rectangleButton.setToggleGroup(shapeToggleGroup);
        circleButton.setToggleGroup(shapeToggleGroup);
        lineButton.setToggleGroup(shapeToggleGroup);
        ellipseButton.setToggleGroup(shapeToggleGroup);
        triangleButton.setToggleGroup(shapeToggleGroup);
        nodeButton.setToggleGroup(shapeToggleGroup);
        edgeButton.setToggleGroup(shapeToggleGroup);
        
        // Sélectionner le rectangle par défaut
        rectangleButton.setSelected(true);
    }
    
    private void setupCanvas() {
        drawingCanvas.setOnMouseClicked(this::onCanvasClicked);
        drawingCanvas.setOnMouseMoved(this::onCanvasMouseMoved);
        drawingCanvas.setOnMousePressed(this::onCanvasMousePressed);
        drawingCanvas.setOnMouseDragged(this::onCanvasMouseDragged);
        drawingCanvas.setOnMouseReleased(this::onCanvasMouseReleased);
        // Fond blanc
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, drawingCanvas.getWidth(), drawingCanvas.getHeight());
    }

    private void onCanvasMousePressed(MouseEvent event) {
        double x = event.getX();
        double y = event.getY();
        if (selectedShape != null) {
            Handle h = getHandleAt(x, y);
            if (h != Handle.NONE) {
                activeHandle = h;
                dragging = false;
                return;
            }
        }
        selectShapeAt(x, y);
        if (selectedShape == null && !deleteMode) {
            // Start creating a new shape
            creatingShape = true;
            creationStartX = x;
            creationStartY = y;
            previewShape = createPreviewShape(x, y, x, y);
        } else if (selectedShape != null) {
            dragOffsetX = x - selectedShape.getX();
            dragOffsetY = y - selectedShape.getY();
            dragging = true;
        }
        // --- Graph drawing support ---
        if (toolMode == ToolMode.NODE) {
            // Add a node
            GraphNode node = new GraphNode(x, y, graphNodes.size());
            graphNodes.add(node);
            // Node selection for shortest path
            if (selectedNodeA == null) {
                selectedNodeA = node;
                statusLabel.setText("Noeud A sélectionné (id=" + node.id + ")");
            } else if (selectedNodeB == null) {
                selectedNodeB = node;
                statusLabel.setText("Noeud B sélectionné (id=" + node.id + ")");
            } else {
                selectedNodeA = node;
                selectedNodeB = null;
                statusLabel.setText("Noeud A réinitialisé (id=" + node.id + ")");
            }
            redrawCanvas();
            return;
        } else if (toolMode == ToolMode.EDGE) {
            // Select two nodes to create an edge
            GraphNode node = findGraphNodeAt(x, y);
            if (node != null) {
                if (edgeStartNode == null) {
                    edgeStartNode = node;
                } else if (edgeStartNode != node) {
                    graphEdges.add(new GraphEdge(edgeStartNode, node, 1.0)); // Default weight 1.0
                    edgeStartNode = null;
                    redrawCanvas();
                }
            }
            return;
        }
    }

    private void onCanvasMouseDragged(MouseEvent event) {
        double x = event.getX();
        double y = event.getY();
        if (activeHandle != Handle.NONE && selectedShape != null) {
            resizeSelectedShape(activeHandle, x, y);
            controller.getCurrentDrawing().modifyShape(selectedShape);
            redrawCanvas();
            updateDimensionControls();
            return;
        }
        if (creatingShape && previewShape != null) {
            // Update preview shape size
            previewShape = createPreviewShape(creationStartX, creationStartY, x, y);
            redrawCanvas();
            drawPreviewShape();
        } else if (dragging && selectedShape != null) {
            double newX = x - dragOffsetX;
            double newY = y - dragOffsetY;
            selectedShape.setX(newX);
            selectedShape.setY(newY);
            controller.getCurrentDrawing().modifyShape(selectedShape);
            redrawCanvas();
            updateDimensionControls();
        }
    }

    private void onCanvasMouseReleased(MouseEvent event) {
        activeHandle = Handle.NONE;
        if (creatingShape && previewShape != null) {
            // Finalize shape creation
            controller.getCurrentDrawing().addShape(previewShape);
            selectedShape = previewShape;
            previewShape = null;
            creatingShape = false;
            redrawCanvas();
            updateDimensionControls();
        }
        dragging = false;
    }
    // --- End interactive shape creation ---
    
    private void updateUI() {
        Platform.runLater(() -> {
            undoMenuItem.setDisable(!controller.canUndo());
            redoMenuItem.setDisable(!controller.canRedo());
            
            String undoText = "Annuler";
            String redoText = "Rétablir";
            
            undoMenuItem.setText(undoText);
            redoMenuItem.setText(redoText);
        });
    }
    
    private void redrawCanvas() {
        // Effacer le canvas
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, drawingCanvas.getWidth(), drawingCanvas.getHeight());
        // Redessiner toutes les formes
        controller.getCurrentDrawing().render(gc);
        if (previewShape != null) drawPreviewShape();
        drawResizeHandles();
        // Draw graph edges
        gc.setStroke(Color.DARKGRAY);
        for (GraphEdge edge : graphEdges) {
            gc.setLineWidth(1);
            gc.strokeLine(edge.from.x, edge.from.y, edge.to.x, edge.to.y);
        }
        // Highlight shortest path
        if (!shortestPath.isEmpty() && selectedNodeA != null && selectedNodeB != null) {
            gc.setStroke(Color.RED);
            gc.setLineWidth(4);
            for (GraphEdge edge : shortestPath) {
                gc.strokeLine(edge.from.x, edge.from.y, edge.to.x, edge.to.y);
            }
            gc.setLineWidth(1);
        }
        // Draw graph nodes (no green highlight)
        for (GraphNode node : graphNodes) {
            gc.setFill(Color.DARKORANGE);
            gc.fillOval(node.x - 10, node.y - 10, 20, 20);
            gc.setFill(Color.BLACK);
            gc.fillText(String.valueOf(node.id), node.x - 4, node.y + 4);
        }
        // Draw shortest path distance if available
        if (!shortestPath.isEmpty() && selectedNodeA != null && selectedNodeB != null) {
            double totalDist = 0.0;
            for (GraphEdge e : shortestPath) totalDist += e.weight;
            double midX = (selectedNodeA.x + selectedNodeB.x) / 2.0;
            double midY = (selectedNodeA.y + selectedNodeB.y) / 2.0;
            gc.setFill(Color.RED);
            gc.fillText(String.format("Distance: %.2f", totalDist), midX + 10, midY - 10);
        }
    }
    
    private void selectShapeAt(double x, double y) {
        selectedShape = controller.getCurrentDrawing().findShapeAt(x, y);
        updateDimensionControls();
    }

    private void updateDimensionControls() {
        // Hide all
        dimensionBox.setVisible(selectedShape != null);
        rectangleDimensions.setVisible(false);
        circleDimensions.setVisible(false);
        ellipseDimensions.setVisible(false);
        lineDimensions.setVisible(false);
        triangleDimensions.setVisible(false);
        if (selectedShape == null) return;
        // Rectangle
        if (selectedShape instanceof com.drawing.model.shapes.Rectangle) {
            rectangleDimensions.setVisible(true);
            rectWidthField.setText(String.valueOf(((com.drawing.model.shapes.Rectangle)selectedShape).getWidth()));
            rectHeightField.setText(String.valueOf(((com.drawing.model.shapes.Rectangle)selectedShape).getHeight()));
        } else if (selectedShape instanceof com.drawing.model.shapes.Circle) {
            circleDimensions.setVisible(true);
            circleRadiusField.setText(String.valueOf(((com.drawing.model.shapes.Circle)selectedShape).getRadius()));
        } else if (selectedShape instanceof com.drawing.model.shapes.Ellipse) {
            ellipseDimensions.setVisible(true);
            ellipseRadiusXField.setText(String.valueOf(((com.drawing.model.shapes.Ellipse)selectedShape).getRadiusX()));
            ellipseRadiusYField.setText(String.valueOf(((com.drawing.model.shapes.Ellipse)selectedShape).getRadiusY()));
        } else if (selectedShape instanceof com.drawing.model.shapes.Line) {
            lineDimensions.setVisible(true);
            lineEndXField.setText(String.valueOf(((com.drawing.model.shapes.Line)selectedShape).getEndX()));
            lineEndYField.setText(String.valueOf(((com.drawing.model.shapes.Line)selectedShape).getEndY()));
        } else if (selectedShape instanceof com.drawing.model.shapes.Triangle) {
            triangleDimensions.setVisible(true);
            triangleX2Field.setText(String.valueOf(((com.drawing.model.shapes.Triangle)selectedShape).getX2()));
            triangleY2Field.setText(String.valueOf(((com.drawing.model.shapes.Triangle)selectedShape).getY2()));
            triangleX3Field.setText(String.valueOf(((com.drawing.model.shapes.Triangle)selectedShape).getX3()));
            triangleY3Field.setText(String.valueOf(((com.drawing.model.shapes.Triangle)selectedShape).getY3()));
        }
    }
    
    // Gestionnaires d'événements du canvas
    @FXML
    private void onCanvasClicked(MouseEvent event) {
        double x = event.getX();
        double y = event.getY();
        if (deleteMode) {
            controller.removeShapeAt(x, y);
            deleteMode = false;
            deleteButton.setText("Supprimer");
            statusLabel.setText("Prêt");
            selectedShape = null;
            updateDimensionControls();
        } else if (toolMode == ToolMode.NODE) {
            // Only add a new node if click is not on an existing node
            GraphNode node = findGraphNodeAt(x, y);
            if (node == null) {
                node = new GraphNode(x, y, graphNodes.size());
                graphNodes.add(node);
                statusLabel.setText("Nouveau noeud ajouté (id=" + node.id + ")");
            }
            redrawCanvas();
            return;
        } else if (toolMode == ToolMode.EDGE) {
            GraphNode node = findGraphNodeAt(x, y);
            if (node != null) {
                if (edgeStartNode == null) {
                    edgeStartNode = node;
                } else if (edgeStartNode != node) {
                    graphEdges.add(new GraphEdge(edgeStartNode, node, 1.0));
                    edgeStartNode = null;
                    redrawCanvas();
                }
            }
            return;
        } else {
            selectShapeAt(x, y);
            if (selectedShape == null) {
                // No shape at this position: add a new one
                controller.addShape(x, y);
                // Select the newly added shape (should be last in list)
                List<Shape> shapes = controller.getCurrentDrawing().getShapes();
                if (!shapes.isEmpty()) {
                    selectedShape = shapes.get(shapes.size() - 1);
                }
                updateDimensionControls();
            }
        }
        redrawCanvas();
        updateUI();
    }
    
    @FXML
    private void onCanvasMouseMoved(MouseEvent event) {
        coordinatesLabel.setText(String.format("(%.0f, %.0f)", event.getX(), event.getY()));
    }
    
    // Gestionnaires d'événements des contrôles
    @FXML
    private void onShapeSelected() {
        ToggleButton selected = (ToggleButton) shapeToggleGroup.getSelectedToggle();
        if (selected != null) {
            String shapeTypeStr = (String) selected.getUserData();
            if ("NODE".equals(shapeTypeStr)) {
                toolMode = ToolMode.NODE;
                statusLabel.setText("Mode outil: Noeud");
                return;
            } else if ("EDGE".equals(shapeTypeStr)) {
                toolMode = ToolMode.EDGE;
                statusLabel.setText("Mode outil: Arête");
                return;
            }
            // Only try to set shape type if not a graph tool
            ShapeFactory.ShapeType shapeType = ShapeFactory.ShapeType.valueOf(shapeTypeStr);
            controller.setSelectedShapeType(shapeType);
            statusLabel.setText("Forme sélectionnée: " + selected.getText());
            toolMode = ToolMode.SHAPE;
        }
    }
    
    @FXML
    private void onColorChanged() {
        Color color = colorPicker.getValue();
        controller.setSelectedColor(color);
        statusLabel.setText("Couleur changée");
    }
    
    @FXML
    private void onStrokeWidthChanged() {
        double width = strokeWidthSlider.getValue();
        controller.setSelectedStrokeWidth(width);
        statusLabel.setText("Épaisseur changée: " + String.format("%.1f", width));
    }
    
    @FXML
    private void onFillModeChanged() {
        boolean fill = fillCheckBox.isSelected();
        controller.setFillMode(fill);
        statusLabel.setText("Mode remplissage: " + (fill ? "Activé" : "Désactivé"));
    }
    
    @FXML
    private void onDeleteMode() {
        deleteMode = !deleteMode;
        if (deleteMode) {
            deleteButton.setText("Annuler suppression");
            statusLabel.setText("Mode suppression - Cliquez sur une forme à supprimer");
        } else {
            deleteButton.setText("Supprimer");
            statusLabel.setText("Prêt");
        }
    }
    
    // Gestionnaires d'événements du menu
    @FXML
    private void onNewDrawing() {
        TextInputDialog dialog = new TextInputDialog("Nouveau dessin");
        dialog.setTitle("Nouveau dessin");
        dialog.setHeaderText("Créer un nouveau dessin");
        dialog.setContentText("Nom du dessin:");
        
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent() && !result.get().trim().isEmpty()) {
            controller.newDrawing(result.get().trim());
            redrawCanvas();
            updateUI();
            statusLabel.setText("Nouveau dessin créé: " + result.get());
        }
    }
    
    @FXML
    private void onSaveDrawing() {
        if (controller.saveDrawing()) {
            statusLabel.setText("Dessin sauvegardé");
        } else {
            statusLabel.setText("Erreur lors de la sauvegarde");
        }
        updateUI();
    }
    
    @FXML
    private void onLoadDrawing() {
        List<String> drawings = controller.getSavedDrawings();
        if (drawings.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Aucun dessin");
            alert.setHeaderText("Aucun dessin sauvegardé");
            alert.setContentText("Il n'y a aucun dessin sauvegardé à charger.");
            alert.showAndWait();
            return;
        }
        
        ChoiceDialog<String> dialog = new ChoiceDialog<>(drawings.get(0), drawings);
        dialog.setTitle("Charger un dessin");
        dialog.setHeaderText("Sélectionner un dessin à charger");
        dialog.setContentText("Dessin:");
        
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            if (controller.loadDrawing(result.get())) {
                redrawCanvas();
                updateUI();
                statusLabel.setText("Dessin chargé: " + result.get());
            } else {
                statusLabel.setText("Erreur lors du chargement");
            }
        }
    }
    
    @FXML
    private void onUndo() {
        if (controller.undo()) {
            redrawCanvas();
            updateUI();
            statusLabel.setText("Action annulée");
        }
    }
    
    @FXML
    private void onRedo() {
        if (controller.redo()) {
            redrawCanvas();
            updateUI();
            statusLabel.setText("Action rétablie");
        }
    }
    
    @FXML
    private void onClearDrawing() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Effacer le dessin");
        alert.setHeaderText("Êtes-vous sûr de vouloir effacer le dessin ?");
        alert.setContentText("Cette action ne peut pas être annulée.");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            controller.clearDrawing();
            redrawCanvas();
            updateUI();
            statusLabel.setText("Dessin effacé");
        }
    }
    
    @FXML
    private void onConsoleLogging() {
        Logger.getInstance().useConsoleLogging();
        statusLabel.setText("Journalisation: Console");
    }
    
    @FXML
    private void onFileLogging() {
        Logger.getInstance().useFileLogging("logs/drawing-app.log");
        statusLabel.setText("Journalisation: Fichier");
    }
    
    @FXML
    private void onDatabaseLogging() {
        Logger.getInstance().useDatabaseLogging("jdbc:h2:./logs");
        statusLabel.setText("Journalisation: Base de données");
    }
    
    @FXML
    private void onExit() {
        controller.cleanup();
        Platform.exit();
    }
    
    // Implémentation de DrawingObserver
    @Override
    public void onShapeAdded(Shape shape) {
        Platform.runLater(this::updateUI);
    }
    
    @Override
    public void onShapeRemoved(Shape shape) {
        Platform.runLater(this::updateUI);
    }
    
    @Override
    public void onShapeModified(Shape shape) {
        Platform.runLater(this::updateUI);
    }
    
    @Override
    public void onDrawingCleared() {
        Platform.runLater(this::updateUI);
    }
    
    @Override
    public void onDrawingSaved(String filename) {
        Platform.runLater(this::updateUI);
    }
    
    @Override
    public void onDrawingLoaded(String filename) {
        Platform.runLater(this::updateUI);
    }

    // --- Dimension editing event handlers ---
    @FXML
    public void onRectWidthChanged() {
        if (selectedShape instanceof com.drawing.model.shapes.Rectangle) {
            try {
                double w = Double.parseDouble(rectWidthField.getText());
                if (w > 0) {
                    ((com.drawing.model.shapes.Rectangle)selectedShape).setWidth(w);
                    controller.getCurrentDrawing().modifyShape(selectedShape);
                    redrawCanvas();
                }
            } catch (NumberFormatException ignored) {}
        }
    }
    @FXML
    public void onRectHeightChanged() {
        if (selectedShape instanceof com.drawing.model.shapes.Rectangle) {
            try {
                double h = Double.parseDouble(rectHeightField.getText());
                if (h > 0) {
                    ((com.drawing.model.shapes.Rectangle)selectedShape).setHeight(h);
                    controller.getCurrentDrawing().modifyShape(selectedShape);
                    redrawCanvas();
                }
            } catch (NumberFormatException ignored) {}
        }
    }
    @FXML
    public void onCircleRadiusChanged() {
        if (selectedShape instanceof com.drawing.model.shapes.Circle) {
            try {
                double r = Double.parseDouble(circleRadiusField.getText());
                if (r > 0) {
                    ((com.drawing.model.shapes.Circle)selectedShape).setRadius(r);
                    controller.getCurrentDrawing().modifyShape(selectedShape);
                    redrawCanvas();
                }
            } catch (NumberFormatException ignored) {}
        }
    }
    @FXML
    public void onEllipseRadiusXChanged() {
        if (selectedShape instanceof com.drawing.model.shapes.Ellipse) {
            try {
                double rx = Double.parseDouble(ellipseRadiusXField.getText());
                if (rx > 0) {
                    ((com.drawing.model.shapes.Ellipse)selectedShape).setRadiusX(rx);
                    controller.getCurrentDrawing().modifyShape(selectedShape);
                    redrawCanvas();
                }
            } catch (NumberFormatException ignored) {}
        }
    }
    @FXML
    public void onEllipseRadiusYChanged() {
        if (selectedShape instanceof com.drawing.model.shapes.Ellipse) {
            try {
                double ry = Double.parseDouble(ellipseRadiusYField.getText());
                if (ry > 0) {
                    ((com.drawing.model.shapes.Ellipse)selectedShape).setRadiusY(ry);
                    controller.getCurrentDrawing().modifyShape(selectedShape);
                    redrawCanvas();
                }
            } catch (NumberFormatException ignored) {}
        }
    }
    @FXML
    public void onLineEndXChanged() {
        if (selectedShape instanceof com.drawing.model.shapes.Line) {
            try {
                double x2 = Double.parseDouble(lineEndXField.getText());
                ((com.drawing.model.shapes.Line)selectedShape).setEndX(x2);
                controller.getCurrentDrawing().modifyShape(selectedShape);
                redrawCanvas();
            } catch (NumberFormatException ignored) {}
        }
    }
    @FXML
    public void onLineEndYChanged() {
        if (selectedShape instanceof com.drawing.model.shapes.Line) {
            try {
                double y2 = Double.parseDouble(lineEndYField.getText());
                ((com.drawing.model.shapes.Line)selectedShape).setEndY(y2);
                controller.getCurrentDrawing().modifyShape(selectedShape);
                redrawCanvas();
            } catch (NumberFormatException ignored) {}
        }
    }
    @FXML
    public void onTriangleX2Changed() {
        if (selectedShape instanceof com.drawing.model.shapes.Triangle) {
            try {
                double x2 = Double.parseDouble(triangleX2Field.getText());
                ((com.drawing.model.shapes.Triangle)selectedShape).setX2(x2);
                controller.getCurrentDrawing().modifyShape(selectedShape);
                redrawCanvas();
            } catch (NumberFormatException ignored) {}
        }
    }
    @FXML
    public void onTriangleY2Changed() {
        if (selectedShape instanceof com.drawing.model.shapes.Triangle) {
            try {
                double y2 = Double.parseDouble(triangleY2Field.getText());
                ((com.drawing.model.shapes.Triangle)selectedShape).setY2(y2);
                controller.getCurrentDrawing().modifyShape(selectedShape);
                redrawCanvas();
            } catch (NumberFormatException ignored) {}
        }
    }
    @FXML
    public void onTriangleX3Changed() {
        if (selectedShape instanceof com.drawing.model.shapes.Triangle) {
            try {
                double x3 = Double.parseDouble(triangleX3Field.getText());
                ((com.drawing.model.shapes.Triangle)selectedShape).setX3(x3);
                controller.getCurrentDrawing().modifyShape(selectedShape);
                redrawCanvas();
            } catch (NumberFormatException ignored) {}
        }
    }
    @FXML
    public void onTriangleY3Changed() {
        if (selectedShape instanceof com.drawing.model.shapes.Triangle) {
            try {
                double y3 = Double.parseDouble(triangleY3Field.getText());
                ((com.drawing.model.shapes.Triangle)selectedShape).setY3(y3);
                controller.getCurrentDrawing().modifyShape(selectedShape);
                redrawCanvas();
            } catch (NumberFormatException ignored) {}
        }
    }

    private void setupControls() {
        strokeWidthSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            strokeWidthLabel.setText(String.format("%.1f", newVal.doubleValue()));
        });
    }

    private Shape createPreviewShape(double x1, double y1, double x2, double y2) {
        ShapeFactory.ShapeType type = controller.getSelectedShapeType();
        Color color = controller.getSelectedColor();
        double strokeWidth = controller.getSelectedStrokeWidth();
        boolean filled = controller.isFillMode();
        switch (type) {
            case RECTANGLE:
                return new com.drawing.model.shapes.Rectangle(
                    Math.min(x1, x2), Math.min(y1, y2),
                    Math.abs(x2 - x1), Math.abs(y2 - y1),
                    color, strokeWidth, filled);
            case CIRCLE: {
                double radius = Math.hypot(x2 - x1, y2 - y1);
                return new com.drawing.model.shapes.Circle(x1, y1, radius, color, strokeWidth, filled);
            }
            case LINE:
                return new com.drawing.model.shapes.Line(x1, y1, x2, y2, color, strokeWidth);
            case ELLIPSE:
                return new com.drawing.model.shapes.Ellipse(
                    x1, y1,
                    Math.abs(x2 - x1), Math.abs(y2 - y1),
                    color, strokeWidth, filled);
            case TRIANGLE:
                // Simple isosceles triangle for preview: base from x1,y1 to x2,y1, tip at midpoint above
                double baseX1 = x1, baseY1 = y2;
                double baseX2 = x2, baseY2 = y2;
                double tipX = (x1 + x2) / 2.0, tipY = y1;
                return new com.drawing.model.shapes.Triangle(
                    baseX1, baseY1, baseX2, baseY2, tipX, tipY, color, strokeWidth, filled);
            default:
                return null;
        }
    }

    private void drawPreviewShape() {
        if (previewShape != null) {
            previewShape.render(gc);
        }
    }

    // Draw resize handles for selected shape
    private void drawResizeHandles() {
        if (selectedShape == null) return;
        com.drawing.model.shapes.Shape.Bounds b = selectedShape.getBounds();
        gc.setFill(Color.BLUE);
        drawHandle(b.getMinX(), b.getMinY()); // Top-left
        drawHandle(b.getMaxX(), b.getMinY()); // Top-right
        drawHandle(b.getMinX(), b.getMaxY()); // Bottom-left
        drawHandle(b.getMaxX(), b.getMaxY()); // Bottom-right
    }
    private void drawHandle(double x, double y) {
        gc.fillRect(x - HANDLE_SIZE/2, y - HANDLE_SIZE/2, HANDLE_SIZE, HANDLE_SIZE);
    }
    private boolean isOnHandle(double mx, double my, double hx, double hy) {
        return Math.abs(mx - hx) <= HANDLE_SIZE/2 && Math.abs(my - hy) <= HANDLE_SIZE/2;
    }
    private Handle getHandleAt(double x, double y) {
        if (selectedShape == null) return Handle.NONE;
        com.drawing.model.shapes.Shape.Bounds b = selectedShape.getBounds();
        if (isOnHandle(x, y, b.getMinX(), b.getMinY())) return Handle.TOP_LEFT;
        if (isOnHandle(x, y, b.getMaxX(), b.getMinY())) return Handle.TOP_RIGHT;
        if (isOnHandle(x, y, b.getMinX(), b.getMaxY())) return Handle.BOTTOM_LEFT;
        if (isOnHandle(x, y, b.getMaxX(), b.getMaxY())) return Handle.BOTTOM_RIGHT;
        return Handle.NONE;
    }
    private void resizeSelectedShape(Handle handle, double x, double y) {
        if (selectedShape instanceof com.drawing.model.shapes.Rectangle) {
            com.drawing.model.shapes.Rectangle r = (com.drawing.model.shapes.Rectangle) selectedShape;
            double x0 = r.getX(), y0 = r.getY(), w = r.getWidth(), h = r.getHeight();
            double minX = x0, minY = y0, maxX = x0 + w, maxY = y0 + h;
            switch (handle) {
                case TOP_LEFT:
                    minX = x; minY = y; break;
                case TOP_RIGHT:
                    maxX = x; minY = y; break;
                case BOTTOM_LEFT:
                    minX = x; maxY = y; break;
                case BOTTOM_RIGHT:
                    maxX = x; maxY = y; break;
                case NONE:
                    // Do nothing
                    break;
            }
            r.setX(Math.min(minX, maxX));
            r.setY(Math.min(minY, maxY));
            r.setWidth(Math.abs(maxX - minX));
            r.setHeight(Math.abs(maxY - minY));
        } else if (selectedShape instanceof com.drawing.model.shapes.Circle) {
            com.drawing.model.shapes.Circle c = (com.drawing.model.shapes.Circle) selectedShape;
            double cx = c.getCenterX(), cy = c.getCenterY();
            double newRadius = Math.max(10, Math.hypot(x - cx, y - cy));
            c.setRadius(newRadius);
        }
        // Add similar logic for Ellipse, Line, Triangle if needed
    }

    private GraphNode findGraphNodeAt(double x, double y) {
        for (GraphNode node : graphNodes) {
            if (Math.hypot(node.x - x, node.y - y) < 15) return node;
        }
        return null;
    }

    // --- UI for shortest path ---
    @FXML private MenuItem shortestPathMenuItem;

    @FXML
    private void onShortestPathMenu() {
        if (toolMode != ToolMode.NODE) {
            statusLabel.setText("Passez en mode Noeud pour calculer le plus court chemin.");
            return;
        }
        int idA, idB;
        try {
            idA = Integer.parseInt(nodeAField.getText().trim());
            idB = Integer.parseInt(nodeBField.getText().trim());
        } catch (Exception e) {
            statusLabel.setText("Veuillez entrer deux identifiants de noeud valides.");
            return;
        }
        GraphNode nodeA = null, nodeB = null;
        for (GraphNode n : graphNodes) {
            if (n.id == idA) nodeA = n;
            if (n.id == idB) nodeB = n;
        }
        if (nodeA == null || nodeB == null) {
            statusLabel.setText("Noeud(s) non trouvé(s) pour les identifiants donnés.");
            return;
        }
        selectedNodeA = nodeA;
        selectedNodeB = nodeB;
        computeAndHighlightShortestPath(nodeA, nodeB);
        double totalDist = 0.0;
        for (GraphEdge e : shortestPath) totalDist += e.weight;
        if (!shortestPath.isEmpty()) {
            statusLabel.setText(String.format("Plus court chemin affiché en rouge. Distance: %.2f", totalDist));
        } else {
            statusLabel.setText("Aucun chemin trouvé entre les deux noeuds sélectionnés.");
        }
        redrawCanvas();
    }
    
    @FXML
    private void onSaveAsImage() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Enregistrer le dessin comme image");
            fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PNG Image", "*.png"),
                new FileChooser.ExtensionFilter("JPEG Image", "*.jpg", "*.jpeg")
            );
            File file = fileChooser.showSaveDialog(drawingCanvas.getScene().getWindow());
            if (file != null) {
                javafx.scene.image.WritableImage writableImage = new javafx.scene.image.WritableImage((int)drawingCanvas.getWidth(), (int)drawingCanvas.getHeight());
                drawingCanvas.snapshot(null, writableImage);
                ImageIO.write(SwingFXUtils.fromFXImage(writableImage, null),
                    file.getName().toLowerCase().endsWith(".jpg") || file.getName().toLowerCase().endsWith(".jpeg") ? "jpg" : "png",
                    file);
                // Clear everything after saving
                controller.clearDrawing();
                graphNodes.clear();
                graphEdges.clear();
                shortestPath.clear();
                selectedNodeA = null;
                selectedNodeB = null;
                edgeStartNode = null;
                redrawCanvas();
                updateUI();
                statusLabel.setText("Image enregistrée et dessin réinitialisé.");
            }
        } catch (Exception e) {
            statusLabel.setText("Erreur lors de l'enregistrement de l'image: " + e.getMessage());
        }
    }
}
