package com.daimler.otr.operationtools.exception;

public class TrelloException extends RuntimeException{
    public TrelloException(String message) {
        super(message);
    }

    public TrelloException(String message, Throwable cause) {
        super(message, cause);
    }
}
