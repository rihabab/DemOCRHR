package com.ocrrh.ocr.exceptions;

public class NoNameMachedException extends Exception{
    public NoNameMachedException(){ super("the document does not match any of our employees");}
}
