package org.parosproxy.paros.extension.phishingprevention;

import org.apache.log4j.Logger;
import org.parosproxy.paros.core.proxy.OverrideMessageProxyListener;
import org.parosproxy.paros.extension.ExtensionAdaptor;
import org.parosproxy.paros.extension.ExtensionHook;
import org.parosproxy.paros.extension.ViewDelegate;
import org.parosproxy.paros.extension.phishingprevention.html.CancelPage;
import org.parosproxy.paros.extension.phishingprevention.html.WarningPage;
import org.parosproxy.paros.extension.phishingprevention.persistence.MemoryPersistenceService;
import org.parosproxy.paros.extension.phishingprevention.persistence.StoredCredentials;
import org.parosproxy.paros.model.Model;
import org.parosproxy.paros.network.HttpMalformedHeaderException;
import org.parosproxy.paros.network.HttpMessage;

import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class ExtensionPhishingPrevention extends ExtensionAdaptor {

    public CredentialScanerService credentialScannerService;
    public IPasswordHygieneService passwordHygieneService;
    public PersistenceService persistenceService;

    public final int PROXY_LISTENER_ORDER = 0;
    public final String NAME = "ExtensionPhishingPrevention";
    public final String ADD_TO_WHITELIST_KEYWORD = "save=true"; // TODO: add extension ID to recognize by other extensions
    public final String CANCEL_KEYWORD = "save=false";
    public final String REQUEST_ID = "request_id";
    public final String HOST_KEYWORD = "host_address";

    protected boolean ON = true;
    protected boolean hygieneON = false;

    private String securityKey = null;
    private static Logger log = Logger.getLogger(ExtensionPhishingPrevention.class);

    private ConcurrentHashMap<HttpMessage, Integer> requestCache;
    private int requestCounter = 0;
    private Path pathToWhitelist;

    public ExtensionPhishingPrevention() {
        super();
        setOrder(778);
        this.credentialScannerService = new RequestCredentialScannerService();
        this.passwordHygieneService = new PasswordHygieneService();
        this.persistenceService = new MemoryPersistenceService();
        requestCache = new ConcurrentHashMap<>();
    }

    public ExtensionPhishingPrevention(CredentialScanerService credentialScannerService,
                                       IPasswordHygieneService passwordHygieneService,
                                       PersistenceService persistenceService) {
        super();
        setOrder(778);
        this.credentialScannerService = credentialScannerService;
        this.passwordHygieneService = passwordHygieneService;
        this.persistenceService = persistenceService;
        requestCache = new ConcurrentHashMap<>();
    }

    // TODO: remove localhost from form
    public void setResponseBodyContent(HttpMessage msg, int requestId, String host) {
        WarningPage warningPage = new WarningPage();
        msg.setResponseBody(warningPage.getBody(requestId, host));

        try {
            msg.setResponseHeader(warningPage.getHeader());
        } catch (HttpMalformedHeaderException e) {
            e.printStackTrace();
        }
        msg.getResponseHeader().setContentLength(msg.getResponseBody().length());
    }

    public void setResponseBodyForCancelPage(HttpMessage msg) {
        CancelPage cancelPage = new CancelPage();
        msg.setResponseBody(cancelPage.getBody());

        try {
            msg.setResponseHeader(cancelPage.getHeader());
        } catch (HttpMalformedHeaderException e) {
            e.printStackTrace();
        }
        msg.getResponseHeader().setContentLength(msg.getResponseBody().length());
    }

    public void setON(boolean ON) {
        this.ON = ON;
        this.securityKey = ""; // TODO: get security key
    }

    public boolean isON() {
        return ON;
    }

    @Override
    public String getAuthor() {
        return null;
    }

    @Override
    public String getUIName() {
        return "ExtensionPhishingPrevention extension name";
    }

    @Override
    public void init() {
        this.setName(NAME);
    }

    @Override
    public void initModel(Model model) {
        // ZAP: changed to init(Model)
        super.initModel(model);
    }

    @Override
    public void initView(ViewDelegate view) {
        super.initView(view);
    }

    @Override
    public void hook(ExtensionHook extensionHook) {
        super.hook(extensionHook);
//        if (getView() != null) {
//            extensionHook.getHookMenu().addToolsMenuItem(getMenuToolsFilter());
//        }
        extensionHook.addOverrideMessageProxyListener(getNewOverrideListener());
    }

    /**
     * No database tables used, so all supported
     */
    @Override
    public boolean supportsDb(String type) {
        return true;
    }

    private int putRequestInCache(HttpMessage message) {
        if (requestCounter >= Integer.MAX_VALUE - 1) {
            requestCounter = 0;
            this.requestCache.clear(); // primitive cache size management
        }
        this.requestCache.put(message, requestCounter);
        return this.requestCounter++;
    }

    // TODO: put into a base class
    public HttpMessage getRequestById(int id) {
        HttpMessage originalRequest = this.requestCache.entrySet()
                .stream()
                .filter(entry -> Objects.equals(entry.getValue(), id))
                .map(Map.Entry::getKey)
                .findFirst()
                .get();
        return originalRequest;
    }

    public int getParamIntFromBody(String body, String paramName) {
        String param = getParamStringFromBody(body, paramName);
        if (param == null) {
            return -1;
        }
        int requestId = Integer.parseInt(param.substring(param.indexOf("=")+1));
        return requestId;
    }

    public String getParamStringFromBody(String body, String paramName) {
        int beginIndex = body.indexOf(paramName);
        if (beginIndex < 0) {
            return null;
        }
        int endIndex = body.indexOf("&", beginIndex);
        String requestIdParam = (endIndex >= 0)
                ? body.substring(beginIndex, endIndex)
                : body.substring(beginIndex);

        return requestIdParam.substring(requestIdParam.indexOf("=")+1);
    }

    public OverrideMessageProxyListener getNewOverrideListener() {
        return new OverrideListener();
    }

    public ConcurrentHashMap<HttpMessage, Integer> getRequestCache() {
        return requestCache;
    }

    public int getRequestCounter() {
        return requestCounter;
    }

    private class OverrideListener implements OverrideMessageProxyListener {

        @Override
        public boolean onHttpRequestSend(HttpMessage msg) {
            String body = msg.getRequestBody().toString();
            if (body.contains(ADD_TO_WHITELIST_KEYWORD)) {
                persistenceService.setAllowed(getParamStringFromBody(body, HOST_KEYWORD), true);

                // Creds are allowed by user & saved => return the original request
                HttpMessage originalRequest = getRequestById(getParamIntFromBody(body, REQUEST_ID));
                msg.setRequestHeader(originalRequest.getRequestHeader());
                msg.setRequestBody(originalRequest.getRequestBody());
                return false;
            }
            else if (body.contains(CANCEL_KEYWORD)) {
                setResponseBodyForCancelPage(msg);
                persistenceService.remove(getParamStringFromBody(body, HOST_KEYWORD));
                return false;
            }

            Credentials requestCredentials = credentialScannerService.getCredentialsInRequest(msg);
            if (requestCredentials == null) {
                return false;
            }
            StoredCredentials storedCredentials = persistenceService.get(requestCredentials.getHost());
            if (storedCredentials == null) { // CONTROL REQUEST: new credentials
                persistenceService.saveOrUpdate(requestCredentials, false);
                setResponseBodyContent(msg, putRequestInCache(msg), requestCredentials.getHost());

                log.info("ExtensionPhishingPrevention caught a request with credentials.");
                return true;
            }
            if (storedCredentials.isAllow()) { // Allow only one credentials per host
                return false;
            }
            else {
                throw new RuntimeException(
                        "ExtensionPhishingPrevention: false in store: " + storedCredentials.getHost());
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
    }
}
