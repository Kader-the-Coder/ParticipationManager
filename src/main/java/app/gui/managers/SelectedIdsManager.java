package main.java.app.gui.managers;

import main.java.app.dao.SettingsDAO;

public class SelectedIdsManager {

  private int selectedQuarterId;
  private int selectedWeekId;

  public SelectedIdsManager() {
    selectedQuarterId = loadIntSetting("selected_quarter", 1);
    selectedWeekId = loadIntSetting("selected_week", 1);
  }

  public int getSelectedQuarterId() {
    return selectedQuarterId;
  }

  public void setSelectedQuarterId(int selectedQuarterId) {
    this.selectedQuarterId = selectedQuarterId;
    saveSetting("selected_quarter", selectedQuarterId);
  }

  public int getSelectedWeekId() {
    return selectedWeekId;
  }

  public void setSelectedWeekId(int selectedWeekId) {
    this.selectedWeekId = selectedWeekId;
    saveSetting("selected_week", selectedWeekId);
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
