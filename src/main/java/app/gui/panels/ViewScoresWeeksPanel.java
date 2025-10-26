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

    // Header
    JLabel headerLabel = new JLabel("Weeks of Quarter starting " + quarter.getStartDate());
    headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
    headerLabel.setHorizontalAlignment(SwingConstants.CENTER);
    setHeaderComponent(headerLabel);

    // Main scrollable panel
    mainPanel = new JPanel();
    mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
    setBodyComponent(mainPanel);

    // Replace  existing backButton to go to the Quarters panel instead
    backButton.removeActionListener(backButton.getActionListeners()[0]);
    backButton.setText("Back");
    backButton.addActionListener(e -> mainFrame.showPanel("scores"));

    loadWeeks();
  }

  private void loadWeeks() {
    mainPanel.removeAll();

    // Fetch and sort all quarters
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
      // Quarter not found in list — treat it as the last quarter (safe fallback)
      index = allQuarters.size() - 1;
    }

    if (index < allQuarters.size() - 1) {
      // There is a following quarter
      LocalDate nextQuarterStart = allQuarters.get(index + 1).getStartDate();
      quarterEnd = nextQuarterStart.minusDays(1); // end is day before next quarter
    } else {
      // No following quarter, extend to last recorded day
      Integer lastDayId = DailyScoresDAO.getLastDayIdForQuarter(quarter.getId());

      if (lastDayId != null) {
        quarterEnd = DaysDAO.getDateForDayId(lastDayId);
      } else {
        // No recorded days, at least one week
        quarterEnd = quarterStart.plusDays(6);
      }
    }

    // Compute total weeks by counting full and partial weeks
    long daysBetween = quarterEnd.toEpochDay() - quarterStart.toEpochDay() + 1;
    int totalWeeks = (int) Math.ceil(daysBetween / 7.0);

    // Build field info for each week
    Function<Integer, String[]> fieldInfo = weekNum -> {
      LocalDate start = quarterStart.plusWeeks(weekNum - 1);
      LocalDate end = start.plusDays(6);
      if (end.isAfter(quarterEnd)) end = quarterEnd; // don't go past quarter end
      Double avg = DailyScoresDAO.getAverageScoreForWeek(quarterStart, weekNum);
      String scoreText = (avg != null) ? String.format("Avg: %.2f", avg) : "Not recorded";
      return new String[]{"Week " + weekNum, start + " – " + end, scoreText};
    };

    // Build buttons
    Function<Integer, java.util.List<JButton>> buttonsSupplier = weekNum -> {
      RoundButton btn = new RoundButton("🔍", Color.WHITE);
      btn.addActionListener(e -> JOptionPane.showMessageDialog(
        this,
        "Viewing daily scores for Week " + weekNum + " of " + quarter.getName() + " is not yet implemented.",
        "Not Implemented",
        JOptionPane.INFORMATION_MESSAGE
      ));
      return java.util.List.of(btn);
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
