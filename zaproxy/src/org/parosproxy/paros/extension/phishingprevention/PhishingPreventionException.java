package org.parosproxy.paros.extension.phishingprevention;

public class PhishingPreventionException extends RuntimeException {
    public PhishingPreventionException() {
    }

    public PhishingPreventionException(String message) {
        super(message);
    }
}
