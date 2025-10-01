package main.java.app.dao;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DB {
  private static final Logger LOGGER = Logger.getLogger(DB.class.getName());
  private static final String DB_FOLDER = "data";
  private static final String DB_FILE = "database.db";
  private static final String DB_URL = "jdbc:sqlite:" + DB_FOLDER + File.separator + DB_FILE;

  private static Connection connection;

  /**
   * Initialize the database connection and create tables if needed.
   */
  public static void init() {
    try {
      // ensure data folder exists
      File folder = new File(DB_FOLDER);
      if (!folder.exists()) {
        if (folder.mkdirs()) {
          LOGGER.info("Created database folder: " + folder.getAbsolutePath());
        }
      }

      if (connection == null || connection.isClosed()) {
        connection = DriverManager.getConnection(DB_URL);
        enableForeignKeys();
        createTables();
        LOGGER.info("Database initialized at " + DB_URL);
      }
    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, "Error initializing database", e);
    }
  }

  /**
   * Provides the open connection for use in other modules (DAOs, etc.).
   */
  public static Connection getConnection() {
    try {
      if (connection == null || connection.isClosed()) {
        connection = DriverManager.getConnection(DB_URL);
        enableForeignKeys();
        LOGGER.info("Database connection reopened.");
      }
    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, "Error getting database connection", e);
    }
    return connection;
  }

  /**
   * Close the connection when the application shuts down.
   */
  public static void close() {
    try {
      if (connection != null && !connection.isClosed()) {
        connection.close();
        LOGGER.info("Database connection closed.");
      }
    } catch (SQLException e) {
      LOGGER.log(Level.WARNING, "Error closing database connection", e);
    }
  }

  // === Helper methods ===

  private static void enableForeignKeys() {
    try (Statement stmt = connection.createStatement()) {
      stmt.execute("PRAGMA foreign_keys = ON");
    } catch (SQLException e) {
      LOGGER.log(Level.WARNING, "Failed to enable foreign keys", e);
    }
  }

  private static void createTables() {
    try (Statement stmt = connection.createStatement()) {
      // students
      stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS students (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL
                )
            """);

      // quarters
      stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS quarters (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    start_date DATE NOT NULL
                )
            """);

      // weeks
      stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS weeks (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    quarter_id INTEGER,
                    week_number INTEGER,
                    start_date DATE,
                    FOREIGN KEY (quarter_id) REFERENCES quarters(id)
                )
            """);

      // days
      stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS days (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    week_id INTEGER,
                    date DATE,
                    UNIQUE(week_id, date),
                    FOREIGN KEY (week_id) REFERENCES weeks(id)
                )
            """);

      // daily_scores
      stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS daily_scores (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    student_id INTEGER,
                    day_id INTEGER,
                    participation INTEGER,
                    camera INTEGER,
                    on_time INTEGER,
                    behaviour INTEGER,
                    attendance INTEGER,
                    daily_total INTEGER,
                    notes TEXT,
                    reflections TEXT,
                    FOREIGN KEY (student_id) REFERENCES students(id),
                    FOREIGN KEY (day_id) REFERENCES days(id)
                )
            """);

      // criteria_weights
      stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS criteria_weights (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    participation INTEGER,
                    camera INTEGER,
                    on_time INTEGER,
                    behaviour INTEGER,
                    attendance INTEGER,
                    effective_date DATE
                )
            """);

      // settings
      stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS settings (
                    key TEXT PRIMARY KEY,
                    value TEXT
                )
            """);

    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, "Error creating tables", e);
    }
  }
}
