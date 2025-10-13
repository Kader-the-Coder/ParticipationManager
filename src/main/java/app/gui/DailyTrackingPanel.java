package main.java.app.gui;

import main.java.app.dao.*;
import main.java.app.models.DailyScore;
import main.java.app.models.Student;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.List;
import com.toedter.calendar.JDateChooser;

public class DailyTrackingPanel extends JPanel {

  private final JPanel mainPanel;
  private int dayID;

  public DailyTrackingPanel(MainFrame mainFrame) {
    setLayout(new BorderLayout(20, 20));
    setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    // Top wrapper for title and date controls
    JPanel topWrapper = new JPanel();
    topWrapper.setLayout(new BoxLayout(topWrapper, BoxLayout.Y_AXIS));

    JLabel titleLabel = new JLabel("Manage Participation");
    titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
    titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    topWrapper.add(titleLabel);
    topWrapper.add(Box.createRigidArea(new Dimension(0, 10)));

    // Date controls
    JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 5));
    JButton prevDayButton = new JButton("◀");
    JButton nextDayButton = new JButton("▶");

    JDateChooser datePicker = new JDateChooser();
    JTextField editor = (JTextField) datePicker.getDateEditor().getUiComponent();
    datePicker.setDateFormatString("EEEE (MM/dd)");
    datePicker.setPreferredSize(new Dimension(220, editor.getPreferredSize().height));
    editor.setPreferredSize(new Dimension(200, editor.getPreferredSize().height));
    editor.setHorizontalAlignment(SwingConstants.CENTER);
    editor.setEditable(false);

    datePanel.add(prevDayButton);
    datePanel.add(datePicker);
    datePanel.add(nextDayButton);
    topWrapper.add(datePanel);

    add(topWrapper, BorderLayout.NORTH);

    // Main scrollable area
    mainPanel = new JPanel();
    mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
    JScrollPane scrollPane = new JScrollPane(mainPanel);
    add(scrollPane, BorderLayout.CENTER);

    // Bottom buttons panel
    JPanel bottomPanel = getBottomPanel(mainFrame);
    add(bottomPanel, BorderLayout.SOUTH);

    // Day handling
    LocalDate[] selectedDay = {LocalDate.now()};
    datePicker.setDate(java.sql.Date.valueOf(selectedDay[0]));
    dayID = DaysDAO.getOrCreateDay(selectedDay[0]);

    loadStudents();

    // Listeners
    datePicker.getDateEditor().addPropertyChangeListener(e -> {
      if ("date".equals(e.getPropertyName())) {
        java.util.Date date = (java.util.Date) e.getNewValue();
        if (date != null) {
          selectedDay[0] = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
          loadDayData(selectedDay[0]);
        }
      }
    });

    prevDayButton.addActionListener(ignored -> {
      selectedDay[0] = selectedDay[0].minusDays(1);
      datePicker.setDate(java.util.Date.from(selectedDay[0].atStartOfDay(ZoneId.systemDefault()).toInstant()));
    });

    nextDayButton.addActionListener(ignored -> {
      selectedDay[0] = selectedDay[0].plusDays(1);
      datePicker.setDate(java.util.Date.from(selectedDay[0].atStartOfDay(ZoneId.systemDefault()).toInstant()));
    });
  }

  private JPanel getBottomPanel(MainFrame mainFrame) {
    JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));

    JButton backButton = new JButton("⬅ Back to Menu");
    backButton.addActionListener(ignored -> mainFrame.showPanel("menu"));

    JButton clearDayButton = new JButton("Clear Day");
    clearDayButton.addActionListener(ignored -> {
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

    bottomPanel.add(backButton);
    bottomPanel.add(clearDayButton);
    return bottomPanel;
  }

  private void loadDayData(LocalDate day) {
    dayID = DaysDAO.getOrCreateDay(day);
    loadStudents();
  }

  private void loadStudents() {
    mainPanel.removeAll();
    List<Student> students = StudentDAO.getAllStudents();

    for (Student s : students) {
      DailyScore score = DailyScoresDAO.getScore(s.getId(), dayID);
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
    return score;
  }
}
