package main.java.app.gui.managers;

import main.java.app.dao.SettingsDAO;
import javax.swing.*;
import java.awt.*;

public class WindowSettingsManager {

  private final JFrame frame;

  public WindowSettingsManager(JFrame frame) {
    this.frame = frame;
  }

  public void loadWindowSettings() {
    frame.setSize(new Dimension(
      loadIntSetting("window_width", 800),
      loadIntSetting("window_height", 600)
    ));
    frame.setLocation(new Point(
      loadIntSetting("window_x", 100),
      loadIntSetting("window_y", 100)
    ));
  }

  public void saveWindowSettings() {
    saveSetting("window_width", frame.getWidth());
    saveSetting("window_height", frame.getHeight());
    saveSetting("window_x", frame.getX());
    saveSetting("window_y", frame.getY());
  }

  private int loadIntSetting(String key, int defaultValue) {
    try {
      return Integer.parseInt(SettingsDAO.loadSetting(key, String.valueOf(defaultValue)));
    } catch (NumberFormatException e) {
      return defaultValue;
    }
  }

  private void saveSetting(String key, int value) {
    SettingsDAO.saveSetting(key, String.valueOf(value));
  }
}
