package org.vrk.accounting.service;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;

@Service
public class OcrService {
    private final ITesseract tesseract;

    public OcrService(ITesseract tesseract) {
        this.tesseract = tesseract;
    }

    /**
     * Распознаёт текст из любого BufferedImage и возвращает только цифры.
     */
    public String extractDigits(BufferedImage image) throws TesseractException {
        // Непосредственно OCR из BufferedImage
        String rawText = tesseract.doOCR(image);
        // Оставляем только цифры
        return rawText.replaceAll("\\D+", "");
    }
}
