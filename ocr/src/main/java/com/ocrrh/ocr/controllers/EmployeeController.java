package com.ocrrh.ocr.controllers;


import com.ocrrh.ocr.entites.Employee;

import com.ocrrh.ocr.services.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @PostMapping
    private Employee createEmployee(@RequestBody Employee emp){

        return employeeService.createEmployee(emp);

    }


    @GetMapping
    private List<Employee> getAll(){
        return employeeService.getAll();
    }

    @PutMapping("/{id}")
    private Employee updateEmployee(@PathVariable Long id, @RequestBody Employee emp){
        return employeeService.updateEmployee(id,emp);
    }

    @DeleteMapping("/{id}")
    private void deleteEmployee(@PathVariable Long id){
        employeeService.deleteEmployee(id);
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadPdf(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty() || !file.getContentType().equals("application/pdf")) {
            return new ResponseEntity<>("Invalid file. Please upload a PDF file.", HttpStatus.BAD_REQUEST);
        }

        try {
            // Process the PDF file (save it, read it, etc.)
            String message = employeeService.processPdf(file);
            return new ResponseEntity<>(message, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>("Error processing the file", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
