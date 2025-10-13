package main.java.app.dao;

import java.sql.*;
import java.time.LocalDate;
import java.util.logging.*;

public class DaysDAO {

  private static final Logger LOGGER = Logger.getLogger(DaysDAO.class.getName());

  public static int getDayIdByDate(LocalDate date) {
    String sql = "SELECT id FROM days WHERE date = ?";
    Connection conn = DB.getConnection();

    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, date.toString());
      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
          return rs.getInt("id");
        }
      }
    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, "Error fetching day ID for date: " + date, e);
    }

    return -1;
  }

  public static int createDay(LocalDate date) {
    String sql = "INSERT INTO days(date) VALUES(?)";
    Connection conn = DB.getConnection();

    try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
      stmt.setString(1, date.toString());
      stmt.executeUpdate();
      try (ResultSet rs = stmt.getGeneratedKeys()) {
        if (rs.next()) {
          return rs.getInt(1);
        }
      }
    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, "Error creating new day for date: " + date, e);
    }

    return -1;
  }

  public static int getOrCreateDay(LocalDate date) {
    int id = getDayIdByDate(date);
    if (id != -1) return id;
    return createDay(date);
  }
}
