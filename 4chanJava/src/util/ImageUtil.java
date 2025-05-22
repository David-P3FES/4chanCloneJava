package util;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID; // For unique file names

public class ImageUtil {

    private static final String RESOURCE_BASE_PATH = "src/resource/"; // Path where images are stored relative to project root
    private static final String DEFAULT_THUMBNAIL_PATH = "/resource/default_thumbnail.png"; // Default thumbnail path

    /**
     * Saves an image file to the application's resource directory.
     * Generates a unique filename to prevent overwrites.
     * Returns the relative path to the saved image (e.g., "/resource/unique_name.jpg").
     *
     * @param sourceFile The File object of the image to save.
     * @return Relative path string for database storage.
     * @throws IOException If there's an error saving the file.
     */
    public static String saveImageToResources(File sourceFile) throws IOException {
        // Ensure the resource directory exists
        Path resourceDir = Paths.get(RESOURCE_BASE_PATH);
        if (!Files.exists(resourceDir)) {
            Files.createDirectories(resourceDir);
        }

        String fileName = sourceFile.getName();
        String fileExtension = "";
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0) {
            fileExtension = fileName.substring(dotIndex);
        }

        // Generate a unique file name
        String uniqueFileName = UUID.randomUUID().toString() + fileExtension;
        Path destinationPath = resourceDir.resolve(uniqueFileName);

        Files.copy(sourceFile.toPath(), destinationPath, StandardCopyOption.REPLACE_EXISTING);

        // Return path relative to the classpath's resource folder for ImageIcon
        return "/resource/" + uniqueFileName;
    }

    /**
     * Loads an image from resources and scales it to the desired dimensions.
     *
     * @param imagePath The relative path to the image in resources (e.g., "/resource/myimage.jpg").
     * @param width     Desired width.
     * @param height    Desired height.
     * @return Scaled ImageIcon.
     * @throws IOException If the image cannot be loaded.
     */
    public static ImageIcon getScaledImage(String imagePath, int width, int height) throws IOException {
        Image originalImage = null;
        try {
            // Attempt to load from resources first (for packaged app)
            originalImage = ImageIO.read(ImageUtil.class.getResource(imagePath));
        } catch (IllegalArgumentException e) {
            // Fallback to file system if not found in resources (for development)
            File imgFile = new File(System.getProperty("user.dir") + imagePath.replace("/", File.separator));
            if (imgFile.exists()) {
                originalImage = ImageIO.read(imgFile);
            } else {
                throw new IOException("Image not found: " + imagePath);
            }
        }


        if (originalImage == null) {
            // Fallback to default thumbnail if specific image is not found
            System.err.println("Original image not found or loaded, using default thumbnail: " + imagePath);
            originalImage = ImageIO.read(ImageUtil.class.getResource(DEFAULT_THUMBNAIL_PATH));
            if (originalImage == null) {
                 throw new IOException("Default thumbnail also not found!");
            }
        }

        Image scaledImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImage);
    }
}