package main.java.app.gui.panels;

import main.java.app.gui.frames.MainFrame;

import javax.swing.*;
import java.awt.*;

public abstract class BasePanel extends JPanel {

  protected final JPanel headerPanel;
  protected final JScrollPane bodyScrollPane;
  protected final JPanel footerPanel;
  protected final JButton backButton;
  protected final JPanel additionalButtonsPanel;

  protected final MainFrame mainFrame;

  public BasePanel(MainFrame mainFrame  ) {
    this.mainFrame = mainFrame;

    setLayout(new BorderLayout(10, 10));
    setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    // Header
    headerPanel = new JPanel(new BorderLayout());
    add(headerPanel, BorderLayout.NORTH);

    // Body (scrollable)
    bodyScrollPane = new JScrollPane();
    add(bodyScrollPane, BorderLayout.CENTER);

    // Footer with BorderLayout
    footerPanel = new JPanel(new BorderLayout());
    add(footerPanel, BorderLayout.SOUTH);

    // Back button on the left
    backButton = new JButton("Back");
    backButton.addActionListener(e -> mainFrame.showPanel("menu"));
    footerPanel.add(backButton, BorderLayout.WEST);

    // Additional buttons panel on the right
    additionalButtonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));

    footerPanel.add(additionalButtonsPanel, BorderLayout.EAST);
  }

  // Helper to set header content
  protected void setHeaderComponent(JComponent component) {
    headerPanel.removeAll();
    headerPanel.add(component, BorderLayout.CENTER);
    headerPanel.revalidate();
    headerPanel.repaint();
  }

  // Helper to set body content
  protected void setBodyComponent(JComponent component) {
    bodyScrollPane.setViewportView(component);
  }

  // Helper to add additional buttons to the right
  protected void addFooterButton(JButton button) {
    additionalButtonsPanel.add(button);
    additionalButtonsPanel.revalidate();
    additionalButtonsPanel.repaint();
  }

  /**
   * Refresh the panel. By default does nothing; override in subclasses.
   */
  public void refresh() {
    // Subclasses can override to reload data when panel is displayed
  }
}
