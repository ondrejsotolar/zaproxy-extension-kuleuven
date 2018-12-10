package org.parosproxy.paros.extension.phishingprevention.requestscan;

import org.apache.log4j.Logger;
import org.parosproxy.paros.core.proxy.OverrideMessageProxyListener;
import org.parosproxy.paros.extension.phishingprevention.*;
import org.parosproxy.paros.extension.phishingprevention.html.ResponseHandler;
import org.parosproxy.paros.extension.phishingprevention.persistence.MemoryPersistenceService;
import org.parosproxy.paros.extension.phishingprevention.persistence.StoredCredentials;
import org.parosproxy.paros.network.HttpMessage;

public class OverrideListener implements OverrideMessageProxyListener {

    public final String IS_CONTROL_REQUEST_KEYWORD = "phishing_prevention_cr=true";
    public final String CREDENTIALS_ALLOWED_KEYWORD = "save=true";
    public final String HYGIENE_IGNORED_KEYWORD = "ignore_hygiene=true";
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

    // TODO: move handlers to separate classes
    @Override
    public boolean onHttpRequestSend(HttpMessage msg) {
        if (!ON) {
            return false;
        }

        if (isControlRequest(msg.getRequestBody().toString())) {
            if (isControlRequestHandled(msg))
                return false;
        }

        Credentials requestCredentials = credentialScannerService.getCredentialsInRequest(msg);
        if (isPlainRequest(requestCredentials)) {
            return false;
        }

        StoredCredentials storedCredentials = persistenceService.get(
                requestCredentials.getHost(),
                requestCredentials.getUsername()
        );
        if (isUnhandledCredentials(storedCredentials)) {
            if (isUnhandledCredentialsHandled(requestCredentials, storedCredentials, msg))
                return true;
        }

        if (isCorrectStoredCredentials(storedCredentials)) {
            handlePasswordUpdate(requestCredentials, storedCredentials);
            return false;
        }
        else {
            throw new IllegalStateException(
                    "ExtensionPhishingPrevention: illegal value in store for: " + storedCredentials.getHost());
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

    private boolean isControlRequest(String body) {
        return body.contains(IS_CONTROL_REQUEST_KEYWORD);
    }

    private boolean isPlainRequest(Credentials requestCredentials) {
        return requestCredentials == null;
    }

    private boolean isUnhandledCredentials(StoredCredentials storedCredentials) {
        return storedCredentials == null
                || !storedCredentials.isHostWhitelisted()
                || (!storedCredentials.isHygieneWhitelisted() && hygieneON);
    }

    private boolean isCorrectStoredCredentials(StoredCredentials storedCredentials) {
     return storedCredentials.isHostWhitelisted()
             && (storedCredentials.isHygieneWhitelisted() || !hygieneON);
    }

    private void handlePasswordUpdate(Credentials requestCredentials, StoredCredentials storedCredentials) {
        boolean isSame = persistenceService.getPasswordHashingService()
                .check(requestCredentials.getPassword(), storedCredentials.getPassword());

        if (!isSame) {
            persistenceService.saveOrUpdate(
                    requestCredentials,
                    storedCredentials.isHostWhitelisted(),
                    storedCredentials.isHygieneWhitelisted());
        }
    }

    private boolean isUnhandledCredentialsHandled(Credentials requestCredentials, StoredCredentials storedCredentials, HttpMessage msg) {
        if (storedCredentials == null) {
            persistenceService.saveOrUpdate(requestCredentials, false, false);
        }

        PasswordHygieneResult hygieneResult = null;
        if (hygieneON) {
            hygieneResult = passwordHygieneService.checkPasswordHygiene(requestCredentials);
        }
        responseHandler.setResponseBodyContent(
                msg,
                requestCache.putRequestInCache(msg),
                requestCredentials.getHost(),
                hygieneResult);

        log.info("ExtensionPhishingPrevention caught a request with credentials.");
        return true;
    }

    private boolean isControlRequestHandled(HttpMessage msg) {
        String body = msg.getRequestBody().toString();

        if (body.contains(CREDENTIALS_ALLOWED_KEYWORD)) { // save & return the original request
            HttpMessage originalRequest = requestCache.getRequestById(
                    credentialScannerService.getParamIntFromBody(body, REQUEST_ID));
            msg.setRequestHeader(originalRequest.getRequestHeader());
            msg.setRequestBody(originalRequest.getRequestBody());

            Credentials requestCredentials = credentialScannerService.getCredentialsInRequest(originalRequest);
            boolean hygiene = credentialScannerService.getParamBoolFromBody(body, HYGIENE_IGNORED_KEYWORD);
            persistenceService.saveOrUpdate(requestCredentials, true, hygiene);

            return true;
        }
        else if (body.contains(CANCEL_KEYWORD)) {
            responseHandler.setResponseBodyForCancelPage(msg);

            HttpMessage originalRequest = requestCache.getRequestById(
                    credentialScannerService.getParamIntFromBody(body, REQUEST_ID));

            Credentials requestCredentials = credentialScannerService.getCredentialsInRequest(originalRequest);
            persistenceService.remove(requestCredentials.getHost(), requestCredentials.getUsername());

            // TODO: remove the request from cache

            return true;
        }
        return false;
    }
}