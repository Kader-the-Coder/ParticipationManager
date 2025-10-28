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
    LocalDate startDate = getStartDateById(quarterId);
    LocalDate nextQuarterStart = getNextQuarterStartDate(quarterId);

    if (startDate == null) {
      LOGGER.log(Level.WARNING, "Start date not found for quarter ID: " + quarterId);
      return null;
    }

    LocalDate endDate = (nextQuarterStart != null) ? nextQuarterStart : LocalDate.of(9999, 12, 31);

    String sql = """
        SELECT MAX(ds.day_id) AS last_day_id
        FROM daily_scores ds
        JOIN days d ON ds.day_id = d.id
        WHERE d.date >= ? AND d.date < ?
    """;

    Connection conn = DB.getConnection();
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, startDate.toString());
      stmt.setString(2, endDate.toString());
      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
          int lastDayId = rs.getInt("last_day_id");
          if (!rs.wasNull()) return lastDayId;
        }
      }
    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, "Error fetching last day id for quarter " + quarterId, e);
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Unexpected error fetching last day id for quarter " + quarterId, e);
    }

    return null;
  }

  public static LocalDate getStartDateById(int id) {
    String sql = "SELECT start_date FROM quarters WHERE id = ?";
    Connection conn = DB.getConnection();
    try {
      PreparedStatement stmt = conn.prepareStatement(sql);
      stmt.setInt(1, id);
      ResultSet rs = stmt.executeQuery();
      if (rs.next()) {
        long timestamp = rs.getLong("start_date");
        return LocalDate.ofEpochDay(timestamp / (24 * 60 * 60 * 1000));
      }
    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, "Error fetching start date for quarter ID: " + id, e);
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Unexpected error while getting start date for quarter ID: " + id, e);
    }
    return null;
  }

  public static LocalDate getNextQuarterStartDate(int id) {
    String sql = """
        SELECT q2.start_date
        FROM quarters q1
        JOIN quarters q2 ON q2.start_date > q1.start_date
        WHERE q1.id = ?
        ORDER BY q2.start_date ASC
        LIMIT 1
    """;
    Connection conn = DB.getConnection();
    try {
      PreparedStatement stmt = conn.prepareStatement(sql);
      stmt.setInt(1, id);
      ResultSet rs = stmt.executeQuery();
      if (rs.next()) {
        long timestamp = rs.getLong("start_date");
        return LocalDate.ofEpochDay(timestamp / (24 * 60 * 60 * 1000));
      }
    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, "Error fetching next quarter start date for ID: " + id, e);
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Unexpected error while getting next quarter start date for ID: " + id, e);
    }
    return null;
  }

  public static Double getAverageScoreForWeek(LocalDate quarterStart, int weekNumber) {
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

    return null;
  }

  public static Double getWeeklyScoreForStudent(int studentId, int quarterId, int weekNumber) {
    LocalDate quarterStart = getStartDateById(quarterId);
    if (quarterStart == null) return null;

    LocalDate weekStart = quarterStart.plusWeeks(weekNumber - 1);
    LocalDate weekEnd = weekStart.plusDays(6);

    // Limit to last recorded day of the quarter
    Integer lastDayId = getLastDayIdForQuarter(quarterId);
    if (lastDayId != null) {
      LocalDate lastDate = DaysDAO.getDateForDayId(lastDayId);
      if (lastDate != null && weekEnd.isAfter(lastDate)) {
        weekEnd = lastDate;
      }
    }

    // Fetch all daily totals for this student in this week
    List<Integer> totals = new ArrayList<>();
    String sql = """
        SELECT ds.daily_total
        FROM daily_scores ds
        JOIN days d ON ds.day_id = d.id
        WHERE ds.student_id = ? AND d.date >= ? AND d.date <= ?
    """;

    Connection conn = DB.getConnection();
    try {
      PreparedStatement stmt = conn.prepareStatement(sql);
      stmt.setInt(1, studentId);
      stmt.setString(2, weekStart.toString());
      stmt.setString(3, weekEnd.toString());

      ResultSet rs = stmt.executeQuery();
      while (rs.next()) {
        totals.add(rs.getInt("daily_total"));
      }
    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, "Error fetching weekly scores for student " + studentId, e);
      return null;
    }

    if (totals.isEmpty()) return null;

    // Compute average manually
    double sum = 0;
    for (int t : totals) sum += t;
    return sum / totals.size();
  }



}
