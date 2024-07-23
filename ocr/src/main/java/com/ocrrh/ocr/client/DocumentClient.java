package com.ocrrh.ocr.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@FeignClient(name = "demo", url = "https://dcmlibrarieswin-ua-shp.itn.intraorange/sites/dev/_api/web/folders")
public interface DocumentClient {
    @GetMapping("/products")
    List<MultipartFile> getDocuments();

    @GetMapping("/products/{id}")
    MultipartFile getDocument(@PathVariable("id") int id);


}
