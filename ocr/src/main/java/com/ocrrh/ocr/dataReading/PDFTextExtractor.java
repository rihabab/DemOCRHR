package com.ocrrh.ocr.dataReading;

import java.awt.*;
import java.awt.image.*;

import java.io.IOException;

import com.ocrrh.ocr.services.EmployeeService;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Core;

import org.opencv.imgproc.Imgproc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PDFTextExtractor {
    private static final Logger log = LoggerFactory.getLogger(EmployeeService.class);

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static String extractTextFromPDF(PDDocument document) throws TesseractException {
        StringBuilder extractedText = new StringBuilder();

            PDFRenderer pdfRenderer = new PDFRenderer(document);
            ITesseract tesseract = new Tesseract();
            tesseract.setDatapath("C:\\Users\\TFRY2424\\AppData\\Local\\Programs\\Tesseract-OCR\\tessdata");
            tesseract.setLanguage("fra"); // Set French language
            tesseract.setVariable("user_defined_dpi", "300");

            tesseract.setPageSegMode(6); // Assume a single uniform block of text

            for (int page = 0; page < document.getNumberOfPages(); page++) {
                BufferedImage image = null;
                try {
                    image = pdfRenderer.renderImageWithDPI(page, 300);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                BufferedImage processedImage = preprocessImage(image);
                String text = tesseract.doOCR(processedImage);
                extractedText.append(text);
            }

        return extractedText.toString();
    }


    public static String extractTextFromImage(BufferedImage image) throws TesseractException, IOException {
        ITesseract tesseract = new Tesseract();
        tesseract.setDatapath("C:\\Users\\TFRY2424\\AppData\\Local\\Programs\\Tesseract-OCR\\tessdata");
        tesseract.setLanguage("fra"); // Set French language
        tesseract.setVariable("user_defined_dpi", "300");
        tesseract.setPageSegMode(6);

        BufferedImage processedImage = preprocessImage(image);
        String text = tesseract.doOCR(processedImage);
        return text;
    }

    private static BufferedImage preprocessImage(BufferedImage image) {
        // Convert to grayscale
        BufferedImage grayscaleImage = new BufferedImage(
                image.getWidth(), image.getHeight(),
                BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g = grayscaleImage.createGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();

        // Binarize the image
        BufferedImage binarizedImage = new BufferedImage(
                grayscaleImage.getWidth(), grayscaleImage.getHeight(),
                BufferedImage.TYPE_BYTE_BINARY);
        Graphics2D g2 = binarizedImage.createGraphics();
        g2.drawImage(grayscaleImage, 0, 0, null);
        g2.dispose();

        // Apply Gaussian blur
        float[] matrix = {
                1f / 16f, 1f / 8f, 1f / 16f,
                1f / 8f, 1f / 4f, 1f / 8f,
                1f / 16f, 1f / 8f, 1f / 16f,
        };
        BufferedImage blurredImage = new BufferedImage(
                binarizedImage.getWidth(), binarizedImage.getHeight(),
                binarizedImage.getType());
        new ConvolveOp(new Kernel(3, 3, matrix)).filter(binarizedImage, blurredImage);

        // Optional: Resize if needed
        int newWidth = blurredImage.getWidth() * 2;
        int newHeight = blurredImage.getHeight() * 2;
        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, blurredImage.getType());
        Graphics2D g3 = resizedImage.createGraphics();
        g3.drawImage(blurredImage, 0, 0, newWidth, newHeight, null);
        g3.dispose();

        return resizedImage;
    }

    public static Mat bufferedImageToMat(BufferedImage bi) {
        DataBuffer dataBuffer = bi.getRaster().getDataBuffer();
        if (dataBuffer instanceof DataBufferByte) {
            byte[] data = ((DataBufferByte) dataBuffer).getData();
            Mat mat = new Mat(bi.getHeight(), bi.getWidth(), CvType.CV_8UC3);
            mat.put(0, 0, data);
            return mat;
        } else if (dataBuffer instanceof DataBufferInt) {
            int[] data = ((DataBufferInt) dataBuffer).getData();
            byte[] dataBytes = new byte[data.length * 4];
            for (int i = 0; i < data.length; i++) {
                dataBytes[i * 4] = (byte) ((data[i] >> 16) & 0xFF);
                dataBytes[i * 4 + 1] = (byte) ((data[i] >> 8) & 0xFF);
                dataBytes[i * 4 + 2] = (byte) (data[i] & 0xFF);
                dataBytes[i * 4 + 3] = (byte) ((data[i] >> 24) & 0xFF);
            }
            Mat mat = new Mat(bi.getHeight(), bi.getWidth(), CvType.CV_8UC4);
            mat.put(0, 0, dataBytes);
            return mat;
        } else {
            throw new IllegalArgumentException("Unsupported image type: " + bi.getType());
        }
    }

    public static BufferedImage matToBufferedImage(Mat matrix) {
        int cols = matrix.cols();
        int rows = matrix.rows();
        int elemSize = (int) matrix.elemSize();
        byte[] data = new byte[cols * rows * elemSize];
        int type;
        matrix.get(0, 0, data);
        switch (matrix.channels()) {
            case 1:
                type = BufferedImage.TYPE_BYTE_GRAY;
                break;
            case 3:
                type = BufferedImage.TYPE_3BYTE_BGR;
                byte b;
                for (int i = 0; i < data.length; i = i + 3) {
                    b = data[i];
                    data[i] = data[i + 2];
                    data[i + 2] = b;
                }
                break;
            default:
                return null;
        }
        BufferedImage image = new BufferedImage(cols, rows, type);
        image.getRaster().setDataElements(0, 0, cols, rows, data);
        return image;
    }



}
