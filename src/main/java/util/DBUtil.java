package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {

    private static final String URL = "jdbc:mysql://localhost:3306/drawingapp"; // nom de ta BDD
    private static final String USER = "root";  // remplace si besoin
    private static final String PASSWORD = "root";  // ton mot de passe MySQL
    // Bloc static : exécuté une seule fois lors du chargement de la classe DBUtil equivalent constructeur peivee
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // Charger le driver MySQL
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        // Initialisation éventuelle des tables (si besoin)
        try (Connection conn = getConnection()) {
            conn.createStatement().execute("CREATE TABLE IF NOT EXISTS logs(" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "message TEXT)");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // Méthode statique pour obtenir une connexion à la base de données
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}