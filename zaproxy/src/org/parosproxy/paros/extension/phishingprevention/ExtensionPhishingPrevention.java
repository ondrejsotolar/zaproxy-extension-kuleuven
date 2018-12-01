package org.parosproxy.paros.extension.phishingprevention;

import org.parosproxy.paros.core.proxy.ProxyListener;
import org.parosproxy.paros.extension.ExtensionAdaptor;
import org.parosproxy.paros.extension.typosquatter.ExtensionTyposquatter;
import org.parosproxy.paros.extension.typosquatter.ITyposquattingService;
import org.parosproxy.paros.extension.typosquatter.PersistanceService;
import org.parosproxy.paros.network.HttpMessage;

public class ExtensionPhishingPrevention extends ExtensionAdaptor implements ProxyListener {

    public CredentialScanerService credentialScannerService;
    public IPasswordHygieneService passwordHygieneService;

    protected boolean ON = false;

    private String securityKey = null;

    public ExtensionPhishingPrevention() {
        super();
        setOrder(778);
        this.credentialScannerService = new RequestCredentialScannerService();
        this.passwordHygieneService = new PasswordHygieneService();
    }

    public ExtensionPhishingPrevention(CredentialScanerService credentialScannerService,
                                       IPasswordHygieneService passwordHygieneService) {
        super();
        setOrder(778);
        this.credentialScannerService = credentialScannerService;
        this.passwordHygieneService = passwordHygieneService;
    }

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

         PasswordHygieneResult hygieneCheckResult = passwordHygieneService.checkPasswordHygiene(credentials);
         if (hygieneCheckResult.getResult()) {
              // TODO: store hygiene result
              // TODO: set response body to warning page
             throw new PhishingPreventionException("PhishingPreventionExtension found credentials.");
         }

        // store request with id

        return true;
    }

    @Override
    public boolean onHttpResponseReceive(HttpMessage msg) {
        return true;
    }

    public void setON(boolean ON) {
        this.ON = ON;
        this.securityKey = ""; // TODO: get security key
    }

    public boolean isON() {
        return ON;
    }

    @Override
    public int getArrangeableListenerOrder() {
        return 0;
    }

    @Override
    public String getAuthor() {
        return null;
    }
}
