package main.java.app.models;

public class DailyScore {
  private int id;
  private int studentId;
  private int dayId;
  private int participation;
  private int camera;
  private int onTime;
  private int behaviour;
  private int attendance;
  private int dailyTotal;
  private String notes;
  private String reflections;

  public DailyScore(
    int id, int studentId, int dayId,
    int participation, int camera, int onTime,
    int behaviour, int attendance, int dailyTotal,
    String notes, String reflections
  ) {
    this.id = id;
    this.studentId = studentId;
    this.dayId = dayId;
    this.participation = participation;
    this.camera = camera;
    this.onTime = onTime;
    this.behaviour = behaviour;
    this.attendance = attendance;
    this.dailyTotal = dailyTotal;
    this.notes = notes;
    this.reflections = reflections;
  }

  // Default constructor for a new day/student: all properties start at 0
  public DailyScore(int studentId, int dayId) {
    this.studentId = studentId;
    this.dayId = dayId;
    this.participation = 0;
    this.camera = 0;
    this.onTime = 0;
    this.behaviour = 0;
    this.attendance = 0;
    recalculateTotal();
  }

  // Recalculate dailyTotal
  public void recalculateTotal() {
    this.dailyTotal = participation + camera + onTime + behaviour + attendance;
  }

  // Getters and setters
  public int getId() { return id; }
  public int getStudentId() { return studentId; }
  public int getDayId() { return dayId; }
  public int getParticipation() { return participation; }
  public int getCamera() { return camera; }
  public int getOnTime() { return onTime; }
  public int getBehaviour() { return behaviour; }
  public int getAttendance() { return attendance; }
  public int getDailyTotal() { return dailyTotal; }
  public String getNotes() { return notes; }
  public String getReflections() { return reflections; }

  public void setParticipation(int participation) {
    this.participation = participation;
    recalculateTotal();
  }
  public void setCamera(int camera) {
    this.camera = camera;
    recalculateTotal();
  }
  public void setOnTime(int onTime) {
    this.onTime = onTime;
    recalculateTotal();
  }
  public void setBehaviour(int behaviour) {
    this.behaviour = behaviour;
    recalculateTotal();
  }
  public void setAttendance(int attendance) {
    this.attendance = attendance;
    recalculateTotal();
  }
  public void setDailyTotal(int dailyTotal) { this.dailyTotal = dailyTotal; }
  public void setNotes(String notes) { this.notes = notes; }
  public void setReflections(String reflections) { this.reflections = reflections; }
}
