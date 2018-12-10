package org.parosproxy.paros.extension.phishingprevention;

import org.parosproxy.paros.network.HttpMessage;

public interface CredentialScanerService {

    Credentials getCredentialsInRequest(HttpMessage message);

    int getParamIntFromBody(String body, String paramName);

    String getParamStringFromBody(String body, String paramName);

    boolean getParamBoolFromBody(String body, String paramName);
}
