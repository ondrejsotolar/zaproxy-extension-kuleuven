package org.parosproxy.paros.extension.phishingprevention.persistence;

import org.parosproxy.paros.extension.phishingprevention.Credentials;

public class StoredCredentials extends Credentials {

    private boolean allow;

    public StoredCredentials(Credentials credentials, boolean allow) {
        setHost(credentials.getHost());
        setUsername(credentials.getUsername());
        setPassword(credentials.getPassword());
        this.allow = allow;
    }

    public boolean isAllow() {
        return allow;
    }

    public void setAllow(boolean allow) {
        this.allow = allow;
    }


}
