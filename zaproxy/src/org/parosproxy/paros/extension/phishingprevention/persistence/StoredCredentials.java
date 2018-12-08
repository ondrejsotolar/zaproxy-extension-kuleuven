package org.parosproxy.paros.extension.phishingprevention.persistence;

import org.parosproxy.paros.extension.phishingprevention.Credentials;

public class StoredCredentials extends Credentials {

    private boolean isHostWhitelisted;

    public StoredCredentials(Credentials credentials, boolean allow) {
        setHost(credentials.getHost());
        setUsername(credentials.getUsername());
        setPassword(credentials.getPassword());
        this.isHostWhitelisted = allow;
    }

    public boolean isHostWhitelisted() {
        return isHostWhitelisted;
    }

    public void setHostWhitelisted(boolean allow) {
        this.isHostWhitelisted = allow;
    }


}
