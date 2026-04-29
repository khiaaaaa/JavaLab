import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class EnrollmentManagerNoDB {

    static class Student {
        private int id;
        private String name, course, email;

        public Student(int id, String name, String course, String email) {
            this.id = id;
            this.name = name;
            this.course = course;
            this.email = email;
        }

        public int getId() { return id; }
        public String getName() { return name; }
        public String getCourse() { return course; }
        public String getEmail() { return email; }

        public void setName(String name) { this.name = name; }
        public void setCourse(String course) { this.course = course; }
        public void setEmail(String email) { this.email = email; }
    }

    private JTextField nameField = new JTextField();
    private JTextField emailField = new JTextField();
    private JTextField searchField = new JTextField();

    private JComboBox<String> courseBox = new JComboBox<String>(
        new String[]{"Select Course", "BS Computer Engineering", "BS Information Technology", "BS Computer Science"}
    );

    private DefaultTableModel model = new DefaultTableModel(
        new String[]{"ID", "Name", "Course", "Email"}, 0
    );

    private JTable table = new JTable(model);
    private List<Student> students = new ArrayList<Student>();
    private int nextId = 1;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new EnrollmentManagerNoDB().createUI();
            }
        });
    }

    private void createUI() {
        final JFrame frame = new JFrame("Enrollment Manager (No DB)");
        frame.setSize(850, 500);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridLayout(4, 2, 8, 8));
        form.setBorder(BorderFactory.createTitledBorder("Student Information"));

        form.add(new JLabel("Student Name:"));
        form.add(nameField);

        form.add(new JLabel("Course:"));
        form.add(courseBox);

        form.add(new JLabel("Email:"));
        form.add(emailField);

        JButton addBtn = new JButton("Add");
        JButton updateBtn = new JButton("Update");
        form.add(addBtn);
        form.add(updateBtn);

        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Search"));
        searchPanel.add(searchField, BorderLayout.CENTER);

        JButton searchBtn = new JButton("Search");
        JButton showAllBtn = new JButton("Show All");
        JPanel searchButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchButtons.add(searchBtn);
        searchButtons.add(showAllBtn);
        searchPanel.add(searchButtons, BorderLayout.EAST);

        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.add(form, BorderLayout.NORTH);
        topPanel.add(searchPanel, BorderLayout.SOUTH);

        frame.add(topPanel, BorderLayout.NORTH);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        frame.add(new JScrollPane(table), BorderLayout.CENTER);

        JButton clearBtn = new JButton("Clear");
        JButton deleteBtn = new JButton("Delete Selected");

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(clearBtn);
        bottom.add(deleteBtn);
        frame.add(bottom, BorderLayout.SOUTH);

        addBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleAdd(frame);
            }
        });

        updateBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleUpdate(frame);
            }
        });

        clearBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clearFields();
            }
        });

        deleteBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleDelete(frame);
            }
        });

        searchBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                searchStudent();
            }
        });

        showAllBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                refreshTableFromList(students);
            }
        });

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                loadSelectedRowToFields();
            }
        });

        frame.setVisible(true);
    }

    private void handleAdd(JFrame frame) {
        String name = nameField.getText().trim();
        String course = courseBox.getSelectedItem().toString();
        String email = emailField.getText().trim();

        if (!validateInput(frame, name, course, email)) return;

        Student student = new Student(nextId++, name, course, email);
        students.add(student);

        refreshTableFromList(students);
        clearFields();

        JOptionPane.showMessageDialog(frame, "Student added successfully!");
    }

    private void handleUpdate(JFrame frame) {
        int selectedRow = table.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(frame, "Please select a student to update.");
            return;
        }

        int id = Integer.parseInt(model.getValueAt(selectedRow, 0).toString());

        String name = nameField.getText().trim();
        String course = courseBox.getSelectedItem().toString();
        String email = emailField.getText().trim();

        if (!validateInput(frame, name, course, email)) return;

        for (Student s : students) {
            if (s.getId() == id) {
                s.setName(name);
                s.setCourse(course);
                s.setEmail(email);
                break;
            }
        }

        refreshTableFromList(students);
        clearFields();

        JOptionPane.showMessageDialog(frame, "Student updated successfully!");
    }

    private void handleDelete(JFrame frame) {
        int selectedRow = table.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(frame, "Please select a row to delete.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
            frame,
            "Are you sure you want to delete this student?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        int id = Integer.parseInt(model.getValueAt(selectedRow, 0).toString());

        for (int i = 0; i < students.size(); i++) {
            if (students.get(i).getId() == id) {
                students.remove(i);
                break;
            }
        }

        refreshTableFromList(students);
        clearFields();

        JOptionPane.showMessageDialog(frame, "Student deleted successfully!");
    }

    private void searchStudent() {
        String keyword = searchField.getText().trim().toLowerCase();
        List<Student> results = new ArrayList<Student>();

        for (Student s : students) {
            if (s.getName().toLowerCase().contains(keyword) ||
                s.getCourse().toLowerCase().contains(keyword) ||
                s.getEmail().toLowerCase().contains(keyword)) {
                results.add(s);
            }
        }

        refreshTableFromList(results);
    }

    private void loadSelectedRowToFields() {
        int selectedRow = table.getSelectedRow();

        if (selectedRow != -1) {
            nameField.setText(model.getValueAt(selectedRow, 1).toString());
            courseBox.setSelectedItem(model.getValueAt(selectedRow, 2).toString());
            emailField.setText(model.getValueAt(selectedRow, 3).toString());
        }
    }

    private void refreshTableFromList(List<Student> list) {
        model.setRowCount(0);

        for (Student s : list) {
            model.addRow(new Object[]{
                s.getId(),
                s.getName(),
                s.getCourse(),
                s.getEmail()
            });
        }
    }

    private boolean validateInput(JFrame frame, String name, String course, String email) {
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Student name is required.");
            return false;
        }

        if (course.equals("Select Course")) {
            JOptionPane.showMessageDialog(frame, "Please select a course.");
            return false;
        }

        if (!email.isEmpty() && (!email.contains("@") || !email.contains("."))) {
            JOptionPane.showMessageDialog(frame, "Invalid email format.");
            return false;
        }

        return true;
    }

    private void clearFields() {
        nameField.setText("");
        courseBox.setSelectedIndex(0);
        emailField.setText("");
        searchField.setText("");
        table.clearSelection();
    }
}