package main.java.app.gui;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.function.Function;
import java.util.function.Consumer;

/**
 * Generic builder for creating listable frames for any data model.
 */
public class ListableFrameBuilder<T> {

  private final JPanel containerPanel;
  private final Function<T, String[]> displayMapper;  // Maps model to array of display strings
  private final List<Consumer<T>> buttonActions;      // Actions for buttons

  public ListableFrameBuilder(JPanel containerPanel, Function<T, String[]> displayMapper, List<Consumer<T>> buttonActions) {
    this.containerPanel = containerPanel;
    this.displayMapper = displayMapper;
    this.buttonActions = buttonActions;
  }

  public JPanel buildFrame(T model, List<JButton> buttons) {
    JPanel frame = new JPanel(new BorderLayout());
    frame.setBorder(BorderFactory.createCompoundBorder(
      BorderFactory.createLineBorder(Color.GRAY),
      BorderFactory.createEmptyBorder(5, 5, 5, 5)
    ));

    // Buttons panel
    JPanel buttonsPanel = new JPanel(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();
    c.gridx = 0;
    c.gridy = 0;
    c.anchor = GridBagConstraints.CENTER;
    if (!buttons.isEmpty()) {
      for (JButton button : buttons) {
        buttonsPanel.add(button, c);
        c.gridx++;
      }
    }
    frame.add(buttonsPanel, BorderLayout.EAST);

    // Info panel
    JPanel infoPanel = new JPanel(new GridBagLayout());
    c.gridx = 0;
    c.gridy = 0;
    c.gridheight = 1;
    c.weightx = 1;
    c.fill = GridBagConstraints.HORIZONTAL;

    String[] infoStrings = displayMapper.apply(model);
    JPanel infoColumn = new JPanel();
    infoColumn.setLayout(new BoxLayout(infoColumn, BoxLayout.X_AXIS));
    String combined = String.join(" | ", infoStrings);
    infoColumn.add(new JLabel(combined));

    infoPanel.add(infoColumn, c);
    frame.add(infoPanel, BorderLayout.CENTER);

    // Assign actions to buttons
    for (int i = 0; i < buttons.size(); i++) {
      JButton button = buttons.get(i);
      Consumer<T> action = buttonActions.get(i);
      button.addActionListener(e -> action.accept(model));
    }

    return frame;
  }
}
