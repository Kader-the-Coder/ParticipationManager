package main.java.app.gui.panels;

import main.java.app.dao.*;
import main.java.app.gui.components.RoundButton;
import main.java.app.gui.frames.MainFrame;
import main.java.app.gui.templates.PanelTemplate;
import main.java.app.models.Student;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Function;

public class StudentManagerPanel extends BasePanel {

  private final JPanel mainPanel;

  public StudentManagerPanel(MainFrame mainFrame) {
    super(mainFrame);

    // Header
    JLabel headerLabel = new JLabel("Manage Students");
    headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
    headerLabel.setHorizontalAlignment(SwingConstants.CENTER);
    setHeaderComponent(headerLabel);

    // Main scrollable panel
    mainPanel = new JPanel();
    mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
    setBodyComponent(mainPanel);

    // Footer button: Add Student
    JButton addStudentBtn = new JButton("➕ Add Student");
    addFooterButton(addStudentBtn);

    addStudentBtn.addActionListener(e -> addNewStudent());

    loadStudents();
  }

  private void loadStudents() {
    mainPanel.removeAll();
    List<Student> students = StudentsDAO.getAllStudents();

    Function<Student, String[]> fieldInfo = this::getFieldInfo;
    Function<Student, List<JButton>> buttonSupplier = this::getJButtons;

    PanelTemplate<Student> builder = new PanelTemplate<>(mainPanel);
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

    List<String> grades = GradesDAO.getAllGradeNames();
    grades.sort(Comparator.comparingInt(Integer::parseInt));
    String[] gradesArray = grades.toArray(new String[0]);
    String gradeName = (String) JOptionPane.showInputDialog(
      mainPanel, "Select Grade:", "New Student", JOptionPane.PLAIN_MESSAGE, null, gradesArray, gradesArray[0]);
    if (gradeName == null) return;
    int gradeId = GradesDAO.getGradeIdByName(gradeName);

    List<String> allSubjects = SubjectsDAO.getAllSubjects();
    JList<String> subjectList = new JList<>(allSubjects.toArray(new String[0]));
    subjectList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

    int result = JOptionPane.showConfirmDialog(mainPanel, new JScrollPane(subjectList),
      "Select Subjects (hold 'Ctrl' to select multiple)", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
    if (result != JOptionPane.OK_OPTION) return;

    List<String> selectedSubjects = subjectList.getSelectedValuesList();
    List<Integer> subjectIds = new ArrayList<>();
    for (String subjName : selectedSubjects)
      subjectIds.add(SubjectsDAO.getSubjectId(subjName));

    Student newStudent = new Student(name, gradeId, subjectIds);
    StudentsDAO.addStudent(newStudent);

    loadStudents();
  }

  private void editStudent(Student student) {
    String newName = JOptionPane.showInputDialog(mainPanel, "Edit Name:", student.getName());
    if (newName == null || newName.trim().isEmpty()) return;

    List<String> grades = GradesDAO.getAllGradeNames();
    grades.sort(Comparator.comparingInt(Integer::parseInt));
    String[] gradesArray = grades.toArray(new String[0]);
    String newGrade = (String) JOptionPane.showInputDialog(
      mainPanel, "Select Grade:", "Edit Grade", JOptionPane.PLAIN_MESSAGE, null, gradesArray, student.getGradeName());
    if (newGrade == null) return;
    int newGradeId = GradesDAO.getGradeIdByName(newGrade);

    List<String> allSubjects = SubjectsDAO.getAllSubjects();
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
      newSubjectIds.add(SubjectsDAO.getSubjectId(subjName));

    student.setName(newName);
    student.setGradeId(newGradeId);
    student.setSubjectIds(newSubjectIds);
    StudentsDAO.updateStudent(student);

    loadStudents();
  }

  private void deleteStudent(Student student) {
    int confirm = JOptionPane.showConfirmDialog(mainPanel,
      "Are you sure you want to delete " + student.getName() + "?",
      "Confirm Delete",
      JOptionPane.YES_NO_OPTION);
    if (confirm == JOptionPane.YES_OPTION) {
      StudentsDAO.deleteStudent(student.getId());
      loadStudents();
    }
  }
}
