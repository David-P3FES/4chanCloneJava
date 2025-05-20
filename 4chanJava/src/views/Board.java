package views;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.io.File;

public class Board extends JFrame {

    private String boardName; // E.g., "/b/ - Random"

    // Components for the new post form
    private JTextField nameField;
    private JTextField emailField;
    private JTextField subjectField;
    private JTextArea commentArea;
    private JLabel selectedImageLabel;
    private JButton selectImageButton;
    private JButton postButton;

    // Panel to display existing threads
    private JPanel threadsPanel;

    public Board(String boardName) {
        this.boardName = boardName;
        initComponents();
        loadThreads(); // Simulate loading existing threads
    }

    private void initComponents() {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE); // Close only this window
        setTitle(boardName + " - Persona 3 Boards");
        setPreferredSize(new Dimension(1000, 800)); // Adjusted size for a board view
        setBackground(new Color(238, 238, 238)); // Light gray background

        // --- Header ---
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(204, 204, 204)); // Light gray header
        headerPanel.setBorder(new LineBorder(Color.BLACK, 1));
        JLabel boardTitleLabel = new JLabel(boardName);
        boardTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        headerPanel.add(boardTitleLabel);

        // --- New Thread Form ---
        JPanel newPostFormPanel = new JPanel();
        newPostFormPanel.setBorder(BorderFactory.createTitledBorder(new LineBorder(Color.BLACK, 1), "Crear Nuevo Hilo"));
        newPostFormPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Padding for components
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        newPostFormPanel.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        nameField = new JTextField(20);
        newPostFormPanel.add(nameField, gbc);

        // Email
        gbc.gridx = 0;
        gbc.gridy = 1;
        newPostFormPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        emailField = new JTextField(20);
        newPostFormPanel.add(emailField, gbc);

        // Subject
        gbc.gridx = 0;
        gbc.gridy = 2;
        newPostFormPanel.add(new JLabel("Asunto:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 2; // Span across two columns
        subjectField = new JTextField(30);
        newPostFormPanel.add(subjectField, gbc);
        gbc.gridwidth = 1; // Reset gridwidth

        // Comment
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.NORTHWEST; // Align to top-left for JTextArea
        newPostFormPanel.add(new JLabel("Comentario:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0; // Allow it to expand horizontally
        gbc.weighty = 1.0; // Allow it to expand vertically
        commentArea = new JTextArea(5, 40);
        commentArea.setLineWrap(true);
        commentArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(commentArea);
        newPostFormPanel.add(scrollPane, gbc);
        gbc.gridwidth = 1;
        gbc.weightx = 0; // Reset weightx
        gbc.weighty = 0; // Reset weighty
        gbc.anchor = GridBagConstraints.CENTER; // Reset anchor

        // Image Upload
        gbc.gridx = 0;
        gbc.gridy = 4;
        newPostFormPanel.add(new JLabel("Imagen:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 4;
        selectImageButton = new JButton("Seleccionar Archivo...");
        selectImageButton.addActionListener(e -> selectImage());
        newPostFormPanel.add(selectImageButton, gbc);
        gbc.gridx = 2;
        gbc.gridy = 4;
        selectedImageLabel = new JLabel("Ningún archivo seleccionado");
        newPostFormPanel.add(selectedImageLabel, gbc);

        // Post Button
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        postButton = new JButton("Publicar");
        postButton.addActionListener(e -> postNewThread());
        newPostFormPanel.add(postButton, gbc);

        // --- Threads Display Area ---
        threadsPanel = new JPanel();
        threadsPanel.setLayout(new BoxLayout(threadsPanel, BoxLayout.Y_AXIS)); // Stack threads vertically
        JScrollPane threadsScrollPane = new JScrollPane(threadsPanel);
        threadsScrollPane.setBorder(BorderFactory.createEmptyBorder()); // No border for the scroll pane itself
        threadsScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        // --- Main Layout ---
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(headerPanel, BorderLayout.NORTH);
        getContentPane().add(newPostFormPanel, BorderLayout.NORTH); // This will push the header to the top
        getContentPane().add(threadsScrollPane, BorderLayout.CENTER); // Threads below the form

        pack();
        setLocationRelativeTo(null); // Center the window
    }

    private void selectImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Image Files", "jpg", "png", "gif"));
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            selectedImageLabel.setText(selectedFile.getName());
            // In a real application, you'd store this file for upload
            System.out.println("Selected file: " + selectedFile.getAbsolutePath());
        } else {
            selectedImageLabel.setText("Ningún archivo seleccionado");
        }
    }

    private void postNewThread() {
        String name = nameField.getText();
        String email = emailField.getText();
        String subject = subjectField.getText();
        String comment = commentArea.getText();
        String imageName = selectedImageLabel.getText(); // Or null if no image

        if (comment.isEmpty() && imageName.equals("Ningún archivo seleccionado")) {
            JOptionPane.showMessageDialog(this, "El comentario o una imagen son obligatorios para crear un hilo.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        System.out.println("Nuevo Hilo:");
        System.out.println("  Nombre: " + (name.isEmpty() ? "Anónimo" : name));
        System.out.println("  Email: " + email);
        System.out.println("  Asunto: " + (subject.isEmpty() ? "Sin Asunto" : subject));
        System.out.println("  Comentario: " + comment);
        System.out.println("  Imagen: " + imageName);

        // Simulate adding the new thread to the display
        addThreadToDisplay(
                (name.isEmpty() ? "Anónimo" : name),
                (subject.isEmpty() ? "" : subject),
                comment,
                imageName.equals("Ningún archivo seleccionado") ? null : "/resource/default_thumbnail.png" // Placeholder image for now
        );

        // Clear the form
        nameField.setText("");
        emailField.setText("");
        subjectField.setText("");
        commentArea.setText("");
        selectedImageLabel.setText("Ningún archivo seleccionado");
    }

    private void loadThreads() {
        // Simulate fetching threads from a database or API
        // For demonstration, we'll add a few dummy threads
        addThreadToDisplay("Anónimo", "¡Primer Hilo de Prueba!", "¡Bienvenidos al tablero! Aquí podremos compartir imágenes y discutir temas.", "/resource/sample_image1.jpg");
        addThreadToDisplay("Alice", "Pregunta sobre Java Swing", "Estoy teniendo problemas con el layout de mis componentes en Swing. ¿Algún consejo?", "/resource/default_thumbnail.png");
        addThreadToDisplay("Bob", "Hilo sin imagen", "Este es un hilo sin ninguna imagen adjunta, solo texto.", null);
        addThreadToDisplay("Anónimo", "Recomendaciones de animes", "Estoy buscando animes nuevos para ver, ¿alguna recomendación?", "/resource/sample_image2.jpg");
    }

    private void addThreadToDisplay(String author, String subject, String comment, String imagePath) {
        JPanel threadPanel = new JPanel();
        threadPanel.setLayout(new BorderLayout(5, 5)); // Gap between components
        threadPanel.setBorder(new LineBorder(Color.BLACK, 1));
        threadPanel.setBackground(Color.WHITE);
        threadPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200)); // Limit height for preview

        // Thread header (Author, Subject, Date/Time - simplified)
        JPanel threadHeader = new JPanel(new FlowLayout(FlowLayout.LEFT));
        threadHeader.setBackground(new Color(240, 240, 240)); // Slightly darker header for thread
        JLabel subjectLabel = new JLabel("<html><a href='#'>" + (subject.isEmpty() ? "Sin Asunto" : subject) + "</a></html>");
        subjectLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        subjectLabel.setForeground(new Color(0, 0, 150)); // Link color
        subjectLabel.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Indicate it's clickable
        // Add a MouseListener for clicking the subject to view full thread

        JLabel authorLabel = new JLabel("  " + author + " ");
        authorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        authorLabel.setForeground(new Color(50, 100, 50)); // Greenish for name

        threadHeader.add(subjectLabel);
        threadHeader.add(authorLabel);
        // Add date/time here if you have it

        threadPanel.add(threadHeader, BorderLayout.NORTH);

        // Content Area (Image + Comment)
        JPanel contentPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5)); // Padding
        contentPanel.setBackground(Color.WHITE);

        if (imagePath != null) {
            try {
                ImageIcon originalIcon = new ImageIcon(getClass().getResource(imagePath));
                Image image = originalIcon.getImage(); // transform it
                Image newimg = image.getScaledInstance(150, 150,  java.awt.Image.SCALE_SMOOTH); // scale it smoothly
                ImageIcon resizedIcon = new ImageIcon(newimg);  // transform it back
                JLabel imageLabel = new JLabel(resizedIcon);
                contentPanel.add(imageLabel);
            } catch (Exception e) {
                System.err.println("Error loading image: " + imagePath + " - " + e.getMessage());
                // Fallback if image not found
                JLabel placeholder = new JLabel("[No Image]");
                placeholder.setPreferredSize(new Dimension(150, 150));
                placeholder.setBorder(new LineBorder(Color.LIGHT_GRAY));
                contentPanel.add(placeholder);
            }
        }

        JTextArea commentPreview = new JTextArea(comment);
        commentPreview.setEditable(false);
        commentPreview.setLineWrap(true);
        commentPreview.setWrapStyleWord(true);
        commentPreview.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        commentPreview.setBackground(Color.WHITE);
        commentPreview.setBorder(BorderFactory.createEmptyBorder()); // No border

        // Limit comment preview height and width
        commentPreview.setPreferredSize(new Dimension(700, Math.min(50, commentPreview.getPreferredSize().height)));
        commentPreview.setMinimumSize(new Dimension(700, 20)); // Minimum height

        contentPanel.add(commentPreview);

        threadPanel.add(contentPanel, BorderLayout.CENTER);

        // Add a "Replies" count or "Reply" button here for more interaction

        // Add some vertical space between threads
        threadsPanel.add(Box.createVerticalStrut(10));
        threadsPanel.add(threadPanel);
        threadsPanel.revalidate();
        threadsPanel.repaint();
    }


    public static void main(String args[]) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(Board.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(() -> {
            new Board("/g/ - Tecnología").setVisible(true); // Example board
        });
    }
}