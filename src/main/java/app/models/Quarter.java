package main.java.app.models;

import main.java.app.dao.QuartersDAO;

import java.time.LocalDate;
import java.util.List;

import static main.java.app.dao.QuartersDAO.getAllQuarters;

public class Quarter {

  private int id;
  private String name;
  private LocalDate startDate;

  /** Constructor for new quarter (id not yet assigned)
   */
  public Quarter(String name, LocalDate startDate) {
    this.name = name;
    this.startDate = startDate;
  }

  /** Constructor with id (for quarters loaded from DB)
   */
  public Quarter(int id, String name, LocalDate startDate) {
    this.id = id;
    this.name = name;
    this.startDate = startDate;
  }

  // Getters and Setters
  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public LocalDate getStartDate() {
    return startDate;
  }

  public void setStartDate(LocalDate startDate) throws IllegalArgumentException {
    List<Quarter> quarters = QuartersDAO.getAllQuarters();

    // Compute the Monday of the new startDate’s week
    LocalDate newWeekStart = startDate.minusDays(startDate.getDayOfWeek().getValue() - 1);

    for (Quarter q : quarters) {
      LocalDate existingWeekStart = q.getStartDate().minusDays(q.getStartDate().getDayOfWeek().getValue() - 1);
      if (existingWeekStart.equals(newWeekStart)) {
        throw new IllegalArgumentException("A quarter already exists for this week.");
      }
    }

    this.startDate = startDate;
  }

  @Override
  public String toString() {
    return name + " (" + startDate + ")";
  }
}
