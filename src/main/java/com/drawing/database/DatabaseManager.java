package com.drawing.database;

import com.drawing.model.drawing.Drawing;
import com.drawing.model.shapes.Shape;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Gestionnaire de base de données pour sauvegarder et charger les dessins
 */
public class DatabaseManager {
    
    private static final String DB_URL = "jdbc:h2:./drawings;AUTO_SERVER=TRUE";
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "";
    
    private Connection connection;

    public DatabaseManager() {
        // this.objectMapper = new ObjectMapper();
        initializeDatabase();
    }
    
    private void initializeDatabase() {
        try {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            createTablesIfNotExist();
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'initialisation de la base de données: " + e.getMessage());
        }
    }
    
    private void createTablesIfNotExist() throws SQLException {
        String createDrawingsTable = """
            CREATE TABLE IF NOT EXISTS drawings (
                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                name VARCHAR(255) NOT NULL UNIQUE,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """;
        
        String createShapesTable = """
            CREATE TABLE IF NOT EXISTS shapes (
                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                drawing_id BIGINT NOT NULL,
                shape_type VARCHAR(50) NOT NULL,
                shape_data TEXT NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (drawing_id) REFERENCES drawings(id) ON DELETE CASCADE
            )
        """;
        
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createDrawingsTable);
            stmt.execute(createShapesTable);
        }
    }
    
    /**
     * Sauvegarde un dessin dans la base de données
     */
    public boolean saveDrawing(Drawing drawing) {
        try {
            connection.setAutoCommit(false);
            
            // Supprimer le dessin existant s'il existe
            deleteDrawing(drawing.getName());
            
            // Insérer le nouveau dessin
            String insertDrawingSQL = "INSERT INTO drawings (name) VALUES (?)";
            long drawingId;
            
            try (PreparedStatement stmt = connection.prepareStatement(insertDrawingSQL, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, drawing.getName());
                stmt.executeUpdate();
                
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        drawingId = rs.getLong(1);
                    } else {
                        throw new SQLException("Échec de la création du dessin");
                    }
                }
            }
            
            // Insérer les formes
            String insertShapeSQL = "INSERT INTO shapes (drawing_id, shape_type, shape_data) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(insertShapeSQL)) {
                for (Shape shape : drawing.getShapes()) {
                    stmt.setLong(1, drawingId);
                    stmt.setString(2, shape.getClass().getSimpleName());
                    stmt.setString(3, shape.toString()); // Simplified serialization
                    stmt.addBatch();
                }
                stmt.executeBatch();
            }
            
            connection.commit();
            drawing.markAsSaved(drawing.getName());
            return true;
            
        } catch (Exception e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                System.err.println("Erreur lors du rollback: " + rollbackEx.getMessage());
            }
            System.err.println("Erreur lors de la sauvegarde: " + e.getMessage());
            return false;
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                System.err.println("Erreur lors de la restauration de l'autocommit: " + e.getMessage());
            }
        }
    }
    
    /**
     * Charge un dessin depuis la base de données
     */
    public Drawing loadDrawing(String name) {
        try {
            String selectDrawingSQL = "SELECT id FROM drawings WHERE name = ?";
            long drawingId;
            
            try (PreparedStatement stmt = connection.prepareStatement(selectDrawingSQL)) {
                stmt.setString(1, name);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        drawingId = rs.getLong("id");
                    } else {
                        return null; // Dessin non trouvé
                    }
                }
            }
            
            Drawing drawing = new Drawing(name);
            
            // Charger les formes
            String selectShapesSQL = "SELECT shape_type, shape_data FROM shapes WHERE drawing_id = ? ORDER BY id";
            try (PreparedStatement stmt = connection.prepareStatement(selectShapesSQL)) {
                stmt.setLong(1, drawingId);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        String shapeType = rs.getString("shape_type");
                        String shapeData = rs.getString("shape_data");
                        
                        // Désérialiser la forme selon son type
                        Shape shape = deserializeShape(shapeType, shapeData);
                        if (shape != null) {
                            drawing.addShape(shape);
                        }
                    }
                }
            }
            
            drawing.markAsLoaded(name);
            return drawing;
            
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement: " + e.getMessage());
            return null;
        }
    }
    
    private Shape deserializeShape(String shapeType, String shapeData) {
        // Simplified deserialization - would need proper implementation with Jackson
        System.out.println("Deserializing: " + shapeType + " - " + shapeData);
        return null; // Placeholder
    }
    
    /**
     * Supprime un dessin de la base de données
     */
    public boolean deleteDrawing(String name) {
        try {
            String deleteSQL = "DELETE FROM drawings WHERE name = ?";
            try (PreparedStatement stmt = connection.prepareStatement(deleteSQL)) {
                stmt.setString(1, name);
                int rowsAffected = stmt.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Obtient la liste de tous les dessins sauvegardés
     */
    public List<String> getDrawingNames() {
        List<String> names = new ArrayList<>();
        try {
            String selectSQL = "SELECT name FROM drawings ORDER BY updated_at DESC";
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(selectSQL)) {
                while (rs.next()) {
                    names.add(rs.getString("name"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des noms: " + e.getMessage());
        }
        return names;
    }
    
    /**
     * Ferme la connexion à la base de données
     */
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la fermeture de la connexion: " + e.getMessage());
        }
    }
}
