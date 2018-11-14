package org.parosproxy.paros.extension.typosquatter;

import org.parosproxy.paros.core.proxy.ProxyListener;
import org.parosproxy.paros.extension.ExtensionAdaptor;
import org.parosproxy.paros.extension.ExtensionHook;
import org.parosproxy.paros.extension.ViewDelegate;
import org.parosproxy.paros.model.Model;
import org.parosproxy.paros.network.HttpMalformedHeaderException;
import org.parosproxy.paros.network.HttpMessage;
import org.zaproxy.zap.view.ZapMenuItem;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class ExtensionTyposquatter extends ExtensionAdaptor implements ProxyListener {

    public static final int PROXY_LISTENER_ORDER = 0;
    public static final String NAME = "ExtensionTyposquatter";

    private boolean ON = false;
    private ZapMenuItem menuToolsFilter = null;
    private TyposquattingService typosquattingService;

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
                    if (!ON) {
                        List<String> whitelist = getWhiteList();
                        if (whitelist == null) {
                            return; // dialog closed or malformed file
                        }
                        typosquattingService = new TyposquattingService(whitelist);
                    }
                    ON = !ON;
                }
            });

        }
        return menuToolsFilter;
    }

    public boolean isTypoInUrl(HttpMessage msg) {
        String candidate = msg.getRequestHeader().getHostName();

        TyposquattingResult result = typosquattingService.checkCandidateHost(candidate);
        return result.getResult();
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

    // TODO: handle malformed file
    public List<String> getWhiteList() {
        File whitelistFile = getWhitelistFile();
        if (whitelistFile == null) {
            return null; // Dialog closed
        }

        return parseWhitelistFile(whitelistFile);
    }

    public File getWhitelistFile() {
        JFileChooser chooser = new JFileChooser(getModel().getOptionsParam().getUserDirectory());
        File file = null;

        int rc = chooser.showSaveDialog(getView().getMainFrame());
        if(rc == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile();
        }
        return file;
    }

    // TODO: handle malformed file
    public List<String> parseWhitelistFile(File file) {
        List<String> whitelist = new ArrayList<>();
        if (file == null) {
            return whitelist;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                whitelist.add(line);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return whitelist;
    }
}
