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
  private final JComboBox<String> gradeFilter;
  private final JComboBox<String> subjectFilter;
  private int dayID;
  final private String allGrades = "XX";
  final private String allSubjects = "XXXXX";

  public DailyTrackingPanel(MainFrame mainFrame) {
    setLayout(new BorderLayout(10, 10));
    setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // reduced padding

    // Top wrapper
    JPanel topWrapper = new JPanel();
    topWrapper.setLayout(new BoxLayout(topWrapper, BoxLayout.Y_AXIS));

    // Combined date & filter row
    JPanel topBar = new JPanel(new BorderLayout(5, 0));

    // Left: Date picker
    JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
    JButton prevDayButton = new JButton("◀");
    JButton nextDayButton = new JButton("▶");

    JDateChooser datePicker = new JDateChooser();
    JTextField editor = (JTextField) datePicker.getDateEditor().getUiComponent();
    datePicker.setDateFormatString("EE (MM/dd)");
    int uniformHeight = editor.getPreferredSize().height - 2;
    datePicker.setPreferredSize(new Dimension(100, uniformHeight));
    editor.setHorizontalAlignment(SwingConstants.CENTER);
    editor.setEditable(false);

    //datePanel.add(prevDayButton);
    datePanel.add(datePicker);
    //datePanel.add(nextDayButton);

    // Right: Filters
    JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 2, 0));
    gradeFilter = new JComboBox<>();
    subjectFilter = new JComboBox<>();
    gradeFilter.setPreferredSize(new Dimension(45, uniformHeight));
    subjectFilter.setPreferredSize(new Dimension(70, uniformHeight));

    gradeFilter.addItem(allGrades);
    for (String g : GradeDAO.getAllGradeNames()) gradeFilter.addItem(g);

    subjectFilter.addItem(allSubjects);
    for (String s : SubjectDAO.getAllSubjectNames()) subjectFilter.addItem(s);

    // Restore last selected filters
    String lastGrade = SettingsDAO.loadSetting("last_grade_filter", allGrades);
    String lastSubject = SettingsDAO.loadSetting("last_subject_filter", allSubjects);
    gradeFilter.setSelectedItem(lastGrade);
    subjectFilter.setSelectedItem(lastSubject);

    filterPanel.add(gradeFilter);
    filterPanel.add(subjectFilter);

    topBar.add(datePanel, BorderLayout.WEST);
    topBar.add(filterPanel, BorderLayout.EAST);

    topWrapper.add(topBar);

    add(topWrapper, BorderLayout.NORTH);

    // Main area
    mainPanel = new JPanel();
    mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
    JScrollPane scrollPane = new JScrollPane(mainPanel);
    add(scrollPane, BorderLayout.CENTER);

    // Bottom panel
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

    gradeFilter.addActionListener(e -> {
      loadStudents();
      SettingsDAO.saveSetting("last_grade_filter", (String) gradeFilter.getSelectedItem());
    });

    subjectFilter.addActionListener(e -> {
      loadStudents();
      SettingsDAO.saveSetting("last_subject_filter", (String) subjectFilter.getSelectedItem());
    });
  }

  private JPanel getBottomPanel(MainFrame mainFrame) {
    JPanel bottomPanel = new JPanel();
    bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));
    bottomPanel.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));

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

    // Compact buttons
    Dimension buttonSize = new Dimension(backButton.getPreferredSize().width, 20);
    backButton.setPreferredSize(buttonSize);
    backButton.setMaximumSize(buttonSize);
    backButton.setMinimumSize(buttonSize);

    buttonSize = new Dimension(clearDayButton.getPreferredSize().width, 20);
    clearDayButton.setPreferredSize(buttonSize);
    clearDayButton.setMaximumSize(buttonSize);
    clearDayButton.setMinimumSize(buttonSize);

    backButton.setAlignmentY(Component.CENTER_ALIGNMENT);
    clearDayButton.setAlignmentY(Component.CENTER_ALIGNMENT);

    bottomPanel.add(Box.createHorizontalGlue());
    bottomPanel.add(backButton);
    bottomPanel.add(Box.createRigidArea(new Dimension(5, 0)));
    bottomPanel.add(clearDayButton);
    bottomPanel.add(Box.createHorizontalGlue());

    bottomPanel.setPreferredSize(new Dimension(bottomPanel.getPreferredSize().width, 24));
    bottomPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));

    return bottomPanel;
  }

  private void loadDayData(LocalDate day) {
    dayID = DaysDAO.getOrCreateDay(day);
    loadStudents();
  }

  private void loadStudents() {
    mainPanel.removeAll();

    String selectedGrade = (String) gradeFilter.getSelectedItem();
    String selectedSubject = (String) subjectFilter.getSelectedItem();

    Integer gradeId = (selectedGrade != null && !selectedGrade.equals(allGrades))
      ? GradeDAO.getGradeIdByName(selectedGrade)
      : null;

    Integer subjectId = (selectedSubject != null && !selectedSubject.equals(allSubjects))
      ? SubjectDAO.getSubjectIdByName(selectedSubject)
      : null;

    List<Student> students = StudentDAO.getFilteredStudents(gradeId, subjectId);

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
      s.getGradeName(),
      String.join(", ", s.getSubjectNames())
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
