package org.parosproxy.paros.extension.phishingprevention;

import org.parosproxy.paros.extension.phishingprevention.persistence.StoredCredentials;

public interface PersistenceService {

    StoredCredentials get(String host);

    void saveOrUpdate(Credentials credentials, boolean allow);

    void setHostWhitelisted(String host, boolean allow);

    void remove(String host);
}
