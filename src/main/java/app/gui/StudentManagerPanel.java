package main.java.app.gui;

import main.java.app.dao.StudentDAO;
import main.java.app.dao.SubjectDAO;
import main.java.app.dao.GradeDAO;
import main.java.app.models.Student;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class StudentManagerPanel extends JPanel {

  private final JPanel mainPanel;
  private final JScrollPane scrollPane;
  private final int studentPanelHeight = 50;

  public StudentManagerPanel(MainFrame mainFrame) {
    setLayout(new BorderLayout(20, 20));
    setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    JLabel titleLabel = new JLabel("Manage Students");
    titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
    titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
    add(titleLabel, BorderLayout.NORTH);

    mainPanel = new JPanel();
    mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
    scrollPane = new JScrollPane(mainPanel);
    add(scrollPane, BorderLayout.CENTER);

    JPanel bottomPanel = new JPanel(new BorderLayout());

    JButton addButton = new JButton("➕ Add Student");
    JPanel addButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    addButtonPanel.add(addButton);
    bottomPanel.add(addButtonPanel, BorderLayout.NORTH);

    JButton backButton = new JButton("⬅ Back to Menu");
    JPanel backButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    backButtonPanel.add(backButton);
    bottomPanel.add(backButtonPanel, BorderLayout.SOUTH);

    add(bottomPanel, BorderLayout.SOUTH);

    loadStudents();

    addButton.addActionListener(e -> addNewStudent());
    backButton.addActionListener(e -> mainFrame.showPanel("menu"));
  }

  private void loadStudents() {
    mainPanel.removeAll();
    List<Student> students = StudentDAO.getAllStudents();
    for (Student s : students) {
      JPanel sFrame = createStudentFrame(s);
      sFrame.setMaximumSize(new Dimension(Integer.MAX_VALUE, studentPanelHeight));
      mainPanel.add(sFrame);
    }
    mainPanel.revalidate();
    mainPanel.repaint();
  }

  private void addNewStudent() {
    String name = JOptionPane.showInputDialog(mainPanel, "Enter Student Name:");
    if (name == null || name.trim().isEmpty()) return;

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
      subjectIds.add(SubjectDAO.getSubjectId(subjName));
    }

    Student newStudent = new Student(name, gradeId, subjectIds);
    StudentDAO.addStudent(newStudent);

    JPanel newStudentFrame = createStudentFrame(newStudent);
    newStudentFrame.setMaximumSize(new Dimension(Integer.MAX_VALUE, studentPanelHeight));
    mainPanel.add(newStudentFrame);
    mainPanel.revalidate();
    mainPanel.repaint();
  }

  private JPanel createStudentFrame(Student student) {
    JPanel frame = new JPanel(new BorderLayout());
    frame.setBorder(BorderFactory.createCompoundBorder(
      BorderFactory.createLineBorder(Color.GRAY),
      BorderFactory.createEmptyBorder(5, 5, 5, 5)
    ));

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
      String newName = JOptionPane.showInputDialog(frame, "Edit Name:", student.getName());
      if (newName == null || newName.trim().isEmpty()) return;

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
        newSubjectIds.add(SubjectDAO.getSubjectId(subjName));
      }

      student.setName(newName);
      student.setGradeId(newGradeId);
      student.setSubjectIds(newSubjectIds);
      StudentDAO.updateStudent(student);

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
