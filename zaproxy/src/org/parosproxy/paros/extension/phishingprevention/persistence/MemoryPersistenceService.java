package org.parosproxy.paros.extension.phishingprevention.persistence;

import org.parosproxy.paros.extension.phishingprevention.Credentials;
import org.parosproxy.paros.extension.phishingprevention.PersistenceService;

import java.util.*;

public class MemoryPersistenceService implements PersistenceService {

    List<StoredCredentials> store = new ArrayList<>();
    PasswordHashingService hashingService = new SimpleHashingService();

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
        StoredCredentials newRecord = new StoredCredentials(credentials, whitelistHost, ignoreHygiene);
        newRecord.hashPassword(hashingService);
        store.add(newRecord);
    }

    @Override
    public void remove(String host, String username) {
        StoredCredentials stored = get(host, username);
        if (stored != null) {
            store.remove(stored);
        }
    }

//    @Override
//    public PasswordHashingService getPasswordHashingService() {
//        return this.hashingService;
//    }

    @Override
    public void updatePassword(Credentials requestCredentials, StoredCredentials storedCredentials) {
        boolean isSame = hashingService
                .check(requestCredentials.getPassword(), storedCredentials.getPassword());

        if (!isSame) {
            saveOrUpdate(
                    requestCredentials,
                    storedCredentials.isHostWhitelisted(),
                    storedCredentials.isHygieneWhitelisted());
        }
    }
}
