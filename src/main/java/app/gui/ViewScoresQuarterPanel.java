package main.java.app.gui;

import main.java.app.dao.QuartersDAO;
import main.java.app.models.Quarter;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.function.Function;

public class ViewScoresQuarterPanel extends JPanel {

  private final JPanel mainPanel;
  private final MainFrame mainFrame;

  public ViewScoresQuarterPanel(MainFrame mainFrame) {
    this.mainFrame = mainFrame;

    setLayout(new BorderLayout(20, 20));
    setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    JLabel titleLabel = new JLabel("View Scores");
    titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
    titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
    add(titleLabel, BorderLayout.NORTH);

    mainPanel = new JPanel();
    mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
    JScrollPane scrollPane = new JScrollPane(mainPanel);
    add(scrollPane, BorderLayout.CENTER);

    JPanel bottomPanel = new JPanel(new BorderLayout());
    JButton backButton = new JButton("⬅ Back to Menu");
    backButton.addActionListener(e -> mainFrame.showPanel("menu"));
    JPanel backButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    backButtonPanel.add(backButton);
    bottomPanel.add(backButtonPanel, BorderLayout.SOUTH);
    add(bottomPanel, BorderLayout.SOUTH);

    loadQuarters();
  }

  public void loadQuarters() {
    mainPanel.removeAll();
    List<Quarter> quarters = QuartersDAO.getAllQuarters();

    Function<Quarter, String[]> fieldInfo = this::getFieldInfo;
    Function<Quarter, java.util.List<JButton>> buttonSupplier = this::getJButtons;

    FrameBuilder<Quarter> builder = new FrameBuilder<>(mainPanel);
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
      ViewScoresWeeksPanel weeksPanel = new ViewScoresWeeksPanel(mainFrame, q);
      mainFrame.showPanel(weeksPanel, "weeks_" + q.getId());
    });
    return java.util.List.of(viewBtn);
  }
}
