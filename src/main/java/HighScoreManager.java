/* 
 * Moussaif Fahd
 * Nasry Sami
 * Louaddi Zakaria  
 * AIT LAADIK Soukaina
 */

// Classe utilitaire pour gérer le meilleur score dans une base de données SQLite

import java.sql.*;

public class HighScoreManager {
    // Chemin de la base de données SQLite (fichier local)
    private static final String DB_URL = "jdbc:sqlite:tetris_highscores.db";

    // Constructeur : crée la table si elle n'existe pas
    public HighScoreManager() {
        createTableIfNotExists();
    }

    // Crée la table "highscores" si elle n'existe pas déjà
    private void createTableIfNotExists() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS highscores (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "score INTEGER NOT NULL)"
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Récupère le meilleur score enregistré dans la base
    public int getHighScore() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT MAX(score) AS max_score FROM highscores")) {
            if (rs.next()) {
                return rs.getInt("max_score");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Enregistre un nouveau score dans la base (utilisé si le joueur bat le record)
    public void saveScore(int score) {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement("INSERT INTO highscores(score) VALUES (?)")) {
            ps.setInt(1, score);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Réinitialise le meilleur score (supprime tous les scores enregistrés)
    public void resetHighScore() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM highscores");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
