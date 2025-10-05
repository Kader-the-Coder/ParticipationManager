package main.java.app.dao;

import java.sql.*;
import java.util.*;
import java.util.logging.*;

/**
 * Data Access Object for managing grades.
 */
public class GradeDAO {

  private static final Logger LOGGER = Logger.getLogger(GradeDAO.class.getName());
  private static Connection conn;

  public static void setConnection(Connection connection) {
    conn = connection;
  }

  /**
   * Retrieves all grades from the database.
   * @return A list of grade names.
   */
  public static List<String> getAllGrades() {
    List<String> grades = new ArrayList<>();
    String sql = "SELECT name FROM grades ORDER BY name ASC";
    try (PreparedStatement stmt = conn.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {
      while (rs.next()) {
        grades.add(rs.getString("name"));
      }
    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, "Error fetching grades", e);
    }
    return grades;
  }

  /**
   * Gets the ID of a grade by name.
   * @param gradeName The grade name.
   * @return The grade ID or -1 if not found.
   */
  public static int getGradeId(String gradeName) {
    String sql = "SELECT id FROM grades WHERE name = ?";
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, gradeName);
      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) return rs.getInt("id");
      }
    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, "Error fetching grade ID", e);
    }
    return -1;
  }

  /**
   * Inserts a new grade or returns the ID if it already exists.
   * @param gradeName The grade name.
   * @return The grade ID.
   */
  public static int getOrCreateGradeId(String gradeName) {
    int id = getGradeId(gradeName);
    if (id != -1) return id;

    String sql = "INSERT INTO grades (name) VALUES (?)";
    try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
      stmt.setString(1, gradeName);
      stmt.executeUpdate();
      try (ResultSet rs = stmt.getGeneratedKeys()) {
        if (rs.next()) return rs.getInt(1);
      }
    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, "Error inserting new grade", e);
    }
    return -1;
  }
}
