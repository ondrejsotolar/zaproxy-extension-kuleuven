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
import org.parosproxy.paros.network.HttpRequestHeader;
import org.zaproxy.zap.view.ZapMenuItem;

import java.net.MalformedURLException;

public class ExtensionTyposquatter extends ExtensionAdaptor implements ProxyListener {

    public static final int PROXY_LISTENER_ORDER = 0;
    public static final String NAME = "ExtensionTyposquatter";

    private boolean ON = false;
    private ZapMenuItem menuToolsFilter = null;

    public ExtensionTyposquatter() {
        super();
        setOrder(777);
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
            menuToolsFilter.addActionListener(new java.awt.event.ActionListener() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    ON = !ON;
                }
            });

        }
        return menuToolsFilter;
    }

    public boolean isTypoInUrl(HttpMessage msg) {
        String host = msg.getRequestHeader().getHostName();

        // TODO: use actual whitelist
        return host.contains(".");
    }

    @Override
    public boolean onHttpRequestSend(HttpMessage msg) {
        if (!ON) {
            return true;
        }
        if (isTypoInUrl(msg)) {
            throw new RuntimeException("ExtensionTyposquatter caught a typo.");
        }
        return true;
    }

    // TODO: page with proceed button
    public void setResponseBodyContent(HttpMessage msg) {
        msg.setResponseBody("<!doctype html><html lang=\"en\"><head><meta charset=\"utf-8\"><title>Blocked by proxy</title></head><body><H1>Blocked by proxy!</H1></body></html>");
        try {
            msg.setResponseHeader("HTTP/1.1 200 OK\r\nContent-Type: text/html; charset=utf-8");
        } catch (HttpMalformedHeaderException e) {
            e.printStackTrace();
        }
        msg.getResponseHeader().setContentLength(msg.getResponseBody().length());
    }

    @Override
    public boolean onHttpResponseReceive(HttpMessage msg) {
        if (!ON) {
            return true;
        }
        if (isTypoInUrl(msg)) {
            setResponseBodyContent(msg);
        }
        return true;
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
}
