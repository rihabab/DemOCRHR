package com.ocrrh.ocr.services;

import com.ocrrh.ocr.dataReading.PDFTextExtractor;
import com.ocrrh.ocr.docClassification.DocClassifier;
import com.ocrrh.ocr.exceptions.DocumentNotClassifiedException;
import com.ocrrh.ocr.exceptions.NoNameMachedException;
import com.ocrrh.ocr.findingOwner.FrenchNamedEntityRecognition;
import com.ocrrh.ocr.findingOwner.dataMapping;
import com.ocrrh.ocr.repositories.HRdocumentRepository;
import net.sourceforge.tess4j.TesseractException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class HRdocumentService {
    private static final Logger log = LoggerFactory.getLogger(EmployeeService.class);
    @Autowired
    private HRdocumentRepository docRepo;
    List<String> dbNames=new ArrayList<>(Arrays.asList("AIT,BAHESSOU,Rihab","test,test","chahchouh,Abdellah"));
    public String processPdf(MultipartFile file) throws IOException {
        InputStream inputStream = file.getInputStream();
        PDDocument document = PDDocument.load(inputStream);
        String fileName ;

        try {

            String text = PDFTextExtractor.extractTextFromPDF(document);
            log.info(text);
            FrenchNamedEntityRecognition ner = new FrenchNamedEntityRecognition();
            List<String> entities = ner.identifyEntities(text);
            List<String> entitiesr = ner.cleaningEntities(entities);
            List entitiespro = ner.entitiesPrepare(entitiesr);
            fileName= DocClassifier.findStringsInText(text);
            for (String entity : entitiesr) {
                log.info("Found PERSON entity: ");
                log.info(entity);

            }

            log.info(entitiespro.toString());
            log.info(dataMapping.findMatchingNames(entitiespro,dbNames).toString());
        } catch (TesseractException e) {
            throw new RuntimeException(e);
        } catch (DocumentNotClassifiedException e) {
            fileName = file.getOriginalFilename();
        } catch (NoNameMachedException e) {
            throw new RuntimeException(e);
        } finally {

            document.close();
        }

        return "File uploaded successfully: " + fileName;
    }
    public String processImage(MultipartFile file) throws IOException {
        InputStream inputStream = file.getInputStream();
        BufferedImage bufferedImage = ImageIO.read(inputStream);
        String fileName;

        try {

            String text = PDFTextExtractor.extractTextFromImage(bufferedImage);
            log.info(text);
            log.info("process done --------------------------------------");
            FrenchNamedEntityRecognition ner = new FrenchNamedEntityRecognition();
            List<String> entities = ner.identifyEntities(text);
            List<String> entitiesr = ner.cleaningEntities(entities);
            List entitiespro = ner.entitiesPrepare(entitiesr);
            fileName= DocClassifier.findStringsInText(text);
            for (String entity : entitiesr) {
                log.info("Found PERSON entity: ");
                log.info(entity);

            }
            log.info(entitiespro.toString());

            log.info(dataMapping.findMatchingNames(entitiespro,dbNames).toString());
        } catch (TesseractException e) {
            throw new RuntimeException(e);
        } catch (DocumentNotClassifiedException e) {
            fileName = file.getOriginalFilename();
        } catch (NoNameMachedException e) {
            throw new RuntimeException(e);
        } finally {
            inputStream.close();
        }

        return "File uploaded successfully: " + fileName;
    }

}
