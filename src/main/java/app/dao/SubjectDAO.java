package main.java.app.dao;

import java.sql.*;
import java.util.*;
import java.util.logging.*;

/**
 * Data Access Object for managing subjects.
 */
public class SubjectDAO {

  private static final Logger LOGGER = Logger.getLogger(SubjectDAO.class.getName());
  private static Connection conn;

  public static void setConnection(Connection connection) {
    conn = connection;
  }

  /**
   * Retrieves all subjects from the database.
   * @return A list of subject names.
   */
  public static List<String> getAllSubjects() {
    List<String> subjects = new ArrayList<>();
    String sql = "SELECT name FROM subjects ORDER BY name ASC";
    try (PreparedStatement stmt = conn.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {
      while (rs.next()) {
        subjects.add(rs.getString("name"));
      }
    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, "Error fetching subjects", e);
    }
    return subjects;
  }

  /**
   * Gets the ID of a subject by name.
   * @param subjectName The subject name.
   * @return The subject ID or -1 if not found.
   */
  public static int getSubjectId(String subjectName) {
    String sql = "SELECT id FROM subjects WHERE name = ?";
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, subjectName);
      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) return rs.getInt("id");
      }
    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, "Error fetching subject ID", e);
    }
    return -1;
  }

  /**
   * Inserts a new subject or returns the ID if it already exists.
   * @param subjectName The subject name.
   * @return The subject ID.
   */
  public static int getOrCreateSubjectId(String subjectName) {
    int id = getSubjectId(subjectName);
    if (id != -1) return id;

    String sql = "INSERT INTO subjects (name) VALUES (?)";
    try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
      stmt.setString(1, subjectName);
      stmt.executeUpdate();
      try (ResultSet rs = stmt.getGeneratedKeys()) {
        if (rs.next()) return rs.getInt(1);
      }
    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, "Error inserting new subject", e);
    }
    return -1;
  }
}
