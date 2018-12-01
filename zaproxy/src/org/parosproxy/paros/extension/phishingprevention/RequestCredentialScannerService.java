package org.parosproxy.paros.extension.phishingprevention;

import org.parosproxy.paros.network.HttpMessage;

public class RequestCredentialScannerService implements CredentialScanerService {
    @Override
    public Credentials getCredentialsInRequest(HttpMessage message) {
        // TODO: implement
        return null;
    }
}
