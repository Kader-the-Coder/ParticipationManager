package main.java.app.gui.panels;

import main.java.app.dao.DailyScoresDAO;
import main.java.app.dao.DaysDAO;
import main.java.app.dao.QuartersDAO;
import main.java.app.gui.components.RoundButton;
import main.java.app.gui.frames.MainFrame;
import main.java.app.gui.templates.PanelTemplate;
import main.java.app.models.Quarter;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;

public class ViewScoresWeeksPanel extends BasePanel {

  private final JPanel mainPanel;
  private final Quarter quarter;

  public ViewScoresWeeksPanel(MainFrame mainFrame, Quarter quarter) {
    super(mainFrame);
    this.quarter = quarter;

    JLabel headerLabel = new JLabel("Weeks of Quarter starting " + quarter.getStartDate());
    headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
    headerLabel.setHorizontalAlignment(SwingConstants.CENTER);
    setHeaderComponent(headerLabel);

    mainPanel = new JPanel();
    mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
    setBodyComponent(mainPanel);

    backButton.removeActionListener(backButton.getActionListeners()[0]);
    backButton.setText("Back");
    backButton.addActionListener(e -> mainFrame.showPanel("scores"));
  }

  @Override
  public void refresh() {
    loadWeeks();
  }

  private void loadWeeks() {
    mainPanel.removeAll();

    List<Quarter> allQuarters = QuartersDAO.getAllQuarters();
    allQuarters.sort((a, b) -> a.getStartDate().compareTo(b.getStartDate()));

    LocalDate quarterStart = quarter.getStartDate();
    LocalDate quarterEnd;

    int index = -1;
    for (int i = 0; i < allQuarters.size(); i++) {
      if (allQuarters.get(i).getId() == quarter.getId()) {
        index = i;
        break;
      }
    }
    if (index == -1) {
      index = allQuarters.size() - 1;
    }

    if (index < allQuarters.size() - 1) {
      LocalDate nextQuarterStart = allQuarters.get(index + 1).getStartDate();
      quarterEnd = nextQuarterStart.minusDays(1);
    } else {
      Integer lastDayId = DailyScoresDAO.getLastDayIdForQuarter(quarter.getId());
      if (lastDayId != null) {
        quarterEnd = DaysDAO.getDateForDayId(lastDayId);
      } else {
        quarterEnd = quarterStart.plusDays(6);
      }
    }

    long daysBetween = quarterEnd.toEpochDay() - quarterStart.toEpochDay() + 1;
    int totalWeeks = (int) Math.ceil(daysBetween / 7.0);

    Function<Integer, String[]> fieldInfo = weekNum -> {
      LocalDate start = quarterStart.plusWeeks(weekNum - 1);
      LocalDate end = start.plusDays(6);
      if (end.isAfter(quarterEnd)) end = quarterEnd;
      Double avg = DailyScoresDAO.getAverageScoreForWeek(quarterStart, weekNum);
      String scoreText = (avg != null) ? String.format("Avg: %.2f", avg) : "Not recorded";
      return new String[]{"Week " + weekNum, start + " – " + end, scoreText};
    };

    Function<Integer, java.util.List<JButton>> buttonsSupplier = weekNum -> {
      RoundButton viewBtn = new RoundButton("🔍", Color.WHITE);
      viewBtn.addActionListener(e -> {
        ViewScoresStudentsPanel studentsPanel = new ViewScoresStudentsPanel(super.mainFrame, quarter, weekNum);
        super.mainFrame.showPanel(studentsPanel);
      });
      return java.util.List.of(viewBtn);
    };

    PanelTemplate<Integer> builder = new PanelTemplate<>(mainPanel);

    for (int i = 1; i <= totalWeeks; i++) {
      JPanel frame = builder.buildFrame(fieldInfo.apply(i), buttonsSupplier.apply(i));
      frame.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
      mainPanel.add(frame);
    }

    mainPanel.revalidate();
    mainPanel.repaint();
  }
}
