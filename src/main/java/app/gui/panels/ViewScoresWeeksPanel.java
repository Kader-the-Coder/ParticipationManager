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

    loadWeeks();
  }

  private void loadWeeks() {
    mainPanel.removeAll();

    // Determine quarter end date
    List<Quarter> allQuarters = QuartersDAO.getAllQuarters();
    allQuarters.sort((a, b) -> a.getStartDate().compareTo(b.getStartDate()));

    LocalDate quarterEnd;
    int index = allQuarters.indexOf(quarter);
    if (index < allQuarters.size() - 1) {
      quarterEnd = allQuarters.get(index + 1).getStartDate().minusDays(1);
    } else {
      Integer lastDayId = DailyScoresDAO.getLastDayIdForQuarter(quarter.getId());
      if (lastDayId != null) {
        quarterEnd = DaysDAO.getDateForDayId(lastDayId);
      } else {
        quarterEnd = quarter.getStartDate().plusDays(6); // at least one week
      }
    }

    int totalWeeks = (int) ((quarterEnd.toEpochDay() - quarter.getStartDate().toEpochDay()) / 7) + 1;

    Function<Integer, String[]> fieldInfo = weekNum -> {
      LocalDate start = quarter.getStartDate().plusWeeks(weekNum - 1);
      LocalDate end = start.plusDays(6);
      Double avg = DailyScoresDAO.getAverageScoreForWeek(quarter.getStartDate(), weekNum);
      String scoreText = (avg != null) ? String.format("Avg: %.2f", avg) : "Not recorded";
      return new String[]{"Week " + weekNum, start + " – " + end, scoreText};
    };

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
