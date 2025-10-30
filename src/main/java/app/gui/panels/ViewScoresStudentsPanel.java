package main.java.app.gui.panels;

import main.java.app.dao.DailyScoresDAO;
import main.java.app.dao.StudentsDAO;
import main.java.app.gui.components.GradeSubjectFilter;
import main.java.app.gui.frames.MainFrame;
import main.java.app.gui.templates.HeaderPanelTemplate;
import main.java.app.gui.templates.PanelTemplate;
import main.java.app.models.Quarter;
import main.java.app.models.Student;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ViewScoresStudentsPanel extends BasePanel {

  private final JPanel mainPanel;
  private final Quarter quarter;
  private final int weekNum;
  private final MainFrame mainFrame;

  private final GradeSubjectFilter gradeSubjectFilter;

  public ViewScoresStudentsPanel(MainFrame mainFrame, Quarter quarter, int weekNum) {
    super(mainFrame);
    this.mainFrame = mainFrame;
    this.quarter = quarter;
    this.weekNum = weekNum;

    // Filters + header using template
    gradeSubjectFilter = new GradeSubjectFilter(25, (g, s) -> applyFilters());
    HeaderPanelTemplate header = new HeaderPanelTemplate(
      "Weekly Scores - Week " + weekNum,
      25,
      gradeSubjectFilter,
      null // no date picker needed here
    );

    setHeaderComponent(header);

    // Main scrollable panel
    mainPanel = new JPanel();
    mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
    setBodyComponent(new JScrollPane(mainPanel));

    // Back button
    backButton.removeActionListener(backButton.getActionListeners()[0]);
    backButton.setText("Back");
    backButton.addActionListener(e -> {
      ViewScoresWeeksPanel weeksPanel = new ViewScoresWeeksPanel(mainFrame, quarter);
      mainFrame.showPanel(weeksPanel);
    });
  }

  @Override
  public void refresh() {
    applyFilters();
  }

  private void applyFilters() {
    Integer gradeId = gradeSubjectFilter.getSelectedGradeId();
    Integer subjectId = gradeSubjectFilter.getSelectedSubjectId();

    List<Student> filtered = StudentsDAO.getFilteredStudents(gradeId, subjectId);
    loadStudentScores(filtered);
  }

  private void loadStudentScores(List<Student> students) {
    mainPanel.removeAll();
    PanelTemplate<Student> builder = new PanelTemplate<>(mainPanel);

    for (Student s : students) {
      Double weeklyAvg = DailyScoresDAO.getWeeklyScoreForStudent(
        s.getId(), quarter.getId(), weekNum
      );
      String scoreText = (weeklyAvg != null) ? String.format("%.2f", weeklyAvg) : "No Data";

      JButton scoreButton = new JButton(scoreText);
      scoreButton.addActionListener(e -> {
        // Placeholder for future expansion
      });

      String[] fieldInfo = {s.getName(), s.getGradeName()};
      List<JButton> buttons = java.util.List.of(scoreButton);

      JPanel frame = builder.buildFrame(fieldInfo, buttons);
      frame.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
      mainPanel.add(frame);
    }

    mainPanel.revalidate();
    mainPanel.repaint();
  }
}
