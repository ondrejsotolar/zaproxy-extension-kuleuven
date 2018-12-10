package org.parosproxy.paros.extension.phishingprevention.persistence;


public class SimpleHashingService implements PasswordHashingService {
    @Override
    public String hash(String password) {
        return password;
    }
}
