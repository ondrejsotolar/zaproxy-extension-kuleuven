package org.parosproxy.paros.extension.typosquatter;

import org.parosproxy.paros.core.proxy.ProxyListener;
import org.parosproxy.paros.extension.ExtensionAdaptor;
import org.parosproxy.paros.extension.ExtensionHook;
import org.parosproxy.paros.extension.ViewDelegate;
import org.parosproxy.paros.model.Model;
import org.parosproxy.paros.network.HttpMalformedHeaderException;
import org.parosproxy.paros.network.HttpMessage;
import org.zaproxy.zap.view.ZapMenuItem;

import java.io.File;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class ExtensionTyposquatter extends ExtensionAdaptor implements ProxyListener {

    public static final int PROXY_LISTENER_ORDER = 0;
    public static final String NAME = "ExtensionTyposquatter";
    public static final String ADD_TO_WHITELIST_KEYWORD = "save=true";

    private boolean ON = false;
    private ZapMenuItem menuToolsFilter = null;
    private ITyposquattingService typosquattingService;
    private PersistanceService persistanceService;
    private Path pathToWhitelist;



    private ConcurrentHashMap<HttpMessage, Integer> requestCache;
    private int requestCounter = 0;

    public ExtensionTyposquatter() {
        super();
        setOrder(777);
        persistanceService = new TextFilePersistence();
        requestCache = new ConcurrentHashMap<>();
    }

    public ExtensionTyposquatter(ITyposquattingService typosquattingService,
                                 PersistanceService persistanceService) {
        super();
        setOrder(777);
        this.persistanceService = persistanceService;
        this.typosquattingService = typosquattingService;
        requestCache = new ConcurrentHashMap<>();
    }

    @Override
    public String getUIName() {
        return "Typosquatter extension name";
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

    /**
     * This method initializes menuToolsFilter
     *
     * @return javax.swing.JMenuItem
     */
    private ZapMenuItem getMenuToolsFilter() {
        if (menuToolsFilter == null) {
            menuToolsFilter = new ZapMenuItem("menu.tools.typosquatter");

            Model model = getModel();
            ViewDelegate view = getView();
            menuToolsFilter.addActionListener(e -> {
                if (!ON) {
                    File file = persistanceService.getWhitelistFile(model, view);
                    this.pathToWhitelist = file.toPath();
                    List<String> whiteList = persistanceService.getWhiteList(file);
                    if (whiteList == null) {
                        return; // dialog closed or malformed file
                    }
                    typosquattingService = new TyposquattingService(whiteList);
                }
                ON = !ON;
            });

        }
        return menuToolsFilter;
    }

    @Override
    public boolean onHttpRequestSend(HttpMessage msg) {
        if (!ON) {
            return true;
        }
        String candidate = msg.getRequestHeader().getHostName();
        String body = msg.getRequestBody().toString();

        if (body.contains(ADD_TO_WHITELIST_KEYWORD)) {
            persistanceService.persistToWhitelist(candidate, this.pathToWhitelist);
            typosquattingService.setWhiteList(
                    persistanceService.parseWhitelistFile(this.pathToWhitelist.toFile()));

            HttpMessage originalRequest = getRequestById(getRequestIdFromUri(body));
            msg.setRequestHeader(originalRequest.getRequestHeader());
            msg.setRequestBody(originalRequest.getRequestBody());

            return true;
        }

        if (typosquattingService.checkCandidateHost(candidate).getResult()) {
            putRequestInCache(msg);
            throw new TyposquattingException("ExtensionTyposquatter caught a typo.");
        }
        return true;
    }

    @Override
    public boolean onHttpResponseReceive(HttpMessage msg) {
        if (!ON) {
            return true;
        }
        String candidate = msg.getRequestHeader().getHostName();
        TyposquattingResult res = typosquattingService.checkCandidateHost(candidate);

        if (res.getResult()) {
            setResponseBodyContent(msg, candidate, this.requestCache.get(msg),
                    res.getFailedStrategyNames());
        }

        return true;
    }

    public void setResponseBodyContent(HttpMessage msg, String host, int requestId,
                                       Collection<String> failedStrategyNames) {
        ResultPage resultPage = new ResultPage();
        msg.setResponseBody(resultPage.getBody(host, requestId, failedStrategyNames));

        try {
            msg.setResponseHeader(resultPage.getHeader());
        } catch (HttpMalformedHeaderException e) {
            e.printStackTrace();
        }
        msg.getResponseHeader().setContentLength(msg.getResponseBody().length());
    }

    public HttpMessage getRequestById(int id) {
        HttpMessage originalRequest = this.requestCache.entrySet()
                .stream()
                .filter(entry -> Objects.equals(entry.getValue(), id))
                .map(Map.Entry::getKey)
                .findFirst()
                .get();
        return originalRequest;
    }

    public int getRequestIdFromUri(String uri) {
        String requestIdParam = uri.substring(uri.indexOf("requestId"));
        int requestId = Integer.parseInt(requestIdParam.substring(requestIdParam.indexOf("=")+1));
        return requestId;
    }

    @Override
    public void hook(ExtensionHook extensionHook) {
        super.hook(extensionHook);
        if (getView() != null) {
            extensionHook.getHookMenu().addToolsMenuItem(getMenuToolsFilter());
        }
        extensionHook.addProxyListener(this);
    }

    @Override
    public int getArrangeableListenerOrder() {
        return PROXY_LISTENER_ORDER;
    }

    @Override
    public String getAuthor() {
        return "ondrej.sotolar@gmail.com";
    }

    /**
     * No database tables used, so all supported
     */
    @Override
    public boolean supportsDb(String type) {
        return true;
    }

    public boolean isON() {
        return ON;
    }

    public void setON(boolean ON) {
        this.ON = ON;
    }

    public ConcurrentHashMap<HttpMessage, Integer> getRequestCache() {
        return requestCache;
    }

    public int getRequestCounter() {
        return requestCounter;
    }

    private void putRequestInCache(HttpMessage message) {
        if (requestCounter >= Integer.MAX_VALUE - 1) {
            requestCounter = 0;
            this.requestCache.clear(); // primitive cache size management
        }
        this.requestCache.put(message, requestCounter++);
    }
}
