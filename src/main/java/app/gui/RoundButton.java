package main.java.app.gui;

import javax.swing.*;
import java.awt.*;

public class RoundButton extends JButton {
  private Color bgColor;

  public RoundButton(String text, Color color) {
    super(text);
    this.bgColor = color;
    setFocusPainted(false);
    setBorderPainted(false);
    setContentAreaFilled(false);
  }

  public void setBgColor(Color color) {
    this.bgColor = color;
    repaint();
  }

  @Override
  protected void paintComponent(Graphics g) {
    int diameter = Math.max(getWidth(), getHeight());
    Graphics2D g2 = (Graphics2D) g.create();
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    Color fillColor = bgColor != null ? bgColor : Color.WHITE;

    if (getModel().isRollover()) {
      fillColor = adjustBrightness(fillColor, 0.85f); // darken 15% on hover
    }

    if (getModel().isPressed()) {
      fillColor = adjustBrightness(fillColor, 0.7f); // darken 30% on click
    }

    g2.setColor(fillColor);
    g2.fillOval(0, 0, diameter, diameter);

    // Draw border
    g2.setColor(Color.GRAY);
    g2.drawOval(0, 0, diameter - 1, diameter - 1);

    // Draw text manually centered
    FontMetrics fm = g2.getFontMetrics();
    int textWidth = fm.stringWidth(getText());
    int textHeight = fm.getAscent();
    int x = (diameter - textWidth) / 2;
    int y = (diameter + textHeight) / 2 - 2;
    g2.setColor(Color.BLACK);
    g2.drawString(getText(), x, y);

    g2.dispose();
  }

  // Utility to darken/lighten a color by a factor
  private Color adjustBrightness(Color color, float factor) {
    int r = Math.min(255, Math.max(0, (int) (color.getRed() * factor)));
    int g = Math.min(255, Math.max(0, (int) (color.getGreen() * factor)));
    int b = Math.min(255, Math.max(0, (int) (color.getBlue() * factor)));
    return new Color(r, g, b);
  }


  @Override
  public Dimension getPreferredSize() {
    int size = 30;
    return new Dimension(size, size);
  }
}
