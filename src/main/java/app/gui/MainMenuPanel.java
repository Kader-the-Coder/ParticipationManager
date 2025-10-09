package main.java.app.gui;

import javax.swing.*;
import java.awt.*;

public class MainMenuPanel extends JPanel {

  private final MainFrame mainFrame;

  public MainMenuPanel(MainFrame mainFrame) {
    this.mainFrame = mainFrame;
    setLayout(new BorderLayout(20, 20));
    setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

    JLabel titleLabel = new JLabel("Participation Manager");
    titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
    titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
    add(titleLabel, BorderLayout.NORTH);

    JPanel buttonPanel = getjPanel(mainFrame);

    add(buttonPanel, BorderLayout.CENTER);

    JLabel footerLabel = new JLabel("© " + java.time.Year.now() + " ParticipationManager");
    footerLabel.setHorizontalAlignment(SwingConstants.CENTER);
    footerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    add(footerLabel, BorderLayout.SOUTH);
  }

  private static JPanel getjPanel(MainFrame mainFrame) {
    JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new GridLayout(0, 1, 10, 10));

    JButton studentsButton = new JButton("Manage Students");
    JButton quartersButton = new JButton("Manage Quarters");
    JButton settingsButton = new JButton("Settings");

    studentsButton.addActionListener(e -> mainFrame.showPanel("students"));
    quartersButton.addActionListener(e -> mainFrame.showPanel("quarters"));
    settingsButton.addActionListener(e -> mainFrame.showPanel("settings"));

    buttonPanel.add(studentsButton);
    buttonPanel.add(quartersButton);
    buttonPanel.add(settingsButton);
    return buttonPanel;
  }
}
