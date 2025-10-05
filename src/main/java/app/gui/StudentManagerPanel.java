package main.java.app.gui;

import main.java.app.dao.StudentDAO;
import main.java.app.dao.SubjectDAO;
import main.java.app.dao.GradeDAO;
import main.java.app.models.Student;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class StudentManagerPanel extends JPanel {

  JPanel mainPanel;
  JScrollPane scrollPane;
  int studentPanelHeight = 50;

  public StudentManagerPanel() {
    mainPanel = new JPanel();
    mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

    // Scrollable area for listing students
    scrollPane = new JScrollPane(mainPanel);
    this.setLayout(new BorderLayout());
    this.add(scrollPane, BorderLayout.CENTER);

    // Button to add more students
    JButton addButton = new JButton("➕");
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    buttonPanel.add(addButton);
    this.add(buttonPanel, BorderLayout.SOUTH);

    // Load students and add their frames
    List<Student> students = StudentDAO.getAllStudents();
    for (Student s : students) {
      JPanel sFrame = createStudentFrame(s);
      sFrame.setMaximumSize(new Dimension(Integer.MAX_VALUE, studentPanelHeight));
      mainPanel.add(sFrame);
    }

    mainPanel.revalidate();
    mainPanel.repaint();

    // Add Button action
    addButton.addActionListener(e -> {
      // Name
      String name = JOptionPane.showInputDialog(mainPanel, "Enter Student Name:");
      if (name == null || name.trim().isEmpty()) return;

      // Grade selection
      List<String> grades = GradeDAO.getAllGradeNames();
      grades.sort(Comparator.comparingInt(Integer::parseInt));
      String[] gradesArray = grades.toArray(new String[0]);
      String gradeName = (String) JOptionPane.showInputDialog(
        mainPanel,
        "Select Grade:",
        "New Student",
        JOptionPane.PLAIN_MESSAGE,
        null,
        gradesArray,
        gradesArray[0]
      );
      if (gradeName == null) return;
      int gradeId = GradeDAO.getGradeIdByName(gradeName);

      // Subjects multi-select
      List<String> allSubjects = SubjectDAO.getAllSubjects();
      JList<String> subjectList = new JList<>(allSubjects.toArray(new String[0]));
      subjectList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

      int result = JOptionPane.showConfirmDialog(
        mainPanel,
        new JScrollPane(subjectList),
        "Select Subjects (hold 'Ctrl' to select multiple)",
        JOptionPane.OK_CANCEL_OPTION,
        JOptionPane.PLAIN_MESSAGE
      );
      if (result != JOptionPane.OK_OPTION) return;

      List<String> selectedSubjects = subjectList.getSelectedValuesList();
      List<Integer> subjectIds = new ArrayList<>();
      for (String subjName : selectedSubjects) {
        int id = SubjectDAO.getSubjectId(subjName);
        subjectIds.add(id);
      }

      Student newStudent = new Student(name, gradeId, subjectIds);
      StudentDAO.addStudent(newStudent);

      // Add new frame to GUI
      JPanel newStudentFrame = createStudentFrame(newStudent);
      newStudentFrame.setMaximumSize(new Dimension(Integer.MAX_VALUE, studentPanelHeight));
      mainPanel.add(newStudentFrame);
      mainPanel.revalidate();
      mainPanel.repaint();
    });
  }

  private JPanel createStudentFrame(Student student) {
    JPanel frame = new JPanel(new BorderLayout());
    frame.setBorder(BorderFactory.createCompoundBorder(
      BorderFactory.createLineBorder(Color.GRAY),
      BorderFactory.createEmptyBorder(5, 5, 5, 5)
    ));

    // Buttons panel
    JButton editButton = new RoundButton("✎");
    JButton deleteButton = new RoundButton("❌");
    JPanel buttonsPanel = new JPanel(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();
    c.gridx = 0;
    c.gridy = 0;
    c.anchor = GridBagConstraints.CENTER;
    buttonsPanel.add(editButton, c);
    c.gridx = 1;
    buttonsPanel.add(deleteButton, c);
    frame.add(buttonsPanel, BorderLayout.EAST);

    // Info panel
    JPanel infoPanel = new JPanel(new GridBagLayout());
    c.insets = new Insets(2, 2, 2, 2);

    JPanel leftColumn = new JPanel();
    leftColumn.setLayout(new BoxLayout(leftColumn, BoxLayout.Y_AXIS));
    JLabel nameLabel = new JLabel(student.getName());
    JLabel gradeLabel = new JLabel("Grade " + student.getGradeName());
    leftColumn.add(nameLabel);
    leftColumn.add(gradeLabel);
    leftColumn.setPreferredSize(new Dimension(100, leftColumn.getPreferredSize().height));
    c.gridx = 0;
    c.gridy = 0;
    c.gridheight = 2;
    c.anchor = GridBagConstraints.WEST;
    c.fill = GridBagConstraints.NONE;
    infoPanel.add(leftColumn, c);

    c.gridx = 1;
    c.gridy = 0;
    c.gridheight = 2;
    c.weightx = 1;
    c.fill = GridBagConstraints.HORIZONTAL;
    JLabel subjectsLabel = new JLabel("| " + String.join(", ", student.getSubjectNames()));
    subjectsLabel.setHorizontalAlignment(SwingConstants.LEFT);
    infoPanel.add(subjectsLabel, c);

    frame.add(infoPanel, BorderLayout.CENTER);

    editButton.addActionListener(e -> {
      // Edit name
      String newName = JOptionPane.showInputDialog(frame, "Edit Name:", student.getName());
      if (newName == null || newName.trim().isEmpty()) return;

      // Grade selection
      List<String> grades = GradeDAO.getAllGradeNames();
      grades.sort(Comparator.comparingInt(Integer::parseInt));
      String[] gradesArray = grades.toArray(new String[0]);
      String newGrade = (String) JOptionPane.showInputDialog(
        frame,
        "Select Grade:",
        "Edit Grade",
        JOptionPane.PLAIN_MESSAGE,
        null,
        gradesArray,
        student.getGradeName()
      );
      if (newGrade == null) return;

      int newGradeId = GradeDAO.getGradeIdByName(newGrade);

      // Subject selection
      List<String> allSubjects = SubjectDAO.getAllSubjects();
      JList<String> subjectList = new JList<>(allSubjects.toArray(new String[0]));
      subjectList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

      List<String> currentSubjects = student.getSubjectNames();
      int[] selectedIndices = allSubjects.stream()
        .mapToInt(allSubjects::indexOf)
        .filter(i -> currentSubjects.contains(allSubjects.get(i)))
        .toArray();
      subjectList.setSelectedIndices(selectedIndices);

      int result = JOptionPane.showConfirmDialog(
        frame,
        new JScrollPane(subjectList),
        "Select Subjects (hold 'Ctrl' to select multiple)",
        JOptionPane.OK_CANCEL_OPTION,
        JOptionPane.PLAIN_MESSAGE
      );
      if (result != JOptionPane.OK_OPTION) return;

      List<String> newSubjects = subjectList.getSelectedValuesList();
      List<Integer> newSubjectIds = new ArrayList<>();
      for (String subjName : newSubjects) {
        int id = SubjectDAO.getSubjectId(subjName);
        newSubjectIds.add(id);
      }

      // Update student
      student.setName(newName);
      student.setGradeId(newGradeId);
      student.setSubjectIds(newSubjectIds);
      StudentDAO.updateStudent(student);

      // Update GUI
      nameLabel.setText(student.getName());
      gradeLabel.setText("Grade " + student.getGradeName());
      subjectsLabel.setText("| " + String.join(", ", student.getSubjectNames()));

      frame.revalidate();
      frame.repaint();
    });

    deleteButton.addActionListener(e -> {
      int confirm = JOptionPane.showConfirmDialog(frame,
        "Are you sure you want to delete " + student.getName() + "?",
        "Confirm Delete",
        JOptionPane.YES_NO_OPTION);
      if (confirm == JOptionPane.YES_OPTION) {
        StudentDAO.deleteStudent(student.getId());
        mainPanel.remove(frame);
        mainPanel.revalidate();
        mainPanel.repaint();
      }
    });

    return frame;
  }
}
