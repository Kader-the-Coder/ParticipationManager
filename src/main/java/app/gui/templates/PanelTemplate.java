package main.java.app.gui.templates;

import main.java.app.gui.components.RoundButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.List;

public class PanelTemplate<T> {

  public PanelTemplate(JPanel container) {}

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

    final boolean[] isVertical = {false};
    final int thresholdWidth = 300;

    frame.addComponentListener(new ComponentAdapter() {
      @Override
      public void componentResized(ComponentEvent e) {
        int frameWidth = frame.getWidth();

        // Dynamically calculate the button width for normal layout
        int buttonsWidth = calculateButtonsWidth(buttonsPanel);

        // In horizontal layout, truncate label text based on button width
        // In vertical layout, truncate label text based on full frame width
        int minWidth = isVertical[0] ? frameWidth : buttonsWidth;

        truncateLabelText(infoLabel, fullText, frameWidth - minWidth);

        if (frameWidth < thresholdWidth && !isVertical[0]) {
          switchToVertical(contentPanel, infoLabel, infoPanel, buttonsPanel, buttons, fullText, frameWidth);
          isVertical[0] = true;
        } else if (frameWidth >= thresholdWidth && isVertical[0]) {
          switchToHorizontal(contentPanel, infoLabel, infoPanel, buttonsPanel, buttons, fullText);
          isVertical[0] = false;
        }
      }
    });

    return frame;
  }

  private int calculateButtonsWidth(JPanel buttonsPanel) {
    int totalWidth = 0;
    for (Component c : buttonsPanel.getComponents()) {
      Dimension pref = c.getPreferredSize();
      totalWidth += pref.width;
    }

    // Add spacing between buttons based on FlowLayout gaps
    LayoutManager layout = buttonsPanel.getLayout();
    if (layout instanceof FlowLayout flow) {
      int gap = flow.getHgap();
      int count = buttonsPanel.getComponentCount();
      if (count > 1) totalWidth += gap * (count - 1);
    }

    // Add a small padding margin
    totalWidth += 40;
    return totalWidth;
  }

  private void switchToVertical(JPanel contentPanel, JLabel infoLabel, JPanel infoPanel, JPanel buttonsPanel, List<JButton> buttons, String fullText, int frameWidth) {
    contentPanel.removeAll();
    contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

    infoLabel.setHorizontalAlignment(SwingConstants.CENTER);
    infoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
    buttonsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
    ((FlowLayout) buttonsPanel.getLayout()).setAlignment(FlowLayout.CENTER);

    // Now truncate using full frame width instead of button width
    truncateLabelText(infoLabel, fullText, frameWidth - 120);
    resizeButtons(buttons, 22);

    contentPanel.add(infoPanel);
    contentPanel.add(Box.createVerticalStrut(4));
    contentPanel.add(buttonsPanel);

    contentPanel.revalidate();
    contentPanel.repaint();
  }

  private void switchToHorizontal(JPanel contentPanel, JLabel infoLabel, JPanel infoPanel, JPanel buttonsPanel, List<JButton> buttons, String fullText) {
    contentPanel.removeAll();
    contentPanel.setLayout(new BorderLayout());

    infoLabel.setHorizontalAlignment(SwingConstants.LEFT);
    ((FlowLayout) buttonsPanel.getLayout()).setAlignment(FlowLayout.RIGHT);

    infoLabel.setText(fullText);
    resizeButtons(buttons, 30);

    contentPanel.add(infoPanel, BorderLayout.CENTER);
    contentPanel.add(buttonsPanel, BorderLayout.EAST);

    contentPanel.revalidate();
    contentPanel.repaint();
  }

  private void truncateLabelText(JLabel label, String fullText, int maxWidth) {
    if (maxWidth <= 0) return;

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

  private void resizeButtons(List<JButton> buttons, int size) {
    for (JButton b : buttons) {
      if (b instanceof RoundButton roundButton) {
        roundButton.setDiameter(size);
      }
    }
  }
}
