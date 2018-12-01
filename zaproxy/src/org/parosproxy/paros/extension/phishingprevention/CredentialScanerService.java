package org.parosproxy.paros.extension.phishingprevention;

import org.parosproxy.paros.network.HttpMessage;

public interface CredentialScanerService {

    Credentials getCredentialsInRequest(HttpMessage message);
}
