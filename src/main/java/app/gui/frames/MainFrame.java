package main.java.app.gui.frames;

import main.java.app.gui.managers.PanelManager;
import main.java.app.gui.managers.WindowSettingsManager;
import main.java.app.gui.managers.SelectedIdsManager;
import main.java.app.gui.panels.*;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;

public class MainFrame extends JFrame {

  private final WindowSettingsManager windowSettingsManager;
  private final PanelManager panelManager;
  private final SelectedIdsManager selectedIdsManager;

  public MainFrame() {
    setTitle("ParticipationManager");
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setAlwaysOnTop(true);
    setMinimumSize(new Dimension(300, 400));

    CardLayout cardLayout = new CardLayout();
    JPanel cardPanel = new JPanel(cardLayout);

    panelManager = new PanelManager(cardPanel, cardLayout);
    windowSettingsManager = new WindowSettingsManager(this);
    selectedIdsManager = new SelectedIdsManager();

    windowSettingsManager.loadWindowSettings();

    // ---------------------------------------------------------------------------------------------
    LinkedHashMap<String, JPanel> panels = new LinkedHashMap<>();

    panels.put("scores", new ViewScoresQuarterPanel(this));
    panels.put("daily", new DailyTrackingPanel(this));
    panels.put("quarters", new QuartersPanel(this));
    panels.put("students", new StudentManagerPanel(this));
    panels.put("settings", new SettingsPanel(this));

    // Add panels to manager
    panels.forEach(panelManager::addPanel);

    // Create main menu dynamically
    panelManager.addPanel("menu", new MainMenuPanel(this, panelManager));

    // ---------------------------------------------------------------------------------------------

    setContentPane(cardPanel);
    panelManager.showPanel("menu");

    addWindowListener(new java.awt.event.WindowAdapter() {
      @Override
      public void windowClosing(java.awt.event.WindowEvent e) {
        windowSettingsManager.saveWindowSettings();
      }
    });
  }

  public void showPanel(String name) {
    panelManager.showPanel(name);
  }

  public void showPanel(JPanel panel) {
    panelManager.showPanel(panel);
  }

  public int getSelectedQuarterId() {
    return selectedIdsManager.getSelectedQuarterId();
  }

  public void setSelectedQuarterId(int id) {
    selectedIdsManager.setSelectedQuarterId(id);
  }

  public int getSelectedWeekId() {
    return selectedIdsManager.getSelectedWeekId();
  }

  public void setSelectedWeekId(int id) {
    selectedIdsManager.setSelectedWeekId(id);
  }
}
