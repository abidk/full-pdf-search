package abid.fullpdfsearch.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
public class PdfTextExtractor {

    public String extract(File file) {
        try (PDDocument document = PDDocument.load(file)) {
            final PDFTextStripper textStripper = new PDFTextStripper();
            final String text = textStripper.getText(document);
            return text;
        } catch (IOException e) {
            throw new RuntimeException("Failed to read: " + file.getName(), e);
        }
    }
}
