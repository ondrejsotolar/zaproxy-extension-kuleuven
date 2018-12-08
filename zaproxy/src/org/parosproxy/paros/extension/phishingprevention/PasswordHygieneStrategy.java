package org.parosproxy.paros.extension.phishingprevention;

public interface PasswordHygieneStrategy {
    String getName();

    String applyStrategy(String password);
}
