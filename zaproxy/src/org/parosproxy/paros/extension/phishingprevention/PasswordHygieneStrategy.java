package org.parosproxy.paros.extension.phishingprevention;

public interface PasswordHygieneStrategy {
    String getName();

    String getMessage();

    boolean applyStrategy(String password);
}
