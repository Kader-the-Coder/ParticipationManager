package main.java.app.gui.components;

import main.java.app.dao.GradesDAO;
import main.java.app.dao.SubjectsDAO;

import javax.swing.*;
import java.awt.*;
import java.util.function.BiConsumer;

public class GradeSubjectFilter extends JPanel {

  private final JComboBox<String> gradeCombo;
  private final JComboBox<String> subjectCombo;
  private final String allGrades = "X";
  private final String allSubjects = "XXXX";

  public GradeSubjectFilter(int height, BiConsumer<String, String> onChange) {
    this.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));

    gradeCombo = new JComboBox<>();
    subjectCombo = new JComboBox<>();

    // Populate grades
    gradeCombo.addItem(allGrades);
    for (String g : GradesDAO.getAllGradeNames()) {
      gradeCombo.addItem(g);
    }

    // Populate subjects
    subjectCombo.addItem(allSubjects);
    for (String s : SubjectsDAO.getAllSubjectNames()) {
      subjectCombo.addItem(s);
    }

    // Set uniform size for both combo boxes
    int heightAdjustment = 7; // To ensure height of filters match that of the date picker.
    height = height - heightAdjustment;
    Dimension comboSize = new Dimension(40, height);
    gradeCombo.setPreferredSize(comboSize);
    gradeCombo.setMaximumSize(comboSize);
    comboSize = new Dimension(60, height);
    subjectCombo.setPreferredSize(comboSize);
    subjectCombo.setMaximumSize(comboSize);

    // Add listeners
    gradeCombo.addActionListener(e -> onChange.accept(getSelectedGrade(), getSelectedSubject()));
    subjectCombo.addActionListener(e -> onChange.accept(getSelectedGrade(), getSelectedSubject()));

    this.add(gradeCombo);
    this.add(subjectCombo);

    // Ensure the panel respects height
    this.setPreferredSize(new Dimension(this.getPreferredSize().width, height));
    this.setMaximumSize(new Dimension(Integer.MAX_VALUE, height));
  }

  public String getSelectedGrade() {
    return (String) gradeCombo.getSelectedItem();
  }

  public String getSelectedSubject() {
    return (String) subjectCombo.getSelectedItem();
  }

  public Integer getSelectedGradeId() {
    String grade = getSelectedGrade();
    if (grade == null || grade.equals(allGrades)) return null;
    return GradesDAO.getGradeIdByName(grade);
  }

  public Integer getSelectedSubjectId() {
    String subject = getSelectedSubject();
    if (subject == null || subject.equals(allSubjects)) return null;
    return SubjectsDAO.getSubjectIdByName(subject);
  }
}
