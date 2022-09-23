package com.thoughtworks.otr.snconnector.exception;

public class TrelloException extends RuntimeException{
    public TrelloException(String message) {
        super(message);
    }

    public TrelloException(String message, Throwable cause) {
        super(message, cause);
    }
}
