package main.java.app.models;

import main.java.app.dao.GradeDAO;
import main.java.app.dao.StudentDAO;
import main.java.app.dao.SubjectDAO;

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

  // Returns the student id
  public int getId() {
    return id;
  }

  // Returns the student name
  public String getName() {
    return name;
  }

  // Returns the grade name
  public String getGradeName() {
    return GradeDAO.getNameById(gradeId);
  }

  // Returns the grade ID
  public int getGradeId() {
    return gradeId;
  }

  // Returns the student subjects as a list of ids
  public List<Integer> getSubjectIds() {
    return subjectIds;
  }

  public List<String> getSubjectNames() {
    return SubjectDAO.getNamesForIds(subjectIds);
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setGradeId(int gradeId) {
    this.gradeId = gradeId;
  }

  public void setSubjectIds(List<Integer> subjectIds) {
    this.subjectIds = subjectIds;
  }

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
