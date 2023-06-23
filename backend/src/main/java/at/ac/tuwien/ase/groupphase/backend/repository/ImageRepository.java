package at.ac.tuwien.ase.groupphase.backend.repository;

import at.ac.tuwien.ase.groupphase.backend.service.SubmissionService;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Repository
public class ImageRepository {

    private static final String IMAGE_FORMAT = "png";
    private static final int MAX_WIDTH_HEIGHT = 1000;
    private static final String IMAGE_PATH = "src/main/resources/static/img/";

    public void save(MultipartFile file, UUID uuid) {
        try {
            InputStream inputStream = file.getInputStream();
            BufferedImage originalImage = ImageIO.read(inputStream); // javax.imageio has built-in support for GIF, PNG,
            // JPEG, BMP, and WBMP
            inputStream.close();

            int[] correctWidthHeight = widthHeightCorrectAspectRatio(originalImage);
            BufferedImage resizedImage = resizeImage(originalImage, correctWidthHeight[0], correctWidthHeight[1]);

            File f = new File(getPath(uuid).toUri());

            ImageIO.write(resizedImage, IMAGE_FORMAT, f);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(UUID uuid) {
        try {
            Files.delete(getPath(uuid));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Path getPath(UUID uuid) {
        return Paths.get(IMAGE_PATH, uuid + "." + IMAGE_FORMAT);
    }

    /**
     * Calculates the width and height with the correct aspect ratio.
     *
     * @param img
     *            the img
     *
     * @return res array: width=res[0] height=res[1]
     */
    private static int[] widthHeightCorrectAspectRatio(BufferedImage img) {
        int[] res = { img.getWidth(), img.getHeight() };
        if (img.getWidth() > MAX_WIDTH_HEIGHT || img.getHeight() > MAX_WIDTH_HEIGHT) {
            if (img.getWidth() >= img.getHeight()) {
                res[0] = MAX_WIDTH_HEIGHT;
                res[1] = (int) (((0.0 + img.getHeight()) / img.getWidth()) * MAX_WIDTH_HEIGHT);
            } else {
                res[0] = (int) (((0.0 + img.getWidth()) / img.getHeight()) * MAX_WIDTH_HEIGHT);
                res[1] = MAX_WIDTH_HEIGHT;
            }
        }
        return res;
    }

    /**
     * Resize BufferedImage.
     *
     * @param originalImage
     *            the original image
     * @param targetWidth
     *            the target width
     * @param targetHeight
     *            the target height
     *
     * @return the resized buffered image
     */
    private static BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2D = resizedImage.createGraphics();
        graphics2D.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        graphics2D.dispose();
        return resizedImage;
    }
}
