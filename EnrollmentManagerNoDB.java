import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class EnrollmentManagerNoDB {

    // Student class (same file)
    static class Student {
        private int id;
        private String name;
        private String course;
        private String email;

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
    }

    private JTextField nameField = new JTextField();
    private JTextField courseField = new JTextField();
    private JTextField emailField = new JTextField();

    private DefaultTableModel model = new DefaultTableModel(
            new String[]{"ID", "Name", "Course", "Email"}, 0);

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
        frame.setSize(700, 400);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout(10,10));

        JPanel form = new JPanel(new GridLayout(4,2,5,5));

        form.add(new JLabel("Student Name:"));
        form.add(nameField);

        form.add(new JLabel("Course:"));
        form.add(courseField);

        form.add(new JLabel("Email:"));
        form.add(emailField);

        final JButton addBtn = new JButton("Add");
        final JButton clearBtn = new JButton("Clear");
        final JButton deleteBtn = new JButton("Delete Selected");

        form.add(addBtn);
        form.add(clearBtn);

        frame.add(form, BorderLayout.NORTH);

        final JTable table = new JTable(model);
        frame.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(deleteBtn);
        frame.add(bottom, BorderLayout.SOUTH);

        // ✅ Java 7 style listeners
        addBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleAdd(frame);
            }
        });

        clearBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clearFields();
            }
        });

        deleteBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleDelete(table, frame);
            }
        });

        frame.setVisible(true);
    }

    private void handleAdd(JFrame frame) {
        String name = nameField.getText().trim();
        String course = courseField.getText().trim();
        String email = emailField.getText().trim();

        if (!validateInput(frame, name, course, email)) return;

        Student s = new Student(nextId++, name, course, email);
        students.add(s);

        refreshTable();
        clearFields();
    }

    private void handleDelete(JTable table, JFrame frame) {
        int selectedRow = table.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(frame, "Please select a row to delete.");
            return;
        }

        students.remove(selectedRow);
        refreshTable();
    }

    private void refreshTable() {
        model.setRowCount(0);

        for (Student s : students) {
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
            JOptionPane.showMessageDialog(frame, "Name is required.");
            return false;
        }

        if (course.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Course is required.");
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
        courseField.setText("");
        emailField.setText("");
    }
}