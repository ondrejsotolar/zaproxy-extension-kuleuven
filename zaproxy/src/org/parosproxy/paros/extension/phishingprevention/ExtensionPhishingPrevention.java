package org.parosproxy.paros.extension.phishingprevention;

import org.apache.log4j.Logger;
import org.parosproxy.paros.core.proxy.ProxyListener;
import org.parosproxy.paros.extension.ExtensionAdaptor;
import org.parosproxy.paros.extension.ExtensionHook;
import org.parosproxy.paros.extension.ViewDelegate;
import org.parosproxy.paros.extension.phishingprevention.html.CancelPage;
import org.parosproxy.paros.extension.phishingprevention.html.WarningPage;
import org.parosproxy.paros.extension.phishingprevention.persistence.FilePersistenceService;
import org.parosproxy.paros.extension.phishingprevention.persistence.StoredCredentials;
import org.parosproxy.paros.model.Model;
import org.parosproxy.paros.network.HttpMalformedHeaderException;
import org.parosproxy.paros.network.HttpMessage;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class ExtensionPhishingPrevention extends ExtensionAdaptor implements ProxyListener {

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
        this.persistenceService = new FilePersistenceService();
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

    @Override
    public boolean onHttpRequestSend(HttpMessage msg) {
        if (!ON) {
            return true;
        }
        String body = msg.getRequestBody().toString();
        if (body.contains(ADD_TO_WHITELIST_KEYWORD)) {
            persistenceService.setAllowed(getParamStringFromBody(body, HOST_KEYWORD), true);

            // Creds are allowed by user & saved => return the original request
            HttpMessage originalRequest = getRequestById(getParamIntFromBody(body, REQUEST_ID));
            msg.setRequestHeader(originalRequest.getRequestHeader());
            msg.setRequestBody(originalRequest.getRequestBody());
            return true;
        }
        else if (body.contains(CANCEL_KEYWORD)) {
            setResponseBodyForCancelPage(msg);
            persistenceService.remove(getParamStringFromBody(body, HOST_KEYWORD));
            return true;
        }

        Credentials requestCredentials = credentialScannerService.getCredentialsInRequest(msg);
        if (requestCredentials == null) {
            return true;
        }
        StoredCredentials storedCredentials = persistenceService.get(requestCredentials.getHost());
        if (storedCredentials == null) { // CONTROL REQUEST: new credentials
            persistenceService.saveOrUpdate(requestCredentials, false);
            setResponseBodyContent(msg, putRequestInCache(msg), requestCredentials.getHost());

            log.info("ExtensionPhishingPrevention caught a request with credentials.");
            return true; // TODO: implement control requests and eventually return false
        }
        if (storedCredentials.isAllow()) {
//            if (!storedCredentials.equals(requestCredentials)) {
//                persistenceService.saveOrUpdate(requestCredentials, true);
//            } // Allow only one cred for host
            return true;
        }
        else {
            throw new RuntimeException(
                    "ExtensionPhishingPrevention: false in store: " + storedCredentials.getHost());
        }
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
        extensionHook.addProxyListener(this);
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

    /*
    makeRequest("http://control.request");
        if (host.equals("control.request")) {
            return true;
        }
        return false;
     */
    private void makeRequest(String address){
        String urlString = address;
        String charset = "UTF-8";

        // TODO: get settings from config: use OptionsChangedListener
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 8081));

        try {
            URLConnection connection = new URL(urlString).openConnection(proxy);
            connection.setRequestProperty("Accept-Charset", charset);
            InputStream response = connection.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }


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
}
