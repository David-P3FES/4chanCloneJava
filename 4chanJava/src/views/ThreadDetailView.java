package views;

import dao.PostDAO;
import models.Post;
import models.Thread;
import util.ImageUtil;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ThreadDetailView extends JFrame {

    private Thread currentThread;
    private PostDAO postDAO;

    // Components for new post form (for replies)
    private JTextField nameField;
    private JTextField emailField;
    private JTextArea commentArea;
    private JLabel selectedImageLabel;
    private JButton selectImageButton;
    private JButton postButton;
    private File selectedImageFile;

    // Panel to display the original thread post and replies
    private JPanel threadContentPanel;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yy (HH:mm:ss)");

    public ThreadDetailView(Thread thread) {
        this.currentThread = thread;
        this.postDAO = new PostDAO();
        initComponents();
        loadThreadContent(); // Load the original post and replies
    }

    private void initComponents() {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Hilo: " + currentThread.getSubject() + " - Persona 3");
        setPreferredSize(new Dimension(1000, 900));
        setBackground(new Color(238, 238, 238));

        // --- Header (Thread Subject) ---
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(204, 204, 204));
        headerPanel.setBorder(new LineBorder(Color.BLACK, 1));
        JLabel threadTitleLabel = new JLabel("Hilo: " + (currentThread.getSubject().isEmpty() ? "Sin Asunto" : currentThread.getSubject()));
        threadTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        headerPanel.add(threadTitleLabel);

        // --- Thread Content Panel (Scrollable) ---
        threadContentPanel = new JPanel();
        threadContentPanel.setLayout(new BoxLayout(threadContentPanel, BoxLayout.Y_AXIS));
        JScrollPane contentScrollPane = new JScrollPane(threadContentPanel);
        contentScrollPane.setBorder(BorderFactory.createEmptyBorder());
        contentScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        // --- New Post Form (for replies) ---
        JPanel newPostFormPanel = new JPanel();
        newPostFormPanel.setBorder(BorderFactory.createTitledBorder(new LineBorder(Color.BLACK, 1), "Añadir Comentario"));
        newPostFormPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; newPostFormPanel.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; nameField = new JTextField(20); newPostFormPanel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; newPostFormPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; emailField = new JTextField(20); newPostFormPanel.add(emailField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.NORTHWEST; newPostFormPanel.add(new JLabel("Comentario:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.gridwidth = 2; gbc.weightx = 1.0; gbc.weighty = 1.0;
        commentArea = new JTextArea(5, 40);
        commentArea.setLineWrap(true);
        commentArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(commentArea);
        newPostFormPanel.add(scrollPane, gbc);
        gbc.gridwidth = 1; gbc.weightx = 0; gbc.weighty = 0; gbc.anchor = GridBagConstraints.CENTER;

        gbc.gridx = 0; gbc.gridy = 3; newPostFormPanel.add(new JLabel("Imagen:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3;
        selectImageButton = new JButton("Seleccionar Archivo...");
        selectImageButton.addActionListener(e -> selectImage());
        newPostFormPanel.add(selectImageButton, gbc);
        gbc.gridx = 2; gbc.gridy = 3;
        selectedImageLabel = new JLabel("Ningún archivo seleccionado");
        newPostFormPanel.add(selectedImageLabel, gbc);

        gbc.gridx = 1; gbc.gridy = 4; gbc.gridwidth = 2;
        postButton = new JButton("Publicar Comentario");
        postButton.addActionListener(e -> postNewComment());
        newPostFormPanel.add(postButton, gbc);

        // --- Main Layout ---
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(headerPanel, BorderLayout.NORTH);
        getContentPane().add(contentScrollPane, BorderLayout.CENTER); // Thread content in center
        getContentPane().add(newPostFormPanel, BorderLayout.SOUTH); // New post form at bottom

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

    private void postNewComment() {
        String name = nameField.getText();
        String email = emailField.getText();
        String comment = commentArea.getText();
        String imagePath = null;

        if (comment.isEmpty() && selectedImageFile == null) {
            JOptionPane.showMessageDialog(this, "El comentario o una imagen son obligatorios para publicar.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (selectedImageFile != null) {
            try {
                imagePath = ImageUtil.saveImageToResources(selectedImageFile);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error al guardar la imagen: " + e.getMessage(), "Error de Imagen", JOptionPane.ERROR_MESSAGE);
                System.err.println("Error saving image: " + e.getMessage());
                return;
            }
        }

        Post newPost = new Post(
                currentThread.getId(),
                name,
                email,
                comment,
                imagePath
        );

        int newPostId = postDAO.createPost(newPost);

        if (newPostId != -1) {
            JOptionPane.showMessageDialog(this, "Comentario publicado con éxito!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            // Refresh thread content to show new post
            loadThreadContent();
            // Clear form
            nameField.setText("");
            emailField.setText("");
            commentArea.setText("");
            selectedImageLabel.setText("Ningún archivo seleccionado");
            selectedImageFile = null;
        } else {
            JOptionPane.showMessageDialog(this, "Error al publicar el comentario.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadThreadContent() {
        threadContentPanel.removeAll(); // Clear existing content

        // Add the original thread post
        addPostToDisplay(currentThread.getName(), currentThread.getEmail(), currentThread.getComment(), currentThread.getImagePath(), currentThread.getCreatedAt().format(DATE_FORMATTER), true);

        // Add separator for replies
        threadContentPanel.add(Box.createVerticalStrut(15));
        threadContentPanel.add(new JSeparator(SwingConstants.HORIZONTAL));
        threadContentPanel.add(Box.createVerticalStrut(15));

        // Load and add replies
        List<Post> posts = postDAO.getPostsByThreadId(currentThread.getId());
        if (posts.isEmpty()) {
            JLabel noPostsLabel = new JLabel("No hay comentarios en este hilo.");
            noPostsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            noPostsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            threadContentPanel.add(noPostsLabel);
        } else {
            for (Post post : posts) {
                addPostToDisplay(post.getName(), post.getEmail(), post.getComment(), post.getImagePath(), post.getCreatedAt().format(DATE_FORMATTER), false);
            }
        }

        threadContentPanel.revalidate();
        threadContentPanel.repaint();
    }

    private void addPostToDisplay(String author, String email, String comment, String imagePath, String timestamp, boolean isOriginalPost) {
        JPanel postPanel = new JPanel();
        postPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5)); // Smaller gaps for flow layout
        postPanel.setBorder(new LineBorder(Color.LIGHT_GRAY, 1)); // Lighter border for posts
        postPanel.setBackground(isOriginalPost ? new Color(255, 255, 240) : Color.WHITE); // Light yellow for OP
        postPanel.setAlignmentX(Component.LEFT_ALIGNMENT); // Align to left

        // Post Header (Name, Email, Timestamp)
        JPanel postHeader = new JPanel(new FlowLayout(FlowLayout.LEFT));
        postHeader.setBackground(isOriginalPost ? new Color(255, 255, 240) : Color.WHITE);
        postHeader.setAlignmentX(Component.LEFT_ALIGNMENT);


        JLabel nameLabel = new JLabel(author);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        nameLabel.setForeground(new Color(50, 100, 50)); // Greenish for name

        JLabel emailLabel = new JLabel(" &lt;" + email + "&gt;"); // Display email like 4chan
        emailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        emailLabel.setForeground(Color.BLUE);

        JLabel timestampLabel = new JLabel(" " + timestamp);
        timestampLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        timestampLabel.setForeground(Color.GRAY);

        postHeader.add(nameLabel);
        if (email != null && !email.trim().isEmpty()) {
            postHeader.add(emailLabel);
        }
        postHeader.add(timestampLabel);

        // Main content (Image + Comment)
        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setBackground(isOriginalPost ? new Color(255, 255, 240) : Color.WHITE);
        mainContent.setAlignmentX(Component.LEFT_ALIGNMENT);


        if (imagePath != null && !imagePath.isEmpty()) {
            try {
                ImageIcon resizedIcon = ImageUtil.getScaledImage(imagePath, 300, 300); // Larger image for full thread view
                JLabel imageLabel = new JLabel(resizedIcon);
                imageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                mainContent.add(imageLabel);
            } catch (Exception e) {
                System.err.println("Error loading post image: " + imagePath + " - " + e.getMessage());
                JLabel placeholder = new JLabel("[Imagen no disponible]");
                placeholder.setPreferredSize(new Dimension(300, 300));
                placeholder.setBorder(new LineBorder(Color.LIGHT_GRAY));
                placeholder.setAlignmentX(Component.LEFT_ALIGNMENT);
                mainContent.add(placeholder);
            }
        }

        JTextArea commentArea = new JTextArea(comment);
        commentArea.setEditable(false);
        commentArea.setLineWrap(true);
        commentArea.setWrapStyleWord(true);
        commentArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        commentArea.setBackground(isOriginalPost ? new Color(255, 255, 240) : Color.WHITE);
        commentArea.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0)); // Padding inside comment area
        commentArea.setPreferredSize(new Dimension(800, commentArea.getPreferredSize().height));
        commentArea.setMaximumSize(new Dimension(800, Integer.MAX_VALUE));
        commentArea.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainContent.add(commentArea);


        // Combine header and main content
        postPanel.add(postHeader, BorderLayout.NORTH);
        postPanel.add(mainContent, BorderLayout.CENTER);


        threadContentPanel.add(postPanel);
        threadContentPanel.add(Box.createVerticalStrut(10)); // Space between posts
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
            java.util.logging.Logger.getLogger(ThreadDetailView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(() -> {
            // For direct testing, create a dummy thread
            Thread dummyThread = new Thread(1, 1, "Hilo de Prueba", "Tester", "test@example.com",
                    "Este es un hilo de prueba para ver el contenido completo.", "/resource/sample_image1.jpg", java.time.LocalDateTime.now());
            new ThreadDetailView(dummyThread).setVisible(true);
        });
    }
}