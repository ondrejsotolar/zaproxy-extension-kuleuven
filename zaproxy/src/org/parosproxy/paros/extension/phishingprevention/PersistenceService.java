package org.parosproxy.paros.extension.phishingprevention;

import org.parosproxy.paros.extension.phishingprevention.persistence.PasswordHashingService;
import org.parosproxy.paros.extension.phishingprevention.persistence.StoredCredentials;

public interface PersistenceService {

    StoredCredentials get(String host, String username);

    void saveOrUpdate(Credentials credentials, boolean whitelistHost, boolean ignoreHygiene);

    void remove(String host, String username);

    PasswordHashingService getPasswordHashingService();
}
