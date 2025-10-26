package main.java.app.gui.components;

import javax.swing.*;
import java.awt.*;

public class RoundButton extends JButton {
  private Color bgColor;
  private int diameter; // new field for dynamic size

  // Constructor with default size
  public RoundButton(String text, Color color) {
    this(text, color, 30); // default diameter = 30
  }

  // Constructor with custom size
  public RoundButton(String text, Color color, int size) {
    super(text);
    this.bgColor = color;
    this.diameter = size;
    setFocusPainted(false);
    setBorderPainted(false);
    setContentAreaFilled(false);
  }

  public void setBgColor(Color color) {
    this.bgColor = color;
    repaint();
  }

  public void setDiameter(int size) {
    this.diameter = size;
    revalidate();
    repaint();
  }

  @Override
  protected void paintComponent(Graphics g) {
    int d = Math.max(getWidth(), getHeight());
    Graphics2D g2 = (Graphics2D) g.create();
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    Color fillColor = bgColor != null ? bgColor : Color.WHITE;

    if (getModel().isRollover()) {
      fillColor = adjustBrightness(fillColor, 0.85f);
    }

    if (getModel().isPressed()) {
      fillColor = adjustBrightness(fillColor, 0.7f);
    }

    g2.setColor(fillColor);
    g2.fillOval(0, 0, d, d);

    g2.setColor(Color.GRAY);
    g2.drawOval(0, 0, d - 1, d - 1);

    FontMetrics fm = g2.getFontMetrics();
    int textWidth = fm.stringWidth(getText());
    int textHeight = fm.getAscent();
    int x = (d - textWidth) / 2;
    int y = (d + textHeight) / 2 - 2;
    g2.setColor(Color.BLACK);
    g2.drawString(getText(), x, y);

    g2.dispose();
  }

  private Color adjustBrightness(Color color, float factor) {
    int r = Math.min(255, Math.max(0, (int) (color.getRed() * factor)));
    int g = Math.min(255, Math.max(0, (int) (color.getGreen() * factor)));
    int b = Math.min(255, Math.max(0, (int) (color.getBlue() * factor)));
    return new Color(r, g, b);
  }

  @Override
  public Dimension getPreferredSize() {
    return new Dimension(diameter, diameter);
  }
}
