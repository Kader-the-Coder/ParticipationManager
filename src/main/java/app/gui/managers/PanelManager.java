package main.java.app.gui.managers;

import javax.swing.*;
import java.awt.*;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

public class PanelManager {

  private final JPanel cardPanel;
  private final CardLayout cardLayout;
  private final Map<String, JPanel> panels = new LinkedHashMap<>(); // preserves insertion order

  public PanelManager(JPanel cardPanel, CardLayout cardLayout) {
    this.cardPanel = cardPanel;
    this.cardLayout = cardLayout;
  }

  public void addPanel(String name, JPanel panel) {
    panels.put(name, panel);
    cardPanel.add(panel, name);
  }

  public void registerPanels(Map<String, Function<JPanel, JPanel>> defaultPanels) {
    defaultPanels.forEach((key, constructor) -> addPanel(key, constructor.apply(cardPanel)));
  }

  public void showPanel(String name) {
    JPanel panel = panels.get(name);
    if (panel == null) throw new IllegalArgumentException("Panel not found: " + name);
    cardLayout.show(cardPanel, name);
  }

  public void showPanel(JPanel panel) {
    if (!panels.containsValue(panel)) {
      String key = "dynamic_" + panels.size();
      panels.put(key, panel);
      cardPanel.add(panel, key);
    }
    cardLayout.show(cardPanel, getKeyForPanel(panel));
  }

  private String getKeyForPanel(JPanel panel) {
    return panels.entrySet().stream()
      .filter(entry -> entry.getValue() == panel)
      .map(Map.Entry::getKey)
      .findFirst()
      .orElseThrow(() -> new IllegalArgumentException("Panel not registered"));
  }

  public Map<String, JPanel> getRegisteredPanels() {
    return Collections.unmodifiableMap(panels);
  }
}
