package main.java.app.gui;

import main.java.app.dao.SettingsDAO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;

public class MainFrame extends JFrame {

  private final JPanel cardPanel;
  private final CardLayout cardLayout;

  private int selectedQuarterId;
  private int selectedWeekId;

  // Map to store dynamic panels by name
  private final Map<String, JPanel> panels = new HashMap<>();

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

    // Initialize default panels
    addPanel("menu", new MainMenuPanel(this));
    addPanel("students", new StudentManagerPanel(this));
    addPanel("quarters", new QuartersPanel(this));
    addPanel("daily", new DailyTrackingPanel(this));
    addPanel("scores", new ViewScoresQuarterPanel(this));
    // addPanel("settings", new SettingsPanel(this));

    setContentPane(cardPanel);
    showPanel("menu");

    addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        saveSettings();
      }
    });
  }

  /** Show a panel by name (legacy support) */
  public void showPanel(String name) {
    if (!panels.containsKey(name)) {
      throw new IllegalArgumentException("Panel not found: " + name);
    }
    cardLayout.show(cardPanel, name);
  }

  /** Show a panel directly by JPanel object */
  public void showPanel(JPanel panel) {
    if (!panels.containsValue(panel)) {
      // Assign a dynamic key for the panel
      String key = "dynamic_" + panels.size();
      panels.put(key, panel);
      cardPanel.add(panel, key);
    }
    cardLayout.show(cardPanel, getKeyForPanel(panel));
  }

  /** Register a panel with a name */
  public void addPanel(String name, JPanel panel) {
    panels.put(name, panel);
    cardPanel.add(panel, name);
  }

  private String getKeyForPanel(JPanel panel) {
    return panels.entrySet().stream()
      .filter(entry -> entry.getValue() == panel)
      .map(Map.Entry::getKey)
      .findFirst()
      .orElseThrow(() -> new IllegalArgumentException("Panel not registered"));
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
