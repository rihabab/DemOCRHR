package com.ocrrh.ocr.exceptions;

public class DocumentNotClassifiedException extends Exception {
    public DocumentNotClassifiedException(){
        super("File was no classified ! No identifier found");
    }
}
