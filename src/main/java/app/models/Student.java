package main.java.app.models;

import main.java.app.dao.StudentDAO;
import java.util.List;

/**
 * Represents a student record in the database.
 */
public class Student {
  private int id;
  private String name;
  private int gradeId;
  private List<Integer> subjectIds; // multiple subjects

  public Student(int id, String name, int gradeId, List<Integer> subjectIds) {
    this.id = id;
    this.name = name;
    this.gradeId = gradeId;
    this.subjectIds = subjectIds;
  }

  public Student(String name, int gradeId, List<Integer> subjectIds) {
    this(-1, name, gradeId, subjectIds);
  }

  public int getId() { return id; }
  public String getName() { return name; }
  public int getGradeId() { return gradeId; }
  public List<Integer> getSubjectIds() { return subjectIds; }

  public void setName(String name) { this.name = name; }
  public void setGradeId(int gradeId) { this.gradeId = gradeId; }
  public void setSubjectIds(List<Integer> subjectIds) { this.subjectIds = subjectIds; }

  /**
   * Updates this student and their subjects in the database.
   */
  public void updateInDB() {
    StudentDAO.updateStudent(this);
  }

  /**
   * Deletes this student and their subjects from the database.
   */
  public void deleteFromDB() {
    StudentDAO.deleteStudent(this.id);
  }
}
