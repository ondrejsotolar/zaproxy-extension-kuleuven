package org.parosproxy.paros.extension.phishingprevention;

import org.parosproxy.paros.extension.typosquatter.ExtensionTyposquatter;
import org.parosproxy.paros.network.HttpMessage;

public class ExtensionPhishingPrevention extends ExtensionTyposquatter {

    public CredentialScanerService credentialScannerService = new RequestCredentialScannerService();

    private String securityKey = null;

    @Override
    public boolean onHttpRequestSend(HttpMessage msg) {
        if (!ON) {
            return true;
        }

        // if (isControlRequest(msg)) {
        //      // TODO: set response body
        //      return true;
        // }

        Credentials credentials = credentialScannerService.getCredentialsInRequest(msg);
        if (credentials == null) {
            return true;
        }

        // HygieneCheckResult hygieneCheckResult = passwordHygieneService.checkPassword(credentials);
        // if (hygieneCheckResult.getResult()) {
        //      // TODO: store hygiene result
        //      // TODO: set response body to warning page
        //      return false;
        // }

        // store request with id

        throw new PhishingPreventionException("PhishingPreventionExtension found credentials.");
    }

    @Override
    public boolean onHttpResponseReceive(HttpMessage msg) {
        return true;
    }

    @Override
    public void setON(boolean ON) {
        super.setON(ON);
        this.securityKey = ""; // TODO: get security key
    }
}
