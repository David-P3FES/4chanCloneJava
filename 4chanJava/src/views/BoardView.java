package views;

import dao.ThreadDAO;
import models.Board;
import models.Thread;
import util.ImageUtil; // New utility for image handling

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class BoardView extends JFrame {

    private Board currentBoard;
    private ThreadDAO threadDAO;

    // Components for the new post form (for new threads in this view)
    private JTextField nameField;
    private JTextField emailField;
    private JTextField subjectField;
    private JTextArea commentArea;
    private JLabel selectedImageLabel;
    private JButton selectImageButton;
    private JButton postButton;
    private File selectedImageFile; // To hold the actual file

    // Panel to display existing threads
    private JPanel threadsPanel;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yy (HH:mm:ss)");

    public BoardView(Board board) {
        this.currentBoard = board;
        this.threadDAO = new ThreadDAO();
        initComponents();
        loadThreads();
    }

    private void initComponents() {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(currentBoard.getFullName() + " - Persona 3 Boards");
        setPreferredSize(new Dimension(1000, 800));
        setBackground(new Color(238, 238, 238));

        // --- Header ---
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(204, 204, 204));
        headerPanel.setBorder(new LineBorder(Color.BLACK, 1));
        JLabel boardTitleLabel = new JLabel(currentBoard.getFullName());
        boardTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        headerPanel.add(boardTitleLabel);

        // --- New Thread Form ---
        JPanel newPostFormPanel = new JPanel();
        newPostFormPanel.setBorder(BorderFactory.createTitledBorder(new LineBorder(Color.BLACK, 1), "Crear Nuevo Hilo"));
        newPostFormPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; newPostFormPanel.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; nameField = new JTextField(20); newPostFormPanel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; newPostFormPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; emailField = new JTextField(20); newPostFormPanel.add(emailField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; newPostFormPanel.add(new JLabel("Asunto:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.gridwidth = 2; subjectField = new JTextField(30); newPostFormPanel.add(subjectField, gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = 3; gbc.anchor = GridBagConstraints.NORTHWEST; newPostFormPanel.add(new JLabel("Comentario:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; gbc.gridwidth = 2; gbc.weightx = 1.0; gbc.weighty = 1.0;
        commentArea = new JTextArea(5, 40);
        commentArea.setLineWrap(true);
        commentArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(commentArea);
        newPostFormPanel.add(scrollPane, gbc);
        gbc.gridwidth = 1; gbc.weightx = 0; gbc.weighty = 0; gbc.anchor = GridBagConstraints.CENTER;

        gbc.gridx = 0; gbc.gridy = 4; newPostFormPanel.add(new JLabel("Imagen:"), gbc);
        gbc.gridx = 1; gbc.gridy = 4;
        selectImageButton = new JButton("Seleccionar Archivo...");
        selectImageButton.addActionListener(e -> selectImage());
        newPostFormPanel.add(selectImageButton, gbc);
        gbc.gridx = 2; gbc.gridy = 4;
        selectedImageLabel = new JLabel("Ningún archivo seleccionado");
        newPostFormPanel.add(selectedImageLabel, gbc);

        gbc.gridx = 1; gbc.gridy = 5; gbc.gridwidth = 2;
        postButton = new JButton("Publicar Hilo");
        postButton.addActionListener(e -> postNewThread());
        newPostFormPanel.add(postButton, gbc);

        // --- Threads Display Area ---
        threadsPanel = new JPanel();
        threadsPanel.setLayout(new BoxLayout(threadsPanel, BoxLayout.Y_AXIS));
        JScrollPane threadsScrollPane = new JScrollPane(threadsPanel);
        threadsScrollPane.setBorder(BorderFactory.createEmptyBorder());
        threadsScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        // --- Main Layout ---
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(headerPanel, BorderLayout.NORTH);
        getContentPane().add(newPostFormPanel, BorderLayout.CENTER); // Form takes center initially
        getContentPane().add(threadsScrollPane, BorderLayout.SOUTH); // Threads are at the bottom

        pack();
        setLocationRelativeTo(null);
    }

    private void selectImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Image Files", "jpg", "png", "gif"));
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedImageFile = fileChooser.getSelectedFile();
            selectedImageLabel.setText(selectedImageFile.getName());
        } else {
            selectedImageFile = null;
            selectedImageLabel.setText("Ningún archivo seleccionado");
        }
    }

    private void postNewThread() {
        String name = nameField.getText();
        String email = emailField.getText();
        String subject = subjectField.getText();
        String comment = commentArea.getText();
        String imagePath = null;

        if (comment.isEmpty() && selectedImageFile == null) {
            JOptionPane.showMessageDialog(this, "El comentario o una imagen son obligatorios para crear un hilo.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (selectedImageFile != null) {
            try {
                // Save image and get its path relative to resource folder
                imagePath = ImageUtil.saveImageToResources(selectedImageFile);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error al guardar la imagen: " + e.getMessage(), "Error de Imagen", JOptionPane.ERROR_MESSAGE);
                System.err.println("Error saving image: " + e.getMessage());
                return;
            }
        }

        Thread newThread = new Thread(
                currentBoard.getId(),
                subject,
                name,
                email,
                comment,
                imagePath
        );

        int newThreadId = threadDAO.createThread(newThread);

        if (newThreadId != -1) {
            JOptionPane.showMessageDialog(this, "Hilo publicado con éxito!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            // Refresh threads after posting
            loadThreads();
            // Clear form
            nameField.setText("");
            emailField.setText("");
            subjectField.setText("");
            commentArea.setText("");
            selectedImageLabel.setText("Ningún archivo seleccionado");
            selectedImageFile = null;
        } else {
            JOptionPane.showMessageDialog(this, "Error al publicar el hilo.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadThreads() {
        threadsPanel.removeAll(); // Clear existing threads
        List<Thread> threads = threadDAO.getThreadsByBoardId(currentBoard.getId());

        if (threads.isEmpty()) {
            JLabel noThreadsLabel = new JLabel("No hay hilos en este tablero. ¡Sé el primero en publicar!");
            noThreadsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            threadsPanel.add(noThreadsLabel);
        } else {
            for (Thread thread : threads) {
                addThreadPreview(thread);
            }
        }
        threadsPanel.revalidate();
        threadsPanel.repaint();
    }

    private void addThreadPreview(Thread thread) {
        JPanel threadPanel = new JPanel();
        threadPanel.setLayout(new BorderLayout(5, 5));
        threadPanel.setBorder(new LineBorder(Color.BLACK, 1));
        threadPanel.setBackground(Color.WHITE);
        threadPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

        // Thread header
        JPanel threadHeader = new JPanel(new FlowLayout(FlowLayout.LEFT));
        threadHeader.setBackground(new Color(240, 240, 240));

        JLabel subjectLabel = new JLabel("<html><a href='#'>" + (thread.getSubject().isEmpty() ? "Sin Asunto" : thread.getSubject()) + "</a></html>");
        subjectLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        subjectLabel.setForeground(new Color(0, 0, 150));
        subjectLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel authorLabel = new JLabel("  " + thread.getName() + " ");
        authorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        authorLabel.setForeground(new Color(50, 100, 50));

        JLabel dateTimeLabel = new JLabel(" " + thread.getCreatedAt().format(DATE_FORMATTER));
        dateTimeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        dateTimeLabel.setForeground(Color.GRAY);

        threadHeader.add(subjectLabel);
        threadHeader.add(authorLabel);
        threadHeader.add(dateTimeLabel);

        threadPanel.add(threadHeader, BorderLayout.NORTH);

        // Content Area (Image + Comment)
        JPanel contentPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        contentPanel.setBackground(Color.WHITE);

        if (thread.getImagePath() != null && !thread.getImagePath().isEmpty()) {
            try {
                // Use ImageUtil to load and resize
                ImageIcon resizedIcon = ImageUtil.getScaledImage(thread.getImagePath(), 150, 150);
                JLabel imageLabel = new JLabel(resizedIcon);
                contentPanel.add(imageLabel);
            } catch (Exception e) {
                System.err.println("Error loading thread image: " + thread.getImagePath() + " - " + e.getMessage());
                JLabel placeholder = new JLabel("[Imagen no disponible]");
                placeholder.setPreferredSize(new Dimension(150, 150));
                placeholder.setBorder(new LineBorder(Color.LIGHT_GRAY));
                contentPanel.add(placeholder);
            }
        }

        JTextArea commentPreview = new JTextArea(thread.getComment());
        commentPreview.setEditable(false);
        commentPreview.setLineWrap(true);
        commentPreview.setWrapStyleWord(true);
        commentPreview.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        commentPreview.setBackground(Color.WHITE);
        commentPreview.setBorder(BorderFactory.createEmptyBorder());
        commentPreview.setPreferredSize(new Dimension(700, Math.min(50, commentPreview.getPreferredSize().height)));
        commentPreview.setMinimumSize(new Dimension(700, 20));

        contentPanel.add(commentPreview);
        threadPanel.add(contentPanel, BorderLayout.CENTER);

        // Click listener for the entire thread preview to open ThreadDetailView
        threadPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                ThreadDetailView threadDetailView = new ThreadDetailView(thread);
                threadDetailView.setVisible(true);
            }
            // Optional: visual feedback on hover
            @Override
            public void mouseEntered(MouseEvent e) {
                threadPanel.setBackground(new Color(250, 250, 240)); // Light yellow on hover
            }

            @Override
            public void mouseExited(MouseEvent e) {
                threadPanel.setBackground(Color.WHITE); // Back to white
            }
        });


        threadsPanel.add(Box.createVerticalStrut(10)); // Space between threads
        threadsPanel.add(threadPanel);
    }

    public static void main(String args[]) {
        // This main is for testing this specific view directly,
        // but the application starts from 'tableros' normally.
        // Ensure DatabaseManager.initializeDatabase() is called at application start.
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(BoardView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(() -> {
            // For direct testing, create a dummy board
            Board dummyBoard = new Board(1, "test", "/test/ - Tablero de Prueba", "Descripción de prueba.");
            new BoardView(dummyBoard).setVisible(true);
        });
    }
}