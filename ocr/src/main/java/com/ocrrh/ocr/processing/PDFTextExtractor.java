package com.ocrrh.ocr.processing;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Core;
//import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class PDFTextExtractor {
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
            //tesseract.setOcrEngineMode(ITesseract.OEM_LSTM_ONLY);
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

    public static BufferedImage preprocessImage(BufferedImage image) {
        Mat src = bufferedImageToMat(image);
        Mat gray = new Mat(src.rows(), src.cols(), CvType.CV_8UC1);
        Imgproc.cvtColor(src, gray, Imgproc.COLOR_RGB2GRAY);
        Mat binary = new Mat();
        Imgproc.threshold(gray, binary, 0, 255, Imgproc.THRESH_BINARY | Imgproc.THRESH_OTSU);
        return matToBufferedImage(binary);
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
