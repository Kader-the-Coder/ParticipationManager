package main.java.app.gui.panels;

import main.java.app.dao.DailyScoresDAO;
import main.java.app.dao.DaysDAO;
import main.java.app.dao.StudentsDAO;
import main.java.app.gui.components.GradeSubjectFilter;
import main.java.app.gui.components.RoundButton;
import main.java.app.gui.frames.MainFrame;
import main.java.app.gui.templates.HeaderPanelTemplate;
import main.java.app.gui.templates.PanelTemplate;
import main.java.app.models.DailyScore;
import main.java.app.models.Student;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DailyTrackingPanel extends BasePanel {

  private int dayID;
  private final GradeSubjectFilter gradeSubjectFilter;

  public DailyTrackingPanel(MainFrame mainFrame) {
    super(mainFrame);

    // Filters + header using template
    gradeSubjectFilter = new GradeSubjectFilter(25, (g, s) -> loadStudents());

    HeaderPanelTemplate header = new HeaderPanelTemplate(
      "Daily Tracking",
      25,
      gradeSubjectFilter,
      newDate -> {
        dayID = DaysDAO.getOrCreateDay(newDate);
        loadStudents();
      }
    );

    setHeaderComponent(header);

    // Main panel
    JPanel mainContent = new JPanel();
    mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
    setBodyComponent(mainContent);

    // Footer button: clear day
    JButton clearDayButton = new JButton("Clear Day");
    clearDayButton.addActionListener(e -> {
      int confirm = JOptionPane.showConfirmDialog(
        this,
        "Are you sure you want to delete all data for this day?",
        "Confirm Delete",
        JOptionPane.YES_NO_OPTION
      );
      if (confirm == JOptionPane.YES_OPTION) {
        DailyScoresDAO.deleteScoresForDay(dayID);
        loadStudents();
      }
    });
    addFooterButton(clearDayButton);

    // Day handling
    LocalDate selectedDay = LocalDate.now();
    dayID = DaysDAO.getOrCreateDay(selectedDay);
    loadStudents();
  }

  private void loadStudents() {
    JPanel mainPanel = (JPanel) bodyScrollPane.getViewport().getView();
    mainPanel.removeAll();

    Integer gradeId = gradeSubjectFilter.getSelectedGradeId();
    Integer subjectId = gradeSubjectFilter.getSelectedSubjectId();

    List<Student> students = StudentsDAO.getFilteredStudents(gradeId, subjectId);

    for (Student s : students) {
      DailyScore score = DailyScoresDAO.getScore(s.getId(), dayID);
      List<JButton> buttons = getJButtons(s, score);

      PanelTemplate<Student> builder = new PanelTemplate<>(mainPanel);
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
      button.addActionListener(e -> {
        DailyScore updatedScore = toggleProperty(s, property);
        button.setBgColor(getColor(updatedScore, property));
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
      s.getGradeName(),
      String.join(", ", s.getSubjectNames())
    };
  }

  private DailyScore toggleProperty(Student student, String property) {
    DailyScore score = DailyScoresDAO.getScore(student.getId(), dayID);

    if (score == null) {
      score = new DailyScore(student.getId(), dayID);
      DailyScoresDAO.insertOrUpdate(score);
    }

    switch (property) {
      case "attendance" -> score.setAttendance(score.getAttendance() == 0 ? 1 : 0);
      case "participation" -> score.setParticipation(score.getParticipation() == 0 ? 1 : 0);
      case "camera" -> score.setCamera(score.getCamera() == 0 ? 1 : 0);
      case "onTime" -> score.setOnTime(score.getOnTime() == 0 ? 1 : 0);
      case "behaviour" -> score.setBehaviour(score.getBehaviour() == 0 ? 1 : 0);
    }

    DailyScoresDAO.insertOrUpdate(score);
    return score;
  }
}
