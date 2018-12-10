package org.parosproxy.paros.extension.phishingprevention.persistence;

import org.parosproxy.paros.extension.phishingprevention.Credentials;

public class StoredCredentials extends Credentials {

    private boolean isHostWhitelisted;
    private boolean isHygieneWhitelisted;

    public StoredCredentials(Credentials credentials, boolean whitelistHost, boolean whitelistHygiene) {
        setHost(credentials.getHost());
        setUsername(credentials.getUsername());
        setPassword(credentials.getPassword());
        this.isHostWhitelisted = whitelistHost;
        this.isHygieneWhitelisted = whitelistHygiene;
    }

    public boolean isHostWhitelisted() {
        return isHostWhitelisted;
    }

    public void setHostWhitelisted(boolean allow) {
        this.isHostWhitelisted = allow;
    }

    public boolean isHygieneWhitelisted() {
        return isHygieneWhitelisted;
    }

    public void setHygieneWhitelisted(boolean hygieneWhitelisted) {
        isHygieneWhitelisted = hygieneWhitelisted;
    }
}
