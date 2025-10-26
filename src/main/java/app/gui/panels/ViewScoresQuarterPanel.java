package main.java.app.gui.panels;

import main.java.app.dao.QuartersDAO;
import main.java.app.gui.components.RoundButton;
import main.java.app.gui.frames.MainFrame;
import main.java.app.gui.templates.PanelTemplate;
import main.java.app.models.Quarter;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ViewScoresQuarterPanel extends BasePanel {

  private final JPanel mainPanel;

  public ViewScoresQuarterPanel(MainFrame mainFrame) {
    super(mainFrame);

    // Header
    JLabel headerLabel = new JLabel("View Scores");
    headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
    headerLabel.setHorizontalAlignment(SwingConstants.CENTER);
    setHeaderComponent(headerLabel);

    // Main scrollable panel
    mainPanel = new JPanel();
    mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
    setBodyComponent(mainPanel);
  }

  /**
   * Refresh the panel by reloading quarters from the database.
   * This will be called automatically by MainFrame.showPanel().
   */
  @Override
  public void refresh() {
    loadQuarters();
  }

  private void loadQuarters() {
    mainPanel.removeAll();

    // Fetch latest quarters from DB and sort by start date
    List<Quarter> quarters = QuartersDAO.getAllQuarters();
    quarters.sort((a, b) -> a.getStartDate().compareTo(b.getStartDate()));

    PanelTemplate<Quarter> builder = new PanelTemplate<>(mainPanel);

    for (int i = 0; i < quarters.size(); i++) {
      Quarter q = quarters.get(i);
      int quarterNumber = i + 1;

      JPanel frame = builder.buildFrame(getFieldInfo(q, quarterNumber), getJButtons(q));
      frame.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
      mainPanel.add(frame);
    }

    mainPanel.revalidate();
    mainPanel.repaint();
  }

  private String[] getFieldInfo(Quarter q, int quarterNumber) {
    return new String[]{
      "Quarter " + quarterNumber,
      "Start Date: " + q.getStartDate(),
      "End Date: To be implemented."
    };
  }

  private java.util.List<JButton> getJButtons(Quarter q) {
    RoundButton viewBtn = new RoundButton("🔍", Color.WHITE);
    viewBtn.addActionListener(e -> {
      ViewScoresWeeksPanel weeksPanel = new ViewScoresWeeksPanel(super.mainFrame, q);
      super.mainFrame.showPanel(weeksPanel);
    });
    return java.util.List.of(viewBtn);
  }
}
