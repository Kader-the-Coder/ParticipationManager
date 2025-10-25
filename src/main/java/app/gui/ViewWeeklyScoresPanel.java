package main.java.app.gui;

import javax.swing.*;
import java.awt.*;

public class ViewWeeklyScoresPanel extends JPanel {

  private final JPanel mainPanel;

  public ViewWeeklyScoresPanel(MainFrame mainFrame) {
    setLayout(new BorderLayout(20, 20));
    setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    // Header
    JLabel titleLabel = new JLabel("View Scores");
    titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
    titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
    add(titleLabel, BorderLayout.NORTH);

    // Main scrollable panel
    mainPanel = new JPanel();
    mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
    JScrollPane scrollPane = new JScrollPane(mainPanel);
    add(scrollPane, BorderLayout.CENTER);

    // Bottom panel with buttons
    JPanel bottomPanel = new JPanel(new BorderLayout());

    JPanel addButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    bottomPanel.add(addButtonPanel, BorderLayout.NORTH);

    JButton backButton = new JButton("⬅ Back to Menu");
    JPanel backButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    backButtonPanel.add(backButton);
    bottomPanel.add(backButtonPanel, BorderLayout.SOUTH);

    add(bottomPanel, BorderLayout.SOUTH);

    backButton.addActionListener(ignored -> mainFrame.showPanel("menu"));

    loadQuarters();
  }

  private void loadQuarters() {
  }


}
