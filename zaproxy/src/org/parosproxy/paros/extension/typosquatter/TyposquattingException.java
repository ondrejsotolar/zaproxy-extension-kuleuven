package org.parosproxy.paros.extension.typosquatter;

public class TyposquattingException extends RuntimeException {

    public TyposquattingException(String message) {
        super(message);
    }
}
