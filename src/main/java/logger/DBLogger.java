package logger;

import util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class DBLogger implements Logger {
    @Override
    public void log(String message) {
        try (Connection conn = DBUtil.getConnection()) {
            String sql = "INSERT INTO logs(message) VALUES(?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, message);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void clearLogs() {
        String sql = "DELETE FROM logs";
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement()) {
            int rowsDeleted = stmt.executeUpdate(sql);
            System.out.println("Tous les logs ont été supprimés (" + rowsDeleted + " lignes).");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}

