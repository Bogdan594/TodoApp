import javax.swing.*;
import java.awt.*;

public class UI extends JFrame {
    private TaskRepo repo;
    private JPanel listContainer;
    private ImageIcon checkIcon = new ImageIcon(getClass().getResource("/images/checkicon.png"));
    private ImageIcon emptyIcon = new ImageIcon(getClass().getResource("/images/ongoingicon.jpg"));
    private ImageIcon deleteIcon = new ImageIcon(getClass().getResource("/images/trash.jpg"));

    public UI(TaskRepo repo) {
        this.repo = repo;
        checkIcon = scaleIcon(checkIcon, 25, 25);
        emptyIcon = scaleIcon(emptyIcon, 25, 25);
        deleteIcon = scaleIcon(deleteIcon, 25, 25);
        setTitle("TODO LIST");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        listContainer = new JPanel();
        listContainer.setLayout(new BoxLayout(listContainer, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(listContainer);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        JPanel pnlAdd = new JPanel();
        JButton btnAddMain = new JButton("Add New Task");
        btnAddMain.setFont(new Font("Arial", Font.BOLD, 14));
        btnAddMain.setPreferredSize(new Dimension(200, 40));

        btnAddMain.addActionListener(e -> openAddTaskDialog());

        pnlAdd.add(btnAddMain);
        add(pnlAdd, BorderLayout.SOUTH);

        refreshList();
    }

    private ImageIcon scaleIcon(ImageIcon icon, int width, int height) {
        Image img = icon.getImage();
        Image newImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(newImg);
    }

    private JPanel createTaskRow(Task task) {
        JPanel row = new JPanel(new BorderLayout(15, 0));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        row.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        row.setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel(task.getTitle());
        lblTitle.setFont(new Font("Arial", task.isDone() ? Font.ITALIC : Font.BOLD, 15));
        if (task.isDone()) lblTitle.setForeground(Color.LIGHT_GRAY);
        row.add(lblTitle, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        btnPanel.setOpaque(false);

        JButton btnDetails = new JButton("Details");
        btnDetails.addActionListener(e -> showDetails(task));

        JButton btnStatus = new JButton(task.isDone() ? checkIcon : emptyIcon);
        styleIconButton(btnStatus);

        JButton btnDelete = new JButton(deleteIcon);
        styleIconButton(btnDelete);

        btnStatus.addActionListener(e -> {
            repo.updateTaskStatus(task.getId(), !task.isDone());
            refreshList();
        });

        btnDelete.addActionListener(e -> {
            repo.deleteTask(task.getId());
            refreshList();
        });


        btnPanel.add(btnDetails);
        btnPanel.add(btnStatus);
        btnPanel.add(btnDelete);

        row.add(btnPanel, BorderLayout.EAST);

        return row;
    }


    private void showDetails(Task task) {
        JDialog dialog = new JDialog(this, "Task details", true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(15, 15));

        JLabel lblTitle = new JLabel(task.getTitle(), SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        dialog.add(lblTitle, BorderLayout.NORTH);

        JTextArea txtDesc = new JTextArea(task.getDescription());
        txtDesc.setFont(new Font("Arial", Font.PLAIN, 14));
        txtDesc.setLineWrap(true);
        txtDesc.setWrapStyleWord(true);
        txtDesc.setEditable(false);
        txtDesc.setMargin(new Insets(10, 10, 10, 10));
        txtDesc.setBackground(new Color(240, 240, 240));

        JScrollPane scrollDesc = new JScrollPane(txtDesc);
        scrollDesc.setBorder(BorderFactory.createTitledBorder("Description"));
        dialog.add(scrollDesc, BorderLayout.CENTER);

        JPanel pnlActions = new JPanel();
        JButton btnEdit = new JButton("Edit");
        JButton btnClose = new JButton("ok");

        btnEdit.addActionListener(e -> {
            if (btnEdit.getText().equals("Edit")) {
                // edit
                txtDesc.setEditable(true);
                txtDesc.setBackground(Color.WHITE);
                btnEdit.setText("Save");
            } else {
                // save
                repo.updateTask(task.getId(), task.getTitle(), txtDesc.getText());
                task.setDescription(txtDesc.getText());

                txtDesc.setEditable(false);
                txtDesc.setBackground(new Color(240, 240, 240));
                btnEdit.setText("Edit");
                refreshList();
                JOptionPane.showMessageDialog(dialog, "Saved!");
            }
        });

        btnClose.addActionListener(e -> dialog.dispose());

        pnlActions.add(btnEdit);
        pnlActions.add(btnClose);
        dialog.add(pnlActions, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }


    private void openAddTaskDialog() {
        JDialog dialog = new JDialog(this, "Add new task", true);
        dialog.setSize(350, 250);
        dialog.setLayout(new GridLayout(3, 1, 10, 10));
        dialog.setLocationRelativeTo(this);

        JTextField tfTitle = new JTextField();
        tfTitle.setBorder(BorderFactory.createTitledBorder("Title"));

        JTextArea taDesc = new JTextArea();
        taDesc.setBorder(BorderFactory.createTitledBorder("Description"));

        JButton btnSave = new JButton("Save");
        btnSave.addActionListener(e -> {
            if (!tfTitle.getText().trim().isEmpty()) {
                repo.addTask(tfTitle.getText(), taDesc.getText());
                refreshList();
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Title cannot be empty!");
            }
        });

        dialog.add(tfTitle);
        dialog.add(new JScrollPane(taDesc));
        dialog.add(btnSave);
        dialog.setVisible(true);
    }

    private void styleIconButton(JButton btn) {
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public void refreshList() {
        listContainer.removeAll();

        java.util.List<Task> tasks = repo.getAllTasks();

        for (Task t : tasks) {
            listContainer.add(createTaskRow(t));
            listContainer.add(new JSeparator(SwingConstants.HORIZONTAL));
        }

        listContainer.revalidate();
        listContainer.repaint();
    }

    public void showWindow() {
        setVisible(true);
    }

}
