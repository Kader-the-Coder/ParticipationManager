package main.java.app.gui.panels;

import main.java.app.dao.DailyScoresDAO;
import main.java.app.dao.StudentsDAO;
import main.java.app.gui.frames.MainFrame;
import main.java.app.gui.templates.PanelTemplate;
import main.java.app.models.Quarter;
import main.java.app.models.Student;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;

public class ViewScoresStudentsPanel extends BasePanel {

  private final JPanel mainPanel;
  private final Quarter quarter;
  private final int weekNum;
  private final MainFrame mainFrame;

  public ViewScoresStudentsPanel(MainFrame mainFrame, Quarter quarter, int weekNum) {
    super(mainFrame);
    this.mainFrame = mainFrame;
    this.quarter = quarter;
    this.weekNum = weekNum;

    // Header
    JLabel headerLabel = new JLabel("Weekly Scores - Week " + weekNum);
    headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
    headerLabel.setHorizontalAlignment(SwingConstants.CENTER);
    setHeaderComponent(headerLabel);

    // Main scrollable panel
    mainPanel = new JPanel();
    mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
    setBodyComponent(new JScrollPane(mainPanel));

    // Back button goes to the week panel
    backButton.removeActionListener(backButton.getActionListeners()[0]);
    backButton.setText("Back");
    backButton.addActionListener(e -> {
      ViewScoresWeeksPanel weeksPanel = new ViewScoresWeeksPanel(mainFrame, quarter);
      mainFrame.showPanel(weeksPanel);
    });
  }

  @Override
  public void refresh() {
    loadStudentScores();
  }

  private void loadStudentScores() {
    mainPanel.removeAll();

    List<Student> students = StudentsDAO.getAllStudents();

    // Field info supplier for each student row
    Function<Student, String[]> fieldInfo = student -> {
      Double weeklyAvg = DailyScoresDAO.getWeeklyScoreForStudent(
        student.getId(), quarter.getId(), weekNum
      );
      String scoreText = (weeklyAvg != null) ? String.format("%.2f", weeklyAvg) : "No Data";
      return new String[]{student.getName(), student.getGradeName(), scoreText};
    };

    // No additional buttons for now
    Function<Student, List<JButton>> buttonsSupplier = student -> java.util.List.of();

    PanelTemplate<Student> builder = new PanelTemplate<>(mainPanel);

    for (Student s : students) {
      JPanel frame = builder.buildFrame(fieldInfo.apply(s), buttonsSupplier.apply(s));
      frame.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
      mainPanel.add(frame);
    }

    mainPanel.revalidate();
    mainPanel.repaint();
  }

}
