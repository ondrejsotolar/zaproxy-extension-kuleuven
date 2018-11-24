package org.parosproxy.paros.extension.typosquatter;

import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.URIException;
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
import java.util.List;

public class ExtensionTyposquatter extends ExtensionAdaptor implements ProxyListener {

    public static final int PROXY_LISTENER_ORDER = 0;
    public static final String NAME = "ExtensionTyposquatter";
    public static final String ADD_TO_WHITELIST_KEYWORD = "?save=true";

    private boolean ON = false;
    private ZapMenuItem menuToolsFilter = null;
    private TyposquattingService typosquattingService;
    private PersistanceService persistanceService;
    private Path pathToWhitelist;

    public ExtensionTyposquatter() {
        super();
        setOrder(777);
        persistanceService = new TextFilePersistence();
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
        String uri = msg.getRequestHeader().getURI().toString();
        String candidate = msg.getRequestHeader().getHostName();

        if (uri.contains(ADD_TO_WHITELIST_KEYWORD)) {
            persistanceService.persistToWhitelist(candidate, this.pathToWhitelist);
            typosquattingService.setWhiteList(
                    persistanceService.parseWhitelistFile(this.pathToWhitelist.toFile()));
            removeSaveCommandFromRequest(msg);
            return true;
        }

        if (typosquattingService.checkCandidateHost(candidate).getResult()) {
            throw new RuntimeException("ExtensionTyposquatter caught a typo.");
        }
        return true;
    }

    // TODO: respect protocol (http vs https)
    @Override
    public boolean onHttpResponseReceive(HttpMessage msg) {
        if (!ON) {
            return true;
        }
        String candidate = msg.getRequestHeader().getHostName();
        TyposquattingResult res = typosquattingService.checkCandidateHost(candidate);
        if (res.getResult()) {
            setResponseBodyContent(msg, res.getCandidate());
        }

        return true;
    }

    public void setResponseBodyContent(HttpMessage msg, String host) {
        ResultPage resultPage = new ResultPage();
        msg.setResponseBody(resultPage.getBody(host));

        try {
            msg.setResponseHeader(resultPage.getHeader());
        } catch (HttpMalformedHeaderException e) {
            e.printStackTrace();
        }
        msg.getResponseHeader().setContentLength(msg.getResponseBody().length());
    }

    public void removeSaveCommandFromRequest(HttpMessage msg) {
        String uri = msg.getRequestHeader().getURI().toString();
        try {
            msg.getRequestHeader().setURI(
                    new URI(uri.substring(0, uri.indexOf(ADD_TO_WHITELIST_KEYWORD))));
        } catch (URIException e) {
            e.printStackTrace();
        }
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
}
