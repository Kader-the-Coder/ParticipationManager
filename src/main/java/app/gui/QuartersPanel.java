package main.java.app.gui;

import main.java.app.dao.QuartersDAO;
import main.java.app.models.Quarter;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;
import java.util.Arrays;

public class QuartersPanel extends JPanel {

  private final JPanel quartersListPanel;

  public QuartersPanel(MainFrame mainFrame) {
    setLayout(new BorderLayout(20, 20));
    setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    // Header
    JLabel header = new JLabel("Manage Quarters");
    header.setFont(new Font("Segoe UI", Font.BOLD, 20));
    header.setHorizontalAlignment(SwingConstants.CENTER);
    add(header, BorderLayout.NORTH);

    // Scrollable quarters list
    quartersListPanel = new JPanel();
    quartersListPanel.setLayout(new BoxLayout(quartersListPanel, BoxLayout.Y_AXIS));
    JScrollPane scrollPane = new JScrollPane(quartersListPanel);
    add(scrollPane, BorderLayout.CENTER);

    // Bottom panel buttons
    JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    JButton addQuarterBtn = new JButton("➕ Add Quarter");
    JButton backBtn = new JButton("⬅ Back to Menu");
    bottomPanel.add(addQuarterBtn);
    bottomPanel.add(backBtn);
    add(bottomPanel, BorderLayout.SOUTH);

    addQuarterBtn.addActionListener(ignored -> addNewQuarter());
    backBtn.addActionListener(ignored -> mainFrame.showPanel("menu"));

    loadQuarters();
  }

  private void loadQuarters() {
    quartersListPanel.removeAll();
    List<Quarter> quarters = QuartersDAO.getAllQuarters();

    Function<Quarter, String[]> info = this::getFieldInfo;
    Function<Quarter, List<JButton>> buttons = this::getJButtons;

    FrameBuilder<Quarter> builder = new FrameBuilder<>(quartersListPanel);
    for (Quarter q : quarters) {
      JPanel frame = builder.buildFrame(info.apply(q), buttons.apply(q));
      frame.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
      quartersListPanel.add(frame);
    }

    quartersListPanel.revalidate();
    quartersListPanel.repaint();
  }

  private List<JButton> getJButtons(Quarter q) {
    RoundButton editBtn = new RoundButton("✎", Color.WHITE);
    RoundButton deleteBtn = new RoundButton("❌", Color.RED);

    editBtn.addActionListener(ignored -> editQuarter(q));
    deleteBtn.addActionListener(ignored -> deleteQuarter(q));

    return Arrays.asList(editBtn, deleteBtn);
  }

  private String[] getFieldInfo(Quarter q) {
    return new String[]{
      q.getName(),
      q.getStartDate().toString()
    };
  }

  private void addNewQuarter() {
    String name = JOptionPane.showInputDialog(this, "Enter Quarter Name:");
    if (name == null || name.trim().isEmpty()) return;

    LocalDate startDate = QuartersDAO.computeNextQuarterStartDate();
    Quarter newQuarter = new Quarter(name, startDate);
    QuartersDAO.addQuarter(newQuarter);

    loadQuarters();
  }

  private void editQuarter(Quarter quarter) {
    String newName = JOptionPane.showInputDialog(this, "Edit Quarter Name:", quarter.getName());
    if (newName == null || newName.trim().isEmpty()) return;

    LocalDate newStartDate = quarter.getStartDate();
    String dateInput = JOptionPane.showInputDialog(
      this,
      "Edit Start Date (YYYY-MM-DD):",
      newStartDate.toString()
    );
    if (dateInput == null || dateInput.trim().isEmpty()) return;

    try {
      newStartDate = LocalDate.parse(dateInput.trim());
    } catch (Exception ignored) {
      JOptionPane.showMessageDialog(this, "Invalid date format. Please use YYYY-MM-DD.");
      return;
    }

    quarter.setName(newName);
    quarter.setStartDate(newStartDate);
    QuartersDAO.updateQuarter(quarter);

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
      loadQuarters();
    }
  }
}
