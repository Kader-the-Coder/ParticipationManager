package main.java.app.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.List;

public class FrameBuilder<T> {

  public FrameBuilder(JPanel container) {}

  public JPanel buildFrame(String[] info, List<JButton> buttons) {
    JPanel frame = new JPanel(new BorderLayout());
    frame.setBorder(BorderFactory.createCompoundBorder(
      BorderFactory.createLineBorder(Color.GRAY),
      BorderFactory.createEmptyBorder(5, 5, 5, 5)
    ));

    String fullText = String.join(" | ", info);
    JLabel infoLabel = new JLabel(fullText);
    infoLabel.setHorizontalAlignment(SwingConstants.LEFT);

    JPanel infoPanel = new JPanel(new BorderLayout());
    infoPanel.add(infoLabel, BorderLayout.CENTER);

    JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
    for (JButton button : buttons) buttonsPanel.add(button);

    JPanel contentPanel = new JPanel(new BorderLayout());
    contentPanel.add(infoPanel, BorderLayout.CENTER);
    contentPanel.add(buttonsPanel, BorderLayout.EAST);

    frame.add(contentPanel, BorderLayout.CENTER);

    // Track current layout mode: horizontal by default
    final boolean[] isVertical = {false};

    frame.addComponentListener(new ComponentAdapter() {
      @Override
      public void componentResized(ComponentEvent e) {
        int textWidth = infoLabel.getPreferredSize().width;
        int buttonsWidth = buttonsPanel.getPreferredSize().width;
        int total = textWidth + buttonsWidth + 80;

        if (frame.getWidth() < total) {
          if (!isVertical[0]) { // only switch if not already vertical
            // Switch to vertical centered layout
            contentPanel.removeAll();
            contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

            infoLabel.setHorizontalAlignment(SwingConstants.CENTER);
            infoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
            buttonsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
            ((FlowLayout) buttonsPanel.getLayout()).setAlignment(FlowLayout.CENTER);

            truncateLabelText(infoLabel, fullText, frame.getWidth() - 60);

            // Resize buttons smaller for vertical layout
            resizeButtons(buttons, 22);

            contentPanel.add(infoPanel);
            contentPanel.add(Box.createVerticalStrut(4));
            contentPanel.add(buttonsPanel);

            isVertical[0] = true;
            contentPanel.revalidate();
            contentPanel.repaint();
          }
        } else {
          if (isVertical[0]) { // only switch if currently vertical
            // Switch back to horizontal layout
            contentPanel.removeAll();
            contentPanel.setLayout(new BorderLayout());

            infoLabel.setHorizontalAlignment(SwingConstants.LEFT);
            ((FlowLayout) buttonsPanel.getLayout()).setAlignment(FlowLayout.RIGHT);

            infoLabel.setText(fullText); // restore full text

            // Restore buttons to default size
            resizeButtons(buttons, 30);

            contentPanel.add(infoPanel, BorderLayout.CENTER);
            contentPanel.add(buttonsPanel, BorderLayout.EAST);

            isVertical[0] = false;
            contentPanel.revalidate();
            contentPanel.repaint();
          }
        }
      }
    });

    return frame;
  }

  /** Truncate text with ellipsis based on available width */
  private void truncateLabelText(JLabel label, String fullText, int maxWidth) {
    FontMetrics fm = label.getFontMetrics(label.getFont());
    if (fm.stringWidth(fullText) <= maxWidth) {
      label.setText(fullText);
      return;
    }

    String ellipsis = "...";
    int ellipsisWidth = fm.stringWidth(ellipsis);
    StringBuilder sb = new StringBuilder();
    for (char c : fullText.toCharArray()) {
      if (fm.stringWidth(sb.toString()) + ellipsisWidth >= maxWidth)
        break;
      sb.append(c);
    }
    label.setText(sb.toString().trim() + ellipsis);
  }

  /** Resize all RoundButtons to the given diameter */
  private void resizeButtons(List<JButton> buttons, int size) {
    for (JButton b : buttons) {
      if (b instanceof RoundButton roundButton) {
        roundButton.setDiameter(size);
      }
    }
  }
}
