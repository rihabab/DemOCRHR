package com.ocrrh.ocr.services;

import com.ocrrh.ocr.entites.Employee;
import com.ocrrh.ocr.findingOwner.FrenchNamedEntityRecognition;
import com.ocrrh.ocr.dataReading.PDFTextExtractor;
import com.ocrrh.ocr.repositories.EmployeeRepository;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.TesseractException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Slf4j
@Service
public class EmployeeService {

    private static final Logger log = LoggerFactory.getLogger(EmployeeService.class);
    @Autowired
    EmployeeRepository erepo ;

    public Employee createEmployee(Employee emp){
        return erepo.save(emp);
    }

    public Employee getEmployee(String cuid){
        return erepo.findByCuid(cuid);
    }
    public List<Employee> getAll(){
        return erepo.findAll();
    }
    public Employee updateEmployee(Long Id, Employee emp){
        if (erepo.existsById(Id)) {
            emp.setId(Id);
            return erepo.save(emp);
        } else {
            return null;
        }
    }

    public boolean deleteEmployee(Long Id){
        if(erepo.existsById(Id)){
            erepo.deleteById(Id);
            return true;
        } else {
            return false;
        }
    }
    public String processPdf(MultipartFile file) throws IOException {

        InputStream inputStream = file.getInputStream();

        PDDocument document = PDDocument.load(inputStream);
        String fileName = file.getOriginalFilename();

        try {

            String text = PDFTextExtractor.extractTextFromPDF(document);
            log.info(text);
            FrenchNamedEntityRecognition ner = new FrenchNamedEntityRecognition();
            List<String> entities = ner.identifyEntities(text);
            List<String> entitiesr = ner.cleaningEntities(entities);
            List entitiespro = ner.entitiesPrepare(entitiesr);
            for (String entity : entitiesr) {
                log.info("Found PERSON entity: ");
                log.info(entity);

            }
            log.info(entitiespro.toString());
        } catch (TesseractException e) {
            throw new RuntimeException(e);
        } finally {

            document.close();
        }

        return "File uploaded successfully: " + fileName;
    }



}
