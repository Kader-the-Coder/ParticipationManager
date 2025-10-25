package main.java.app.gui.panels;

import main.java.app.dao.QuartersDAO;
import main.java.app.gui.components.RoundButton;
import main.java.app.gui.frames.MainFrame;
import main.java.app.gui.templates.PanelTemplate;
import main.java.app.models.Quarter;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.function.Function;

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

    loadQuarters();
  }

  public void loadQuarters() {
    mainPanel.removeAll();
    List<Quarter> quarters = QuartersDAO.getAllQuarters();

    Function<Quarter, String[]> fieldInfo = this::getFieldInfo;
    Function<Quarter, java.util.List<JButton>> buttonSupplier = this::getJButtons;

    PanelTemplate<Quarter> builder = new PanelTemplate<>(mainPanel);
    for (Quarter q : quarters) {
      JPanel frame = builder.buildFrame(fieldInfo.apply(q), buttonSupplier.apply(q));
      frame.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
      mainPanel.add(frame);
    }

    mainPanel.revalidate();
    mainPanel.repaint();
  }

  private String[] getFieldInfo(Quarter q) {
    List<Quarter> quarters = QuartersDAO.getAllQuarters();
    quarters.sort((a, b) -> a.getStartDate().compareTo(b.getStartDate()));

    int quarterNumber = 1;
    for (Quarter current : quarters) {
      if (current.getId() == q.getId()) break;
      quarterNumber++;
    }

    return new String[]{
      "Quarter " + quarterNumber,
      "Start Date: " + q.getStartDate(),
      "End Date: To be implemented."
    };
  }

  private java.util.List<JButton> getJButtons(Quarter q) {
    RoundButton viewBtn = new RoundButton("🔍", Color.WHITE);
    viewBtn.addActionListener(e -> {
      // Create new weeks panel dynamically
      ViewScoresWeeksPanel weeksPanel = new ViewScoresWeeksPanel(super.mainFrame, q);
      super.mainFrame.showPanel(weeksPanel);
    });
    return java.util.List.of(viewBtn);
  }
}
