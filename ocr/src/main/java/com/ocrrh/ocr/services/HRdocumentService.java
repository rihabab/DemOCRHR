package com.ocrrh.ocr.services;

import com.ocrrh.ocr.repositories.HRdocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HRdocumentService {
    @Autowired
    private HRdocumentRepository docRepo;


}
