package main.java.app.gui;

import main.java.app.dao.*;
import main.java.app.models.Student;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Function;

public class StudentManagerPanel extends JPanel {

  private final JPanel mainPanel;

  public StudentManagerPanel(MainFrame mainFrame) {
    setLayout(new BorderLayout(20, 20));
    setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    // Header
    JLabel titleLabel = new JLabel("Manage Students");
    titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
    titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
    add(titleLabel, BorderLayout.NORTH);

    // Main scrollable panel
    mainPanel = new JPanel();
    mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
    JScrollPane scrollPane = new JScrollPane(mainPanel);
    add(scrollPane, BorderLayout.CENTER);

    // Bottom panel with buttons
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

    addButton.addActionListener(ignored -> addNewStudent());
    backButton.addActionListener(ignored -> mainFrame.showPanel("menu"));

    loadStudents();
  }

  private void loadStudents() {
    mainPanel.removeAll();
    List<Student> students = StudentDAO.getAllStudents();

    Function<Student, String[]> fieldInfo = this::getFieldInfo;
    Function<Student, List<JButton>> buttonSupplier = this::getJButtons;

    FrameBuilder<Student> builder = new FrameBuilder<>(mainPanel);
    for (Student s : students) {
      JPanel frame = builder.buildFrame(fieldInfo.apply(s), buttonSupplier.apply(s));
      frame.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
      mainPanel.add(frame);
    }

    mainPanel.revalidate();
    mainPanel.repaint();
  }

  private List<JButton> getJButtons(Student s) {
    RoundButton editBtn = new RoundButton("✎", Color.WHITE);
    RoundButton deleteBtn = new RoundButton("❌", Color.RED);

    editBtn.addActionListener(ignored -> editStudent(s));
    deleteBtn.addActionListener(ignored -> deleteStudent(s));

    return Arrays.asList(editBtn, deleteBtn);
  }

  private String[] getFieldInfo(Student s) {
    return new String[]{
      s.getName(),
      "Grade " + s.getGradeName(),
      "Subjects: " + String.join(", ", s.getSubjectNames())
    };
  }

  private void addNewStudent() {
    String name = JOptionPane.showInputDialog(mainPanel, "Enter Student Name:");
    if (name == null || name.trim().isEmpty()) return;

    List<String> grades = GradeDAO.getAllGradeNames();
    grades.sort(Comparator.comparingInt(Integer::parseInt));
    String[] gradesArray = grades.toArray(new String[0]);
    String gradeName = (String) JOptionPane.showInputDialog(
      mainPanel, "Select Grade:", "New Student", JOptionPane.PLAIN_MESSAGE, null, gradesArray, gradesArray[0]);
    if (gradeName == null) return;
    int gradeId = GradeDAO.getGradeIdByName(gradeName);

    List<String> allSubjects = SubjectDAO.getAllSubjects();
    JList<String> subjectList = new JList<>(allSubjects.toArray(new String[0]));
    subjectList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

    int result = JOptionPane.showConfirmDialog(mainPanel, new JScrollPane(subjectList),
      "Select Subjects (hold 'Ctrl' to select multiple)", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
    if (result != JOptionPane.OK_OPTION) return;

    List<String> selectedSubjects = subjectList.getSelectedValuesList();
    List<Integer> subjectIds = new ArrayList<>();
    for (String subjName : selectedSubjects)
      subjectIds.add(SubjectDAO.getSubjectId(subjName));

    Student newStudent = new Student(name, gradeId, subjectIds);
    StudentDAO.addStudent(newStudent);

    loadStudents();
  }

  private void editStudent(Student student) {
    String newName = JOptionPane.showInputDialog(mainPanel, "Edit Name:", student.getName());
    if (newName == null || newName.trim().isEmpty()) return;

    List<String> grades = GradeDAO.getAllGradeNames();
    grades.sort(Comparator.comparingInt(Integer::parseInt));
    String[] gradesArray = grades.toArray(new String[0]);
    String newGrade = (String) JOptionPane.showInputDialog(
      mainPanel, "Select Grade:", "Edit Grade", JOptionPane.PLAIN_MESSAGE, null, gradesArray, student.getGradeName());
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

    int result = JOptionPane.showConfirmDialog(mainPanel, new JScrollPane(subjectList),
      "Select Subjects (hold 'Ctrl' to select multiple)", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
    if (result != JOptionPane.OK_OPTION) return;

    List<String> newSubjects = subjectList.getSelectedValuesList();
    List<Integer> newSubjectIds = new ArrayList<>();
    for (String subjName : newSubjects)
      newSubjectIds.add(SubjectDAO.getSubjectId(subjName));

    student.setName(newName);
    student.setGradeId(newGradeId);
    student.setSubjectIds(newSubjectIds);
    StudentDAO.updateStudent(student);

    loadStudents();
  }

  private void deleteStudent(Student student) {
    int confirm = JOptionPane.showConfirmDialog(mainPanel,
      "Are you sure you want to delete " + student.getName() + "?",
      "Confirm Delete",
      JOptionPane.YES_NO_OPTION);
    if (confirm == JOptionPane.YES_OPTION) {
      StudentDAO.deleteStudent(student.getId());
      loadStudents();
    }
  }
}
