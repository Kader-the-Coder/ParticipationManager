package main.java.app.gui;

import main.java.app.dao.SettingsDAO;

import javax.swing.JFrame;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainFrame extends JFrame {

  public MainFrame() {
    setTitle("ParticipationManager");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    // Load last size and position
    int width = Integer.parseInt(SettingsDAO.loadSetting("window_width", "800"));
    int height = Integer.parseInt(SettingsDAO.loadSetting("window_height", "600"));
    int x = Integer.parseInt(SettingsDAO.loadSetting("window_x", "100"));
    int y = Integer.parseInt(SettingsDAO.loadSetting("window_y", "100"));

    setSize(new Dimension(width, height));
    setLocation(new Point(x, y));

    // Add a listener to save settings when window closes
    addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        saveSettings();
      }
    });
  }

  private void saveSettings() {
    SettingsDAO.saveSetting("window_width", String.valueOf(getWidth()));
    SettingsDAO.saveSetting("window_height", String.valueOf(getHeight()));
    SettingsDAO.saveSetting("window_x", String.valueOf(getX()));
    SettingsDAO.saveSetting("window_y", String.valueOf(getY()));
  }
}
