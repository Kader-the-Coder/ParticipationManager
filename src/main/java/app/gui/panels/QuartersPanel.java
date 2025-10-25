package main.java.app.gui.panels;

import main.java.app.dao.QuartersDAO;
import main.java.app.gui.components.RoundButton;
import main.java.app.gui.frames.MainFrame;
import main.java.app.gui.templates.PanelTemplate;
import main.java.app.models.Quarter;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

public class QuartersPanel extends BasePanel {

  private final JPanel quartersListPanel;
  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("EE (MM/dd/yyyy)");

  public QuartersPanel(MainFrame mainFrame) {
    super(mainFrame);

    // Header
    JLabel headerLabel = new JLabel("Manage Quarters");
    headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
    headerLabel.setHorizontalAlignment(SwingConstants.CENTER);
    setHeaderComponent(headerLabel);

    // Main scrollable list
    quartersListPanel = new JPanel();
    quartersListPanel.setLayout(new BoxLayout(quartersListPanel, BoxLayout.Y_AXIS));
    setBodyComponent(quartersListPanel);

    // Footer button: Add Quarter (right side)
    JButton addQuarterBtn = new JButton("➕ Add Quarter");
    addFooterButton(addQuarterBtn);

    addQuarterBtn.addActionListener(e -> addNewQuarter());

    loadQuarters();
  }

  private void loadQuarters() {
    quartersListPanel.removeAll();
    List<Quarter> quarters = QuartersDAO.getAllQuarters();
    quarters.sort(Comparator.comparing(Quarter::getStartDate));

    for (int i = 0; i < quarters.size(); i++) {
      quarters.get(i).setName("Quarter " + (i + 1));
    }

    Function<Quarter, String[]> info = this::getFieldInfo;
    Function<Quarter, List<JButton>> buttons = this::getJButtons;

    PanelTemplate<Quarter> builder = new PanelTemplate<>(quartersListPanel);
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

    editBtn.addActionListener(e -> editQuarter(q));
    deleteBtn.addActionListener(e -> deleteQuarter(q));

    return Arrays.asList(editBtn, deleteBtn);
  }

  private String[] getFieldInfo(Quarter q) {
    return new String[]{
      q.getName(),
      q.getStartDate().format(DATE_FORMATTER)
    };
  }

  private void addNewQuarter() {
    LocalDate startDate = LocalDate.now();
    List<Quarter> quarters = QuartersDAO.getAllQuarters();

    boolean duplicate;
    do {
      duplicate = false;
      for (Quarter q : quarters) {
        if (q.getStartDate().equals(startDate)) {
          int daysToAdd = (8 - startDate.getDayOfWeek().getValue()) % 7;
          if (daysToAdd == 0) daysToAdd = 7;
          startDate = startDate.plusDays(daysToAdd);
          duplicate = true;
          break;
        }
      }
    } while (duplicate);

    Quarter newQuarter = new Quarter("", startDate);
    QuartersDAO.addQuarter(newQuarter);
    loadQuarters();
  }

  private void editQuarter(Quarter quarter) {
    JPanel panel = new JPanel(new FlowLayout());
    JDateChooser datePicker = new JDateChooser();
    datePicker.setDate(java.sql.Date.valueOf(quarter.getStartDate()));
    datePicker.setDateFormatString("MM/dd/yyyy");
    JTextField editor = (JTextField) datePicker.getDateEditor().getUiComponent();
    editor.setHorizontalAlignment(SwingConstants.CENTER);
    editor.setEditable(false);
    datePicker.setPreferredSize(new Dimension(100, editor.getPreferredSize().height - 2));
    panel.add(new JLabel("Start Date:"));
    panel.add(datePicker);

    int result = JOptionPane.showConfirmDialog(this, panel, "Edit Quarter", JOptionPane.OK_CANCEL_OPTION);
    if (result != JOptionPane.OK_OPTION) return;

    LocalDate newDate = datePicker.getDate() != null
      ? datePicker.getDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate()
      : quarter.getStartDate();

    try {
      quarter.setStartDate(newDate);
      QuartersDAO.updateQuarter(quarter);

      List<Quarter> quarters = QuartersDAO.getAllQuarters();
      quarters.sort(Comparator.comparing(Quarter::getStartDate));
      for (int i = 0; i < quarters.size(); i++) {
        quarters.get(i).setName("Quarter " + (i + 1));
        QuartersDAO.updateQuarter(quarters.get(i));
      }

      loadQuarters();
    } catch (IllegalArgumentException ex) {
      JOptionPane.showMessageDialog(this, ex.getMessage());
    }
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
