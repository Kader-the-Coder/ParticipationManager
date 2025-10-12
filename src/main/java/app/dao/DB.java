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
      // Grades
      stmt.executeUpdate("""
            CREATE TABLE IF NOT EXISTS grades (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL UNIQUE
            );
        """);

      // Subjects
      stmt.executeUpdate("""
            CREATE TABLE IF NOT EXISTS subjects (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL UNIQUE
            );
        """);

      // --- Insert grades 1 to 12 ---
      stmt.executeUpdate("INSERT OR IGNORE INTO grades (name) VALUES ('1')");
      stmt.executeUpdate("INSERT OR IGNORE INTO grades (name) VALUES ('2')");
      stmt.executeUpdate("INSERT OR IGNORE INTO grades (name) VALUES ('3')");
      stmt.executeUpdate("INSERT OR IGNORE INTO grades (name) VALUES ('4')");
      stmt.executeUpdate("INSERT OR IGNORE INTO grades (name) VALUES ('5')");
      stmt.executeUpdate("INSERT OR IGNORE INTO grades (name) VALUES ('6')");
      stmt.executeUpdate("INSERT OR IGNORE INTO grades (name) VALUES ('7')");
      stmt.executeUpdate("INSERT OR IGNORE INTO grades (name) VALUES ('8')");
      stmt.executeUpdate("INSERT OR IGNORE INTO grades (name) VALUES ('9')");
      stmt.executeUpdate("INSERT OR IGNORE INTO grades (name) VALUES ('10')");
      stmt.executeUpdate("INSERT OR IGNORE INTO grades (name) VALUES ('11')");
      stmt.executeUpdate("INSERT OR IGNORE INTO grades (name) VALUES ('12')");

      // --- Insert subjects from timetable ---
      stmt.executeUpdate("INSERT OR IGNORE INTO subjects (name) VALUES ('Quran')");
      stmt.executeUpdate("INSERT OR IGNORE INTO subjects (name) VALUES ('Arabic Beg/Quran Adv')");
      stmt.executeUpdate("INSERT OR IGNORE INTO subjects (name) VALUES ('Math')");
      stmt.executeUpdate("INSERT OR IGNORE INTO subjects (name) VALUES ('Science')");
      stmt.executeUpdate("INSERT OR IGNORE INTO subjects (name) VALUES ('History')");
      stmt.executeUpdate("INSERT OR IGNORE INTO subjects (name) VALUES ('Arabic')");
      stmt.executeUpdate("INSERT OR IGNORE INTO subjects (name) VALUES ('Islamic Studies')");
      stmt.executeUpdate("INSERT OR IGNORE INTO subjects (name) VALUES ('English')");
      stmt.executeUpdate("INSERT OR IGNORE INTO subjects (name) VALUES ('Quran Beg/Arabic Adv')");

      // Students
      stmt.executeUpdate("""
            CREATE TABLE IF NOT EXISTS students (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                grade_id INTEGER,
                FOREIGN KEY (grade_id) REFERENCES grades(id)
            );
        """);

      // Join table: student_subject
      stmt.executeUpdate("""
            CREATE TABLE IF NOT EXISTS student_subject (
                student_id INTEGER NOT NULL,
                subject_id INTEGER NOT NULL,
                PRIMARY KEY (student_id, subject_id),
                FOREIGN KEY (student_id) REFERENCES students(id),
                FOREIGN KEY (subject_id) REFERENCES subjects(id)
            );
        """);

      // quarters
      stmt.executeUpdate("""
          CREATE TABLE IF NOT EXISTS quarters (
              id INTEGER PRIMARY KEY AUTOINCREMENT,
              name TEXT NOT NULL,
              start_date TEXT NOT NULL
          )
      """);

      // days
      stmt.executeUpdate("""
          CREATE TABLE IF NOT EXISTS days (
              id INTEGER PRIMARY KEY AUTOINCREMENT,
              date TEXT UNIQUE
          )
      """);

      // daily_scores
      stmt.executeUpdate("""
          CREATE TABLE IF NOT EXISTS daily_scores (
              id INTEGER PRIMARY KEY AUTOINCREMENT,
              student_id INTEGER NOT NULL,
              day_id INTEGER NOT NULL,
              participation INTEGER DEFAULT 0,
              camera INTEGER DEFAULT 0,
              on_time INTEGER DEFAULT 0,
              behaviour INTEGER DEFAULT 0,
              attendance INTEGER DEFAULT 0,
              daily_total INTEGER DEFAULT 0,
              notes TEXT,
              reflections TEXT,
              FOREIGN KEY (student_id) REFERENCES students(id),
              FOREIGN KEY (day_id) REFERENCES days(id),
              UNIQUE(student_id, day_id)
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
