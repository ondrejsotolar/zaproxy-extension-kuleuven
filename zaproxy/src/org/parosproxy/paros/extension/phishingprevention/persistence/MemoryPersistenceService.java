package org.parosproxy.paros.extension.phishingprevention.persistence;

import org.parosproxy.paros.extension.phishingprevention.Credentials;
import org.parosproxy.paros.extension.phishingprevention.PersistenceService;

import java.util.*;

public class MemoryPersistenceService implements PersistenceService {

    List<StoredCredentials> store = new ArrayList<>();

    @Override
    public StoredCredentials get(String host, String username) {

        Optional<StoredCredentials> found = this.store
                .stream()
                .filter(sc -> host.equals(sc.getHost()) && username.equals(sc.getUsername()))
                .findFirst();

        StoredCredentials result = found.isPresent()
                ? found.get()
                : null;

        return result;
    }

    @Override
    public void saveOrUpdate(Credentials credentials, boolean whitelistHost, boolean ignoreHygiene) {
        StoredCredentials stored = get(credentials.getHost(), credentials.getUsername());
        if (stored != null) {
            store.remove(stored);
        }
        store.add(new StoredCredentials(credentials, whitelistHost, ignoreHygiene));
    }

    @Override
    public void remove(String host, String username) {
        StoredCredentials stored = get(host, username);
        if (stored != null) {
            store.remove(stored);
        }
    }
}
