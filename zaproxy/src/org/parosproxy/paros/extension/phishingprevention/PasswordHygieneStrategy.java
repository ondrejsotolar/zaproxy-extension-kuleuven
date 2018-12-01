package org.parosproxy.paros.extension.phishingprevention;

public interface PasswordHygieneStrategy {
    String getName();

    boolean applyStrategy(String password);
}
