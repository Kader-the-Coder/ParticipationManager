package main.java.app.gui;

import main.java.app.dao.QuartersDAO;
import main.java.app.dao.StudentDAO;
import main.java.app.models.Quarter;
import main.java.app.models.Student;

import javax.swing.*;
import java.awt.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Panel for managing quarters only.
 */
public class QuartersPanel extends JPanel {

  private final MainFrame mainFrame;
  private final JPanel quartersListPanel;
  private final int quarterPanelHeight = 50;

  public QuartersPanel(MainFrame mainFrame) {
    this.mainFrame = mainFrame;
    setLayout(new BorderLayout(20, 20));
    setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    // Header
    JLabel header = new JLabel("Manage Quarters");
    header.setFont(new Font("Segoe UI", Font.BOLD, 20));
    header.setHorizontalAlignment(SwingConstants.CENTER);
    add(header, BorderLayout.NORTH);

    // Scrollable list of quarters
    quartersListPanel = new JPanel();
    quartersListPanel.setLayout(new BoxLayout(quartersListPanel, BoxLayout.Y_AXIS));
    JScrollPane scrollPane = new JScrollPane(quartersListPanel);
    add(scrollPane, BorderLayout.CENTER);

    // Bottom buttons
    JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    JButton addQuarterBtn = new JButton("➕ Add Quarter");
    JButton backBtn = new JButton("⬅ Back to Menu");
    bottomPanel.add(addQuarterBtn);
    bottomPanel.add(backBtn);
    add(bottomPanel, BorderLayout.SOUTH);

    // Load existing quarters
    loadQuarters();

    // Actions
    addQuarterBtn.addActionListener(e -> addNewQuarter());
    backBtn.addActionListener(e -> mainFrame.showPanel("menu"));
  }

//  private void loadQuarters() {
//    quartersListPanel.removeAll();
//    List<Quarter> quarters = QuartersDAO.getAllQuarters();
//    for (Quarter q : quarters) {
//      JLabel label = new JLabel(q.getName() + " (Start: " + q.getStartDate() + ")");
//      label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
//      quartersListPanel.add(label);
//    }
//    quartersListPanel.revalidate();
//    quartersListPanel.repaint();
//  }

  private void loadQuarters() {
    quartersListPanel.removeAll();
    List<Quarter> quarters = QuartersDAO.getAllQuarters();

    Function<Quarter, String[]> info = q -> new String[]{
      q.getName(),
      q.getStartDate().toString()
    };

    List<Consumer<Quarter>> actions = Arrays.asList(this::editQuarter, this::deleteQuarter);

    for (Quarter q : quarters) {
      JButton editBtn = new JButton("✎");
      JButton deleteBtn = new JButton("❌");
      List<JButton> buttons = Arrays.asList(editBtn, deleteBtn);

      ListableFrameBuilder<Quarter> builder = new ListableFrameBuilder<>(quartersListPanel, info, actions);
      JPanel studentFrame = builder.buildFrame(q, buttons);
      studentFrame.setMaximumSize(new Dimension(Integer.MAX_VALUE, quarterPanelHeight));
      quartersListPanel.add(studentFrame);
    }

    quartersListPanel.revalidate();
    quartersListPanel.repaint();
  }

  private void addNewQuarter() {
    String name = JOptionPane.showInputDialog(this, "Enter Quarter Name:");
    if (name == null || name.trim().isEmpty()) return;

    // Compute start date: next Monday after last quarter
    LocalDate startDate = QuartersDAO.computeNextQuarterStartDate();
    Quarter newQuarter = new Quarter(name, startDate);
    QuartersDAO.addQuarter(newQuarter);

    loadQuarters();
  }

  private void editQuarter(Quarter quarter) {
    // Edit name
    String newName = JOptionPane.showInputDialog(this, "Edit Quarter Name:", quarter.getName());
    if (newName == null || newName.trim().isEmpty()) return;

    // Edit start date
    LocalDate newStartDate = quarter.getStartDate();
    String dateInput = JOptionPane.showInputDialog(
      this,
      "Edit Start Date (YYYY-MM-DD):",
      newStartDate.toString()
    );
    if (dateInput == null || dateInput.trim().isEmpty()) return;

    try {
      newStartDate = LocalDate.parse(dateInput.trim());
    } catch (Exception e) {
      JOptionPane.showMessageDialog(this, "Invalid date format. Please use YYYY-MM-DD.");
      return;
    }

    // Update Quarter object
    quarter.setName(newName);
    quarter.setStartDate(newStartDate);

    // Update in DB
    QuartersDAO.updateQuarter(quarter);

    // Refresh the list
    loadQuarters();
  }

  private void deleteQuarter(Quarter quarter) {
    int confirm = JOptionPane.showConfirmDialog(
      this,
      "Are you sure you want to delete the quarter \"" + quarter.getName() + "\"?",
      "Confirm Delete",
      JOptionPane.YES_NO_OPTION
    );

    if (confirm == JOptionPane.YES_OPTION) {
      QuartersDAO.deleteQuarter(quarter.getId());
      loadQuarters(); // Refresh the list to reflect deletion
    }
  }

}
