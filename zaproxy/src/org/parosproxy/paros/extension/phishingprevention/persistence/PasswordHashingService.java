package org.parosproxy.paros.extension.phishingprevention.persistence;

public interface PasswordHashingService {
    String hash(String password);
}
