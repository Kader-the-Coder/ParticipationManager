package main.java.app.gui.panels;

import main.java.app.gui.frames.MainFrame;
import main.java.app.gui.managers.PanelManager;

import javax.swing.*;
import java.awt.*;

public class MainMenuPanel extends JPanel {

  private final MainFrame mainFrame;
  private final PanelManager panelManager;

  public MainMenuPanel(MainFrame mainFrame, PanelManager panelManager) {
    this.mainFrame = mainFrame;
    this.panelManager = panelManager;

    setLayout(new BorderLayout(20, 20));
    setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

    JLabel titleLabel = new JLabel("Participation Manager");
    titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
    titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
    add(titleLabel, BorderLayout.NORTH);

    add(createButtonPanel(), BorderLayout.CENTER);

    JLabel footerLabel = new JLabel("© " + java.time.Year.now() + " ParticipationManager");
    footerLabel.setHorizontalAlignment(SwingConstants.CENTER);
    footerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    add(footerLabel, BorderLayout.SOUTH);
  }

  private JPanel createButtonPanel() {
    JPanel buttonPanel = new JPanel(new GridLayout(0, 1, 10, 10));

    // iterate over LinkedHashMap to preserve order
    panelManager.getRegisteredPanels().forEach((key, panel) -> {
      if (key.equals("menu")) return; // skip menu itself
      JButton button = new JButton(toLabel(key));
      button.addActionListener(e -> mainFrame.showPanel(key));
      buttonPanel.add(button);
    });

    return buttonPanel;
  }

  private static String toLabel(String key) {
    String label = key.replace("_", " ");
    return Character.toUpperCase(label.charAt(0)) + label.substring(1);
  }
}
