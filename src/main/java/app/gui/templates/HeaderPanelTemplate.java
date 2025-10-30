package main.java.app.gui.templates;

import main.java.app.gui.components.DatePicker;
import main.java.app.gui.components.GradeSubjectFilter;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;

public class HeaderPanelTemplate extends JPanel {

  private final DatePicker datePicker;

  public HeaderPanelTemplate(String title, int height, GradeSubjectFilter rightFilters, DateChangeListener listener) {
    setLayout(new BorderLayout());
    setPreferredSize(new Dimension(0, height * 2)); // double height for two rows

    // Row 1: Title
    JLabel titleLabel = new JLabel(title);
    titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
    titleLabel.setHorizontalAlignment(SwingConstants.CENTER);


    JPanel titlePanel = new JPanel(new BorderLayout());
    titlePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
    titlePanel.add(titleLabel, BorderLayout.CENTER);
    add(titlePanel, BorderLayout.NORTH);

    // Row 2: Date picker + filters
    JPanel secondRow = new JPanel(new BorderLayout());
    secondRow.setPreferredSize(new Dimension(0, height));

    // Date picker on the left
    if (listener != null) {
      datePicker = createDatePicker(height * 3, listener);
      secondRow.add(datePicker, BorderLayout.WEST);
    } else {
      datePicker = null;
    }

    // Filters on the right
    if (rightFilters != null) {
      secondRow.add(rightFilters, BorderLayout.EAST);
    }

    add(secondRow, BorderLayout.CENTER);
  }

  public LocalDate getSelectedDate() {
    if (datePicker == null) return null;
    java.util.Date date = (java.util.Date) datePicker.getDate();
    if (date != null) return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    return null;
  }

  public void setSelectedDate(LocalDate date) {
    if (datePicker != null && date != null) {
      datePicker.setDate(java.sql.Date.valueOf(date));
    }
  }

  // Listener interface for date changes
  public interface DateChangeListener {
    void dateChanged(LocalDate newDate);
  }

  /** Helper method to create a date picker with listener */
  private DatePicker createDatePicker(int height, DateChangeListener listener) {
    DatePicker picker = new DatePicker();
    picker.setPreferredSize(new Dimension(120, height));
    picker.getDateChooser().getDateEditor().addPropertyChangeListener(e -> {
      if ("date".equals(e.getPropertyName())) {
        java.util.Date date = (java.util.Date) e.getNewValue();
        if (date != null) {
          LocalDate selected = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
          listener.dateChanged(selected);
        }
      }
    });
    return picker;
  }
}
