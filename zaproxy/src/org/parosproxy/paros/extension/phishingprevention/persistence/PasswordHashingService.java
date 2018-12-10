package org.parosproxy.paros.extension.phishingprevention.persistence;

public interface PasswordHashingService {
    String getHash(String password);
    boolean check(String password, String stored);
}
