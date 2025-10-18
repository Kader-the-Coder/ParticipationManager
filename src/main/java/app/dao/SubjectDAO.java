package main.java.app.dao;

import java.sql.*;
import java.util.*;
import java.util.logging.*;

/**
 * Data Access Object for managing subjects.
 */
public class SubjectDAO {

  private static final Logger LOGGER = Logger.getLogger(SubjectDAO.class.getName());

  /**
   * Retrieves all subjects from the database.
   * @return A list of subject names.
   */
  public static List<String> getAllSubjects() {
    List<String> subjects = new ArrayList<>();
    String sql = "SELECT name FROM subjects ORDER BY name ASC";
    Connection conn = DB.getConnection();

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
    Connection conn = DB.getConnection();

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
    Connection conn = DB.getConnection();

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

  /**
   * Returns a list of subject names for the given IDs.
   */
  public static List<String> getNamesForIds(List<Integer> subjectIds) {
    List<String> names = new ArrayList<>();
    if (subjectIds == null || subjectIds.isEmpty()) return names;

    String placeholders = String.join(",", Collections.nCopies(subjectIds.size(), "?"));
    String sql = "SELECT name FROM subjects WHERE id IN (" + placeholders + ")";
    Connection conn = DB.getConnection();

    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      for (int i = 0; i < subjectIds.size(); i++) {
        stmt.setInt(i + 1, subjectIds.get(i));
      }
      try (ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
          names.add(rs.getString("name"));
        }
      }
    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, "Failed to fetch subject names for IDs", e);
    }

    return names;
  }

  /**
   * Returns a list of subject IDs corresponding to the given list of subject names.
   */
  public static List<Integer> getIdsForNames(List<String> names) {
    List<Integer> ids = new ArrayList<>();
    if (names == null || names.isEmpty()) return ids;

    String sql = "SELECT id FROM subjects WHERE name = ?";
    Connection conn = DB.getConnection();

    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      for (String name : names) {
        stmt.setString(1, name);
        try (ResultSet rs = stmt.executeQuery()) {
          if (rs.next()) {
            ids.add(rs.getInt("id"));
          }
        }
      }
    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, "Failed to get IDs for subject names", e);
    }
    return ids;
  }

  /**
   * Returns a list of all subject names in the database.
   */
  public static List<String> getAllNames() {
    List<String> names = new ArrayList<>();
    String sql = "SELECT name FROM subjects ORDER BY name";
    Connection conn = DB.getConnection();

    try (PreparedStatement stmt = conn.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {
      while (rs.next()) {
        names.add(rs.getString("name"));
      }
    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, "Error fetching all subject names", e);
    }
    return names;
  }

  /**
   * Returns the ID of a subject by its name.
   * Returns -1 if not found.
   */
  public static int getIdByName(String name) {
    String sql = "SELECT id FROM subjects WHERE name = ?";
    Connection conn = DB.getConnection();

    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, name);
      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) return rs.getInt("id");
      }
    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, "Error fetching subject ID for name: " + name, e);
    }
    return -1;
  }

  public static List<String> getAllSubjectNames() {
    List<String> names = new ArrayList<>();
    String sql = "SELECT name FROM subjects ORDER BY name ASC";

    Connection conn = DB.getConnection();
    try (Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {
      while (rs.next()) names.add(rs.getString("name"));
    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, "Failed to get all subject names", e);
    }
    return names;
  }

  public static int getSubjectIdByName(String name) {
    String sql = "SELECT id FROM subjects WHERE name = ?";
    Connection conn = DB.getConnection();
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, name);
      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) return rs.getInt("id");
      }
    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, "Failed to get subject ID for name: " + name, e);
    }
    return -1;
  }

}
