package main.java.app.gui;

import javax.swing.*;
import java.awt.*;

public class RoundButton extends JButton {
  public RoundButton(String text) {
    super(text);
    setFocusPainted(false);
    setContentAreaFilled(false);
    setBorderPainted(false);
    setOpaque(false);
  }

  @Override
  protected void paintComponent(Graphics g) {
    int diameter = Math.max(getWidth(), getHeight());
    Graphics2D g2 = (Graphics2D) g.create();
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    // Draw circular background
    g2.setColor(getModel().isPressed() ? Color.LIGHT_GRAY : Color.WHITE);
    g2.fillOval(0, 0, diameter, diameter);

    // Draw border
    g2.setColor(Color.GRAY);
    g2.drawOval(0, 0, diameter - 1, diameter - 1);

    // Draw text manually centered
    String text = getText();
    FontMetrics fm = g2.getFontMetrics();
    int textWidth = fm.stringWidth(text);
    int textHeight = fm.getAscent();
    int x = (diameter - textWidth) / 2;
    int y = (diameter + textHeight) / 2 - 2; // slight adjustment
    g2.setColor(getForeground());
    g2.drawString(text, x, y);

    g2.dispose();
  }

  @Override
  public Dimension getPreferredSize() {
    Dimension d = super.getPreferredSize();
    int s = Math.max(d.width, d.height);
    return new Dimension(30, 30); // make it square
  }
}
