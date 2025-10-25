package main.java.app.dao;

import main.java.app.models.Quarter;

import java.sql.*;
import java.time.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class QuartersDAO {

  private static final Logger LOGGER = Logger.getLogger(QuartersDAO.class.getName());

  public static List<Quarter> getAllQuarters() {
    List<Quarter> quarters = new ArrayList<>();
    String sql = "SELECT id, name, start_date FROM quarters ORDER BY start_date ASC";
    Connection conn = DB.getConnection();

    try (PreparedStatement stmt = conn.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {

      while (rs.next()) {
        int id = rs.getInt("id");
        String name = rs.getString("name");

        // Handle both epoch and ISO string formats safely
        LocalDate date;
        try {
          long millis = rs.getLong("start_date");
          date = Instant.ofEpochMilli(millis)
            .atZone(ZoneId.systemDefault())
            .toLocalDate();
        } catch (Exception ignored) {
          date = LocalDate.parse(rs.getString("start_date"));
        }

        quarters.add(new Quarter(id, name, date));
      }
    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, "Error fetching quarters", e);
    }

    return quarters;
  }

  public static void addQuarter(Quarter quarter) {
    String sql = "INSERT INTO quarters (name, start_date) VALUES (?, ?)";
    Connection conn = DB.getConnection();

    try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
      stmt.setString(1, quarter.getName());
      stmt.setLong(2, quarter.getStartDate()
        .atStartOfDay(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli());
      stmt.executeUpdate();

      try (ResultSet rs = stmt.getGeneratedKeys()) {
        if (rs.next()) quarter.setId(rs.getInt(1));
      }
    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, "Error adding quarter", e);
    }
  }

  public static void updateQuarter(Quarter quarter) {
    String sql = "UPDATE quarters SET name = ?, start_date = ? WHERE id = ?";
    Connection conn = DB.getConnection();

    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, quarter.getName());
      stmt.setLong(2, quarter.getStartDate()
        .atStartOfDay(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli());
      stmt.setInt(3, quarter.getId());
      stmt.executeUpdate();
    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, "Error updating quarter with id " + quarter.getId(), e);
    }
  }

  public static void deleteQuarter(int quarterId) {
    String sql = "DELETE FROM quarters WHERE id = ?";
    Connection conn = DB.getConnection();

    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setInt(1, quarterId);
      stmt.executeUpdate();
    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, "Error deleting quarter with id " + quarterId, e);
    }
  }

  public static LocalDate computeNextQuarterStartDate() {
    List<Quarter> quarters = getAllQuarters();
    LocalDate startDate;

    if (quarters.isEmpty()) {
      startDate = nextMonday(LocalDate.now());
    } else {
      Quarter last = quarters.getLast();
      startDate = nextMonday(last.getStartDate().plusWeeks(1));
    }

    return startDate;
  }

  private static LocalDate nextMonday(LocalDate fromDate) {
    int daysUntilMonday = DayOfWeek.MONDAY.getValue() - fromDate.getDayOfWeek().getValue();
    if (daysUntilMonday <= 0) daysUntilMonday += 7;
    return fromDate.plusDays(daysUntilMonday);
  }
}
