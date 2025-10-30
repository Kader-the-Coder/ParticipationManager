package main.java.app.gui.components;

import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import java.awt.*;
import java.util.Date;

/**
 * A reusable date picker component that wraps JDateChooser with
 * a consistent layout, size, and formatting.
 */
public class DatePicker extends JPanel {

  private final JDateChooser dateChooser;

  public DatePicker() {
    this(new Date());
  }

  public DatePicker(Date initialDate) {
    super(new FlowLayout(FlowLayout.LEFT, 2, 0));

    // Create and configure the JDateChooser
    dateChooser = new JDateChooser();
    dateChooser.setDateFormatString("EE (MM/dd)");
    dateChooser.setDate(initialDate);

    JTextField editor = (JTextField) dateChooser.getDateEditor().getUiComponent();
    int uniformHeight = editor.getPreferredSize().height - 2;
    dateChooser.setPreferredSize(new Dimension(100, uniformHeight));
    editor.setHorizontalAlignment(SwingConstants.CENTER);
    editor.setEditable(false);

    // Add the date chooser to this panel
    this.add(dateChooser);
  }

  /**
   * Get the currently selected date.
   */
  public Date getDate() {
    return dateChooser.getDate();
  }

  /**
   * Set the currently selected date.
   */
  public void setDate(Date date) {
    dateChooser.setDate(date);
  }

  /**
   * Access the internal JDateChooser for adding listeners, etc.
   */
  public JDateChooser getDateChooser() {
    return dateChooser;
  }
}
