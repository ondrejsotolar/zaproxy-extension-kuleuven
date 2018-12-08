package org.parosproxy.paros.extension.phishingprevention.requestscan;

import org.apache.log4j.Logger;
import org.parosproxy.paros.core.proxy.OverrideMessageProxyListener;
import org.parosproxy.paros.extension.phishingprevention.*;
import org.parosproxy.paros.extension.phishingprevention.html.ResponseHandler;
import org.parosproxy.paros.extension.phishingprevention.persistence.MemoryPersistenceService;
import org.parosproxy.paros.extension.phishingprevention.persistence.StoredCredentials;
import org.parosproxy.paros.network.HttpMessage;

public class OverrideListener implements OverrideMessageProxyListener {

    public final String CREDENTIALS_ALLOWED_KEYWORD = "save=true";
    public final String CANCEL_KEYWORD = "save=false";
    public final String REQUEST_ID = "request_id";
    public final String HOST_KEYWORD = "host_address";

    private CredentialScanerService credentialScannerService;
    private IPasswordHygieneService passwordHygieneService;
    private PersistenceService persistenceService;
    private RequestCache requestCache;
    private ResponseHandler responseHandler;

    private boolean ON = false;
    private boolean hygieneON = false;

    private static Logger log = Logger.getLogger(OverrideListener.class);

    public OverrideListener() {
        this.credentialScannerService = new RequestCredentialScannerService();
        this.passwordHygieneService = new PasswordHygieneService();
        this.persistenceService = new MemoryPersistenceService();
        this.requestCache = new RequestCache();
        this.responseHandler = new ResponseHandler();
    }

    public OverrideListener(
            CredentialScanerService credentialScannerService,
            IPasswordHygieneService passwordHygieneService,
            PersistenceService persistenceService) {
        this.credentialScannerService = credentialScannerService;
        this.passwordHygieneService = passwordHygieneService;
        this.persistenceService = persistenceService;
        this.requestCache = new RequestCache();
        this.responseHandler = new ResponseHandler();
    }

    @Override
    public boolean onHttpRequestSend(HttpMessage msg) {
        if (!ON) {
            return false;
        }

        String body = msg.getRequestBody().toString();
        if (body.contains(CREDENTIALS_ALLOWED_KEYWORD)) { // save & return the original request
            persistenceService.setHostWhitelisted(
                    credentialScannerService.getParamStringFromBody(body, HOST_KEYWORD), true);

            HttpMessage originalRequest = requestCache.getRequestById(
                    credentialScannerService.getParamIntFromBody(body, REQUEST_ID));
            msg.setRequestHeader(originalRequest.getRequestHeader());
            msg.setRequestBody(originalRequest.getRequestBody());
            return false;
        }
        else if (body.contains(CANCEL_KEYWORD)) {
            responseHandler.setResponseBodyForCancelPage(msg);
            persistenceService.remove(
                    credentialScannerService.getParamStringFromBody(body, HOST_KEYWORD));
            return false;
        }

        Credentials requestCredentials = credentialScannerService.getCredentialsInRequest(msg);
        if (requestCredentials == null) {
            return false;
        }
        StoredCredentials storedCredentials = persistenceService.get(requestCredentials.getHost());
        if (storedCredentials == null) { // new credentials -> return warning page
            persistenceService.saveOrUpdate(requestCredentials, false);

            PasswordHygieneResult hygieneResult = null;
            if (hygieneON) {
                hygieneResult = passwordHygieneService.checkPasswordHygiene(requestCredentials);
            }
            responseHandler.setResponseBodyContent(msg, requestCache.putRequestInCache(msg),
                    requestCredentials.getHost(), hygieneResult);

            log.info("ExtensionPhishingPrevention caught a request with credentials.");
            return true;
        }
        if (storedCredentials.isHostWhitelisted()) { // Allow only one credentials per host
            return false;
        }
        else {
            throw new IllegalStateException(
                    "ExtensionPhishingPrevention: false value in store for host: "
                            + storedCredentials.getHost());
        }
    }

    @Override
    public boolean onHttpResponseReceived(HttpMessage msg) {
        return true;
    }

    @Override
    public int getArrangeableListenerOrder() {
        return 0;
    }

    public void setHygieneON(boolean hygieneON) {
        this.hygieneON = hygieneON;
    }

    public void setON(boolean ON) {
        this.ON = ON;
    }

    public RequestCache getRequestCache() {
        return this.requestCache;
    }
}