package main.java.app.gui;

import main.java.app.dao.SettingsDAO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainFrame extends JFrame {

  private final JPanel cardPanel;
  private final CardLayout cardLayout;

  private int selectedQuarterId;
  private int selectedWeekId;

  public MainFrame() {
    setTitle("ParticipationManager");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setAlwaysOnTop(true);
    setMinimumSize(new Dimension(300, 400));

    int width = Integer.parseInt(SettingsDAO.loadSetting("window_width", "800"));
    int height = Integer.parseInt(SettingsDAO.loadSetting("window_height", "600"));
    int x = Integer.parseInt(SettingsDAO.loadSetting("window_x", "100"));
    int y = Integer.parseInt(SettingsDAO.loadSetting("window_y", "100"));

    setSize(new Dimension(width, height));
    setLocation(new Point(x, y));

    selectedQuarterId = Integer.parseInt(SettingsDAO.loadSetting("selected_quarter", "1"));
    selectedWeekId = Integer.parseInt(SettingsDAO.loadSetting("selected_week", "1"));

    cardLayout = new CardLayout();
    cardPanel = new JPanel(cardLayout);

    MainMenuPanel menuPanel = new MainMenuPanel(this);
    StudentManagerPanel studentPanel = new StudentManagerPanel(this);
    QuartersPanel quartersPanel = new QuartersPanel(this);
    DailyTrackingPanel dailyPanel = new DailyTrackingPanel(this);
    // SettingsPanel settingsPanel = new SettingsPanel(this);

    cardPanel.add(menuPanel, "menu");
    cardPanel.add(dailyPanel, "daily");
    cardPanel.add(studentPanel, "students");
    cardPanel.add(quartersPanel, "quarters");
    // cardPanel.add(settingsPanel, "settings");

    setContentPane(cardPanel);
    showPanel("menu");

    addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        saveSettings();
      }
    });
  }

  public void showPanel(String name) {
    cardLayout.show(cardPanel, name);
  }

  public int getSelectedQuarterId() {
    return selectedQuarterId;
  }

  public void setSelectedQuarterId(int selectedQuarterId) {
    this.selectedQuarterId = selectedQuarterId;
    SettingsDAO.saveSetting("selected_quarter", String.valueOf(selectedQuarterId));
  }

  public int getSelectedWeekId() {
    return selectedWeekId;
  }

  public void setSelectedWeekId(int selectedWeekId) {
    this.selectedWeekId = selectedWeekId;
    SettingsDAO.saveSetting("selected_week", String.valueOf(selectedWeekId));
  }

  private void saveSettings() {
    SettingsDAO.saveSetting("window_width", String.valueOf(getWidth()));
    SettingsDAO.saveSetting("window_height", String.valueOf(getHeight()));
    SettingsDAO.saveSetting("window_x", String.valueOf(getX()));
    SettingsDAO.saveSetting("window_y", String.valueOf(getY()));
  }
}
