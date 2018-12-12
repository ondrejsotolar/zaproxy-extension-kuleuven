package org.parosproxy.paros.extension.phishingprevention.hygiene;

public interface PasswordHygieneStrategy {
    String getName();

    String applyStrategy(String password);
}
