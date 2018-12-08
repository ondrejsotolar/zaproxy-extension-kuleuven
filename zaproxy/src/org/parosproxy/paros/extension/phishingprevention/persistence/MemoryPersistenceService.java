package org.parosproxy.paros.extension.phishingprevention.persistence;

import org.parosproxy.paros.extension.phishingprevention.Credentials;
import org.parosproxy.paros.extension.phishingprevention.PersistenceService;

import java.util.HashMap;
import java.util.Map;

public class MemoryPersistenceService implements PersistenceService {

    Map<String, StoredCredentials> store = new HashMap<>();

    @Override
    public StoredCredentials get(String host) {
        return store.get(host);
    }

    @Override
    public void saveOrUpdate(Credentials credentials, boolean allow) {
        StoredCredentials stored = store.get(credentials.getHost());
        if (stored == null) {
            store.put(credentials.getHost(), new StoredCredentials(credentials, allow));
        } else {
            stored.setHostWhitelisted(allow);
        }
    }

    @Override
    public void setHostWhitelisted(String host, boolean allow) {
        StoredCredentials stored = store.get(host);
        if (stored == null) {
            throw new RuntimeException("MemoryPersistenceService: not found: " + host);
        } else {
            stored.setHostWhitelisted(allow);
        }
    }

    public void remove(String host) {
        store.remove(host);
    }
}
