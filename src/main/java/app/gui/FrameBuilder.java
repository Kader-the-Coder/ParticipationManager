package main.java.app.gui;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Generic builder for creating listable frames for any data model.
 */
public class FrameBuilder<T> {

  public FrameBuilder(JPanel container) {
  }

  public JPanel buildFrame(String[] info, List<JButton> buttons) {
    JPanel frame = new JPanel(new BorderLayout());
    frame.setBorder(BorderFactory.createCompoundBorder(
      BorderFactory.createLineBorder(Color.GRAY),
      BorderFactory.createEmptyBorder(5, 5, 5, 5)
    ));

    // Buttons panel
    JPanel buttonsPanel = new JPanel(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();
    c.gridx = 0;
    c.gridy = 0;
    c.anchor = GridBagConstraints.CENTER;
    if (!buttons.isEmpty()) {
      for (JButton button : buttons) {
        buttonsPanel.add(button, c);
        c.gridx++;
      }
    }
    frame.add(buttonsPanel, BorderLayout.EAST);

    // Info panel
    JPanel infoPanel = new JPanel(new GridBagLayout());
    c.gridx = 0;
    c.gridy = 0;
    c.gridheight = 1;
    c.weightx = 1;
    c.fill = GridBagConstraints.HORIZONTAL;
    JPanel infoColumn = new JPanel();
    infoColumn.setLayout(new BoxLayout(infoColumn, BoxLayout.X_AXIS));
    String combined = String.join(" | ", info);
    infoColumn.add(new JLabel(combined));
    infoPanel.add(infoColumn, c);
    frame.add(infoPanel, BorderLayout.CENTER);

    return frame;
  }
}
