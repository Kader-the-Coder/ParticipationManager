package main.java.app.dao;

import main.java.app.models.Student;
import java.sql.*;
import java.util.*;
import java.util.logging.*;

public class StudentDAO {

  private static final Logger LOGGER = Logger.getLogger(StudentDAO.class.getName());
  private static Connection conn;

  public static void setConnection(Connection connection) {
    conn = connection;
  }

  // ------------------ CREATE ------------------

  /**
   * Adds a new student with associated subjects.
   * Returns the generated student ID or -1 if failed.
   */
  public static int addStudent(Student student) {
    int studentId = -1;
    String sql = "INSERT INTO students (name, grade_id) VALUES (?, ?)";
    try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
      stmt.setString(1, student.getName());
      stmt.setInt(2, student.getGradeId());
      stmt.executeUpdate();

      try (ResultSet rs = stmt.getGeneratedKeys()) {
        if (rs.next()) studentId = rs.getInt(1);
      }

      // Insert subjects
      if (studentId != -1 && student.getSubjectIds() != null) {
        updateStudentSubjects(studentId, student.getSubjectIds());
      }

    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, "Error adding student", e);
    }
    return studentId;
  }

  // ------------------ READ ------------------

  /**
   * Retrieves all students with subjects.
   */
  public static List<Student> getAllStudents() {
    List<Student> students = new ArrayList<>();
    String sql = "SELECT * FROM students";
    try (PreparedStatement stmt = conn.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {
      while (rs.next()) {
        int studentId = rs.getInt("id");
        List<Integer> subjectIds = getSubjectsForStudent(studentId);
        students.add(new Student(
          studentId,
          rs.getString("name"),
          rs.getInt("grade_id"),
          subjectIds
        ));
      }
    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, "Error fetching students", e);
    }
    return students;
  }

  /**
   * Gets a student by ID.
   */
  public static Student getStudentById(int studentId) {
    String sql = "SELECT * FROM students WHERE id = ?";
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setInt(1, studentId);
      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
          List<Integer> subjectIds = getSubjectsForStudent(studentId);
          return new Student(
            studentId,
            rs.getString("name"),
            rs.getInt("grade_id"),
            subjectIds
          );
        }
      }
    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, "Error fetching student by ID", e);
    }
    return null;
  }

  // ------------------ UPDATE ------------------

  /**
   * Updates a student and all associated subjects.
   */
  public static void updateStudent(Student student) {
    String sql = "UPDATE students SET name = ?, grade_id = ? WHERE id = ?";
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, student.getName());
      stmt.setInt(2, student.getGradeId());
      stmt.setInt(3, student.getId());
      stmt.executeUpdate();

      // Update subjects
      updateStudentSubjects(student.getId(), student.getSubjectIds());

    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, "Error updating student", e);
    }
  }

  // ------------------ DELETE ------------------

  /**
   * Deletes a student and all subject links.
   */
  public static void deleteStudent(int studentId) {
    try {
      // Delete subject links first
      deleteAllSubjectsForStudent(studentId);

      // Delete student
      String sql = "DELETE FROM students WHERE id = ?";
      try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setInt(1, studentId);
        stmt.executeUpdate();
      }

    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, "Error deleting student", e);
    }
  }

  // ------------------ SUBJECT MANAGEMENT ------------------

  /**
   * Adds a single subject to a student (without overwriting existing subjects).
   */
  public static void addSubjectToStudent(int studentId, int subjectId) {
    String sql = "INSERT OR IGNORE INTO student_subject (student_id, subject_id) VALUES (?, ?)";
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setInt(1, studentId);
      stmt.setInt(2, subjectId);
      stmt.executeUpdate();
    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, "Error adding subject to student", e);
    }
  }

  /**
   * Removes a single subject from a student.
   */
  public static void removeSubjectFromStudent(int studentId, int subjectId) {
    String sql = "DELETE FROM student_subject WHERE student_id = ? AND subject_id = ?";
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setInt(1, studentId);
      stmt.setInt(2, subjectId);
      stmt.executeUpdate();
    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, "Error removing subject from student", e);
    }
  }

  /**
   * Deletes all subject links for a student.
   */
  private static void deleteAllSubjectsForStudent(int studentId) throws SQLException {
    String sql = "DELETE FROM student_subject WHERE student_id = ?";
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setInt(1, studentId);
      stmt.executeUpdate();
    }
  }

  /**
   * Updates subject links for a student (overwrites all existing subjects).
   */
  private static void updateStudentSubjects(int studentId, List<Integer> subjectIds) throws SQLException {
    deleteAllSubjectsForStudent(studentId);
    if (subjectIds == null || subjectIds.isEmpty()) return;

    String sql = "INSERT INTO student_subject (student_id, subject_id) VALUES (?, ?)";
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      for (Integer subId : subjectIds) {
        stmt.setInt(1, studentId);
        stmt.setInt(2, subId);
        stmt.addBatch();
      }
      stmt.executeBatch();
    }
  }

  /**
   * Retrieves all subject IDs for a student.
   */
  private static List<Integer> getSubjectsForStudent(int studentId) {
    List<Integer> subjectIds = new ArrayList<>();
    String sql = "SELECT subject_id FROM student_subject WHERE student_id = ?";
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setInt(1, studentId);
      try (ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
          subjectIds.add(rs.getInt("subject_id"));
        }
      }
    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, "Error fetching subjects for student", e);
    }
    return subjectIds;
  }
}
