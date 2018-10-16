package me.exrates.exception;

public class InvoiceActionIsProhibitedForNotHolderException extends RuntimeException {
    public InvoiceActionIsProhibitedForNotHolderException(){
        super();
    }

    public InvoiceActionIsProhibitedForNotHolderException(String string){
        super(string);
    }
}
