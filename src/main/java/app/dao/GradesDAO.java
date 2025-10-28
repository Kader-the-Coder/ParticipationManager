package main.java.app.dao;

import java.sql.*;
import java.util.*;
import java.util.logging.*;

/**
 * Data Access Object for managing grades.
 */
public class GradesDAO {

  private static final Logger LOGGER = Logger.getLogger(GradesDAO.class.getName());

  /**
   * Retrieves all grade names from the database.
   * @return A list of grade names.
   */
  public static List<String> getAllGradeNames() {
    List<String> names = new ArrayList<>();
    String sql = "SELECT name FROM grades";

    Connection conn = DB.getConnection();
    try (Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {
      while (rs.next()) {
        names.add(rs.getString("name"));
      }
      // Sort numerically
      names.sort(Comparator.comparingInt(Integer::parseInt));
    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, "Failed to get all grade names", e);
    }


    return names;
  }

  /**
   * Returns the ID of a grade given its name.
   * @param name Grade name
   * @return Grade ID or -1 if not found
   */
  public static int getGradeIdByName(String name) {
    String sql = "SELECT id FROM grades WHERE name = ?";
    Connection conn = DB.getConnection();
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, name);
      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) return rs.getInt("id");
      }
    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, "Failed to get grade ID for name: " + name, e);
    }
    return -1;
  }

  /**
   * Returns the name of a grade given its ID.
   * @param id Grade ID
   * @return Grade name or null if not found
   */
  public static String getNameById(int id) {
    String sql = "SELECT name FROM grades WHERE id = ?";
    Connection conn = DB.getConnection();
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setInt(1, id);
      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) return rs.getString("name");
      }
    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, "Failed to get grade name for ID: " + id, e);
    }
    return null;
  }

  /**
   * Inserts a new grade if it doesn't exist, returns the grade ID.
   * @param gradeName Name of the grade
   * @return Grade ID
   */
  public static int getOrCreateGradeId(String gradeName) {
    int id = getGradeIdByName(gradeName);
    if (id != -1) return id;

    String sql = "INSERT INTO grades (name) VALUES (?)";
    Connection conn = DB.getConnection();
    try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
      stmt.setString(1, gradeName);
      stmt.executeUpdate();
      try (ResultSet rs = stmt.getGeneratedKeys()) {
        if (rs.next()) return rs.getInt(1);
      }
    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, "Error inserting new grade: " + gradeName, e);
    }

    return -1;
  }
}
