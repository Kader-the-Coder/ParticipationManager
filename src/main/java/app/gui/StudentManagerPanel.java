package main.java.app.gui;

import main.java.app.dao.StudentDAO;
import main.java.app.dao.SubjectDAO;
import main.java.app.dao.GradeDAO;
import main.java.app.models.Student;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class StudentManagerPanel extends JPanel {

  private final JPanel mainPanel;
  private final JScrollPane scrollPane;
  private final int studentPanelHeight = 50;
  private final MainFrame mainFrame;

  public StudentManagerPanel(MainFrame mainFrame) {
    this.mainFrame = mainFrame;
    setLayout(new BorderLayout(20, 20));
    setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    // Header
    JLabel titleLabel = new JLabel("Manage Students");
    titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
    titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
    add(titleLabel, BorderLayout.NORTH);

    // Main scrollable panel
    mainPanel = new JPanel();
    mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
    scrollPane = new JScrollPane(mainPanel);
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

    loadStudents();

    addButton.addActionListener(e -> addNewStudent());
    backButton.addActionListener(e -> mainFrame.showPanel("menu"));
  }

  private void loadStudents() {
    mainPanel.removeAll();
    List<Student> students = StudentDAO.getAllStudents();

    Function<Student, String[]> displayMapper = s -> new String[]{
      s.getName(),
      "Grade " + s.getGradeName(),
      "Subjects: " + String.join(", ", s.getSubjectNames())
    };

    List<Consumer<Student>> actions = Arrays.asList(this::editStudent, this::deleteStudent);

    for (Student s : students) {
      JButton editBtn = new JButton("✎");
      JButton deleteBtn = new JButton("❌");
      List<JButton> buttons = Arrays.asList(editBtn, deleteBtn);

      ListableFrameBuilder<Student> builder = new ListableFrameBuilder<>(mainPanel, displayMapper, actions);
      JPanel studentFrame = builder.buildFrame(s, buttons);
      studentFrame.setMaximumSize(new Dimension(Integer.MAX_VALUE, studentPanelHeight));
      mainPanel.add(studentFrame);
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
    for (String subjName : selectedSubjects) subjectIds.add(SubjectDAO.getSubjectId(subjName));

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
    for (String subjName : newSubjects) newSubjectIds.add(SubjectDAO.getSubjectId(subjName));

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
