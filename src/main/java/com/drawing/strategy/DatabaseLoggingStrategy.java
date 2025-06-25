package com.drawing.strategy;

import java.sql.*;
import java.time.LocalDateTime;

/**
 * Stratégie de journalisation dans une base de données
 */
public class DatabaseLoggingStrategy implements LoggingStrategy {
    
    private Connection connection;
    private PreparedStatement insertStatement;
    
    public DatabaseLoggingStrategy(String databaseUrl) {
        try {
            // Connexion à la base de données H2
            connection = DriverManager.getConnection(databaseUrl);
            
            // Créer la table de logs si elle n'existe pas
            createLogTableIfNotExists();
            
            // Préparer la requête d'insertion
            String insertSQL = "INSERT INTO logs (timestamp, level, message) VALUES (?, ?, ?)";
            insertStatement = connection.prepareStatement(insertSQL);
            
        } catch (SQLException e) {
            System.err.println("Erreur lors de la connexion à la base de données: " + e.getMessage());
        }
    }
    
    private void createLogTableIfNotExists() throws SQLException {
        String createTableSQL = """
            CREATE TABLE IF NOT EXISTS logs (
                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                timestamp TIMESTAMP NOT NULL,
                level VARCHAR(10) NOT NULL,
                message TEXT NOT NULL
            )
        """;
        
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createTableSQL);
        }
    }
    
    @Override
    public void log(LogLevel level, String message) {
        if (insertStatement != null) {
            try {
                insertStatement.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
                insertStatement.setString(2, level.toString());
                insertStatement.setString(3, message);
                insertStatement.executeUpdate();
            } catch (SQLException e) {
                System.err.println("Erreur lors de l'insertion du log: " + e.getMessage());
            }
        }
    }
    
    @Override
    public void close() {
        try {
            if (insertStatement != null) {
                insertStatement.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la fermeture de la connexion: " + e.getMessage());
        }
    }
}
