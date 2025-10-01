package main.java.app.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SettingsDAO {
  private static final Logger LOGGER = Logger.getLogger(SettingsDAO.class.getName());

  /**
   * Save or update a setting
   */
  public static void saveSetting(String key, String value) {
    String sql = "INSERT INTO settings(key, value) VALUES(?, ?) " +
      "ON CONFLICT(key) DO UPDATE SET value=excluded.value";
    Connection conn = DB.getConnection();
    try (PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, key);
      ps.setString(2, value);
      ps.executeUpdate();
    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, "Error saving setting: " + key, e);
    }
  }

  /**
   * Load a setting value by key
   */
  public static String loadSetting(String key, String defaultValue) {
    String sql = "SELECT value FROM settings WHERE key = ?";
    Connection conn = DB.getConnection();
    try (PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

      ps.setString(1, key);
      try (ResultSet result = ps.executeQuery()) {
        if (result.next()) {
          return result.getString("value");
        }
      }

    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, "Error loading setting: " + key, e);
    }
    return defaultValue;
  }
}
