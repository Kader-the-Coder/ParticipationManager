package main.java.app.gui.panels;

import main.java.app.gui.frames.MainFrame;

import javax.swing.*;
import java.awt.*;

public class SettingsPanel extends BasePanel {

  public SettingsPanel(MainFrame mainFrame) {
    super(mainFrame);

    // Header
    JLabel headerLabel = new JLabel("Settings");
    headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
    headerLabel.setHorizontalAlignment(SwingConstants.CENTER);
    setHeaderComponent(headerLabel);

    // Body placeholder
    JPanel body = new JPanel();
    body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
    setBodyComponent(body);

    // To be implemented
    JLabel infoLabel = new JLabel("Settings will be implemented here.");
    infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    body.add(Box.createVerticalStrut(10));
    body.add(infoLabel);
  }
}
