package main.java.app.gui;

import main.java.app.dao.*;
import main.java.app.models.DailyScore;
import main.java.app.models.Student;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class DailyTrackingPanel extends JPanel {

  private final JPanel mainPanel;
  private final int dayID = 1;

  public DailyTrackingPanel(MainFrame mainFrame) {
    setLayout(new BorderLayout(20, 20));
    setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    // Header
    JLabel titleLabel = new JLabel("Manage Participation");
    titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
    titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
    add(titleLabel, BorderLayout.NORTH);

    // Main scrollable panel
    mainPanel = new JPanel();
    mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
    JScrollPane scrollPane = new JScrollPane(mainPanel);
    add(scrollPane, BorderLayout.CENTER);

    // Bottom panel with back button
    JPanel bottomPanel = new JPanel(new BorderLayout());
    JButton backButton = new JButton("⬅ Back to Menu");
    JPanel backButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    backButtonPanel.add(backButton);
    bottomPanel.add(backButtonPanel, BorderLayout.SOUTH);
    add(bottomPanel, BorderLayout.SOUTH);

    loadStudents();

    backButton.addActionListener(ignored -> mainFrame.showPanel("menu"));
  }

  private void loadStudents() {
    mainPanel.removeAll();
    List<Student> students = StudentDAO.getAllStudents();

    for (Student s : students) {
      DailyScore score = DailyScoresDAO.getScore(s.getId(), dayID); // cache score
      List<JButton> buttons = getJButtons(s, score);

      FrameBuilder<Student> builder = new FrameBuilder<>(mainPanel);
      JPanel studentFrame = builder.buildFrame(getFieldInfo(s), buttons);
      studentFrame.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
      mainPanel.add(studentFrame);
    }

    mainPanel.revalidate();
    mainPanel.repaint();
  }

  private List<JButton> getJButtons(Student s, DailyScore score) {
    String[][] buttonData = {
      {"P", "participation"},
      {"C", "camera"},
      {"T", "onTime"},
      {"B", "behaviour"},
      {"A", "attendance"}
    };

    List<JButton> buttons = new ArrayList<>();

    for (String[] data : buttonData) {
      String label = data[0];
      String property = data[1];

      RoundButton button = new RoundButton(label, getColor(score, property));
      button.addActionListener(ignored -> {
        DailyScore updatedScore = toggleProperty(s, property); // toggle and get updated score
        button.setBgColor(getColor(updatedScore, property));   // update RoundButton color
      });

      buttons.add(button);
    }

    return buttons;
  }

  private Color getColor(DailyScore score, String property) {
    if (score == null) return Color.LIGHT_GRAY;

    int value = switch (property) {
      case "attendance" -> score.getAttendance();
      case "participation" -> score.getParticipation();
      case "camera" -> score.getCamera();
      case "onTime" -> score.getOnTime();
      case "behaviour" -> score.getBehaviour();
      default -> 0;
    };

    return value == 1 ? Color.GREEN : Color.RED;
  }

  private String[] getFieldInfo(Student s) {
    return new String[]{
      s.getName(),
      "Grade " + s.getGradeName(),
      "Subjects: " + String.join(", ", s.getSubjectNames())
    };
  }

  private DailyScore toggleProperty(Student student, String property) {
    DailyScore score = DailyScoresDAO.getScore(student.getId(), dayID);
    if (score == null) score = new DailyScore(student.getId(), dayID);

    switch (property) {
      case "attendance" -> score.setAttendance(score.getAttendance() == 1 ? 2 : 1);
      case "participation" -> score.setParticipation(score.getParticipation() == 1 ? 2 : 1);
      case "camera" -> score.setCamera(score.getCamera() == 1 ? 2 : 1);
      case "onTime" -> score.setOnTime(score.getOnTime() == 1 ? 2 : 1);
      case "behaviour" -> score.setBehaviour(score.getBehaviour() == 1 ? 2 : 1);
    }

    DailyScoresDAO.insertOrUpdate(score);
    System.out.printf("%s set to %s%n", property, getPropertyValue(score, property));
    return score; // return updated score
  }

  private int getPropertyValue(DailyScore score, String property) {
    return switch (property) {
      case "attendance" -> score.getAttendance();
      case "participation" -> score.getParticipation();
      case "camera" -> score.getCamera();
      case "onTime" -> score.getOnTime();
      case "behaviour" -> score.getBehaviour();
      default -> 0;
    };
  }
}
