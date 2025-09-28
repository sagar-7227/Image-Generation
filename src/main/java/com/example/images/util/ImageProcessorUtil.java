package com.example.images.util;

import com.example.images.dto.TransformationRequest;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class ImageProcessorUtil {

    public static int[] getImageDimensions(String filePath) {
        try {
            BufferedImage img = ImageIO.read(new File(filePath));
            if (img == null) throw new IOException("Invalid image file");
            return new int[]{img.getWidth(), img.getHeight()};
        } catch (IOException e) {
            throw new RuntimeException("Failed to get dimensions: " + e.getMessage());
        }
    }

    public static Path applyTransformations(Path originalPath, TransformationRequest.Transformations t) throws IOException {
        BufferedImage original = ImageIO.read(originalPath.toFile());
        if (original == null) throw new IOException("Invalid image file");

        BufferedImage processed = original;

        // Resize
        if (t.getResize() != null) {
            processed = resize(processed, t.getResize().getWidth(), t.getResize().getHeight());
        }

        // Crop
        if (t.getCrop() != null) {
            processed = crop(processed, t.getCrop().getX(), t.getCrop().getY(), t.getCrop().getWidth(), t.getCrop().getHeight());
        }

        // Rotate
        if (t.getRotate() != null) {
            processed = rotate(processed, t.getRotate());
        }

        // Flip
        if (Boolean.TRUE.equals(t.getFlip())) {
            processed = flipHorizontal(processed);
        }

        // Mirror
        if (Boolean.TRUE.equals(t.getMirror())) {
            processed = mirrorVertical(processed);
        }

        // Filters
        if (t.getFilters() != null) {
            if (Boolean.TRUE.equals(t.getFilters().getGrayscale())) {
                processed = applyGrayscale(processed);
            }
            if (Boolean.TRUE.equals(t.getFilters().getSepia())) {
                processed = applySepia(processed);
            }
        }

        // Watermark
        if (t.getWatermark() != null) {
            processed = addWatermark(processed, t.getWatermark());
        }

        // Output format
        String format = t.getFormat() != null ? t.getFormat() : "jpg";

        // Create output filename safely
        String originalName = originalPath.getFileName().toString();
        int dotIndex = originalName.lastIndexOf('.');
        String baseName = (dotIndex != -1) ? originalName.substring(0, dotIndex) : originalName;
        String transformedName = baseName + "_transformed." + format;

        Path transformedPath = originalPath.resolveSibling(transformedName);
        File outputFile = transformedPath.toFile();

        ImageIO.write(processed, format, outputFile);

        return transformedPath;
    }

    private static BufferedImage resize(BufferedImage img, int width, int height) {
        BufferedImage resized = new BufferedImage(width, height, img.getType() == 0 ? BufferedImage.TYPE_INT_RGB : img.getType());
        Graphics2D g2d = resized.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(img, 0, 0, width, height, null);
        g2d.dispose();
        return resized;
    }

    private static BufferedImage crop(BufferedImage img, int x, int y, int width, int height) {
        return img.getSubimage(x, y, width, height);
    }

    private static BufferedImage rotate(BufferedImage img, int degrees) {
        double rads = Math.toRadians(degrees);
        int newW = (int) (Math.abs(img.getWidth() * Math.cos(rads)) + Math.abs(img.getHeight() * Math.sin(rads)));
        int newH = (int) (Math.abs(img.getWidth() * Math.sin(rads)) + Math.abs(img.getHeight() * Math.cos(rads)));

        BufferedImage rotated = new BufferedImage(newW, newH, img.getType() == 0 ? BufferedImage.TYPE_INT_RGB : img.getType());
        Graphics2D g2d = rotated.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.rotate(rads, newW / 2.0, newH / 2.0);
        g2d.translate((newW - img.getWidth()) / 2.0, (newH - img.getHeight()) / 2.0);
        g2d.drawImage(img, 0, 0, null);
        g2d.dispose();
        return rotated;
    }

    private static BufferedImage applyGrayscale(BufferedImage img) {
        BufferedImage gray = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g = gray.createGraphics();
        g.drawImage(img, 0, 0, null);
        g.dispose();
        return gray;
    }

    private static BufferedImage applySepia(BufferedImage img) {
        BufferedImage sepia = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                Color color = new Color(img.getRGB(x, y));
                int r = color.getRed();
                int g = color.getGreen();
                int b = color.getBlue();

                int tr = (int)(0.393 * r + 0.769 * g + 0.189 * b);
                int tg = (int)(0.349 * r + 0.686 * g + 0.168 * b);
                int tb = (int)(0.272 * r + 0.534 * g + 0.131 * b);

                r = Math.min(255, tr);
                g = Math.min(255, tg);
                b = Math.min(255, tb);

                sepia.setRGB(x, y, new Color(r, g, b).getRGB());
            }
        }
        return sepia;
    }

    private static BufferedImage addWatermark(BufferedImage img, String text) {
        Graphics2D g2d = img.createGraphics();
        AlphaComposite alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f);
        g2d.setComposite(alpha);
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 30));
        FontMetrics fm = g2d.getFontMetrics();
        int x = img.getWidth() - fm.stringWidth(text) - 20;
        int y = img.getHeight() - 20;
        g2d.drawString(text, x, y);
        g2d.dispose();
        return img;
    }

    private static BufferedImage flipHorizontal(BufferedImage img) {
        BufferedImage flipped = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
        Graphics2D g2d = flipped.createGraphics();
        g2d.drawImage(img, 0, 0, img.getWidth(), img.getHeight(), img.getWidth(), 0, 0, img.getHeight(), null);
        g2d.dispose();
        return flipped;
    }

    private static BufferedImage mirrorVertical(BufferedImage img) {
        BufferedImage flipped = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
        Graphics2D g2d = flipped.createGraphics();
        g2d.drawImage(img, 0, 0, img.getWidth(), img.getHeight(), 0, img.getHeight(), img.getWidth(), 0, null);
        g2d.dispose();
        return flipped;
    }
}