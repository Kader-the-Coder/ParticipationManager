package main.java.app.dao;

import main.java.app.models.DailyScore;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import java.util.logging.*;

public class DailyScoresDAO {

  private static final Logger LOGGER = Logger.getLogger(DailyScoresDAO.class.getName());

  public static void insertOrUpdate(DailyScore score) {
    String sql = """
      INSERT INTO daily_scores (student_id, day_id, participation, camera, on_time, behaviour, attendance, daily_total, notes, reflections)
      VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
      ON CONFLICT(student_id, day_id) DO UPDATE SET
        participation = excluded.participation,
        camera = excluded.camera,
        on_time = excluded.on_time,
        behaviour = excluded.behaviour,
        attendance = excluded.attendance,
        daily_total = excluded.daily_total,
        notes = excluded.notes,
        reflections = excluded.reflections
    """;

    Connection conn = DB.getConnection();

    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setInt(1, score.getStudentId());
      stmt.setInt(2, score.getDayId());
      stmt.setInt(3, score.getParticipation());
      stmt.setInt(4, score.getCamera());
      stmt.setInt(5, score.getOnTime());
      stmt.setInt(6, score.getBehaviour());
      stmt.setInt(7, score.getAttendance());
      stmt.setInt(8, score.getDailyTotal());
      stmt.setString(9, score.getNotes());
      stmt.setString(10, score.getReflections());
      stmt.executeUpdate();
    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, "Error inserting or updating daily score", e);
    }
  }

  public static DailyScore getScore(int studentId, int dayId) {
    String sql = "SELECT * FROM daily_scores WHERE student_id = ? AND day_id = ?";
    Connection conn = DB.getConnection();

    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setInt(1, studentId);
      stmt.setInt(2, dayId);
      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
          return new DailyScore(
            rs.getInt("id"),
            rs.getInt("student_id"),
            rs.getInt("day_id"),
            rs.getInt("participation"),
            rs.getInt("camera"),
            rs.getInt("on_time"),
            rs.getInt("behaviour"),
            rs.getInt("attendance"),
            rs.getInt("daily_total"),
            rs.getString("notes"),
            rs.getString("reflections")
          );
        }
      }
    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, "Error fetching daily score", e);
    }

    return null;
  }

  public static List<DailyScore> getScoresForDay(int dayId) {
    List<DailyScore> scores = new ArrayList<>();
    String sql = "SELECT * FROM daily_scores WHERE day_id = ?";
    Connection conn = DB.getConnection();

    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setInt(1, dayId);
      try (ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
          scores.add(new DailyScore(
            rs.getInt("id"),
            rs.getInt("student_id"),
            rs.getInt("day_id"),
            rs.getInt("participation"),
            rs.getInt("camera"),
            rs.getInt("on_time"),
            rs.getInt("behaviour"),
            rs.getInt("attendance"),
            rs.getInt("daily_total"),
            rs.getString("notes"),
            rs.getString("reflections")
          ));
        }
      }
    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, "Error fetching scores for day", e);
    }

    return scores;
  }

  public static void deleteScore(int studentId, int dayId) {
    String sql = "DELETE FROM daily_scores WHERE student_id = ? AND day_id = ?";
    Connection conn = DB.getConnection();

    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setInt(1, studentId);
      stmt.setInt(2, dayId);
      stmt.executeUpdate();
    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, "Error deleting daily score", e);
    }
  }

  public static void deleteScoresForDay(int dayId) {
    String sql = "DELETE FROM daily_scores WHERE day_id = ?";
    Connection conn = DB.getConnection();

    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setInt(1, dayId);
      stmt.executeUpdate();
    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, "Error deleting all scores for day", e);
    }
  }

  public static Integer getLastDayIdForQuarter(int quarterId) {
    String sql = """
        SELECT MAX(ds.day_id) AS last_day_id
        FROM daily_scores ds
        JOIN days d ON ds.day_id = d.id
        WHERE d.quarter_id = ?
    """;

    Connection conn = DB.getConnection();
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setInt(1, quarterId);
      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
          int lastDayId = rs.getInt("last_day_id");
          if (!rs.wasNull()) {
            return lastDayId;
          }
        }
      }
    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, "Error fetching last day id for quarter " + quarterId, e);
    }

    // No daily scores found for this quarter
    return null;
  }

  public static Double getAverageScoreForWeek(LocalDate quarterStart, int weekNumber) {
    // Compute start and end dates for the week
    LocalDate weekStart = quarterStart.plusWeeks(weekNumber - 1);
    LocalDate weekEnd = weekStart.plusDays(6);

    String sql = """
        SELECT AVG(daily_total) AS avg_score
        FROM daily_scores ds
        JOIN days d ON ds.day_id = d.id
        WHERE d.date BETWEEN ? AND ?
    """;

    Connection conn = DB.getConnection();
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, weekStart.toString());
      stmt.setString(2, weekEnd.toString());

      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
          double avg = rs.getDouble("avg_score");
          if (!rs.wasNull()) return avg;
        }
      }
    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, "Error calculating average score for week " + weekNumber
        + " starting " + weekStart, e);
    }

    return null; // no scores recorded for this week
  }


}
