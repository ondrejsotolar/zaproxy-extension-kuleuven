package org.parosproxy.paros.extension.typosquatter;

import org.parosproxy.paros.Constant;
import org.parosproxy.paros.core.proxy.ProxyListener;
import org.parosproxy.paros.extension.ExtensionAdaptor;
import org.parosproxy.paros.extension.ExtensionHook;
import org.parosproxy.paros.extension.ViewDelegate;
import org.parosproxy.paros.extension.filter.ExtensionFilter;
import org.parosproxy.paros.extension.filter.Filter;
import org.parosproxy.paros.extension.filter.FilterDialog;
import org.parosproxy.paros.model.Model;
import org.parosproxy.paros.network.HttpMessage;
import org.zaproxy.zap.view.ZapMenuItem;

import java.util.List;

public class ExtensionTyposquatter extends ExtensionAdaptor implements ProxyListener {

    public static final int PROXY_LISTENER_ORDER = 0;
    public static final String NAME = "ExtensionTyposquatter";
    private TimerFilterThread timerFilterThread;

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
                    boolean startThread = true;
//                    FilterDialog dialog = new FilterDialog(getView().getMainFrame());
//                    dialog.setAllFilters(filterFactory.getAllFilter());
//                    dialog.showDialog(false);
//
//                    boolean startThread = false;
//                    for (Filter filter : filterFactory.getAllFilter()) {
//                        if (filter.isEnabled()) {
//                            startThread = true;
//                            break;
//                        }
//                    }

                    if (startThread) {
                        if (timerFilterThread == null) {
                            timerFilterThread = new TimerFilterThread();
                            timerFilterThread.start();
                        }
                    } else if (timerFilterThread != null) {
                        timerFilterThread.setStopped();
                        timerFilterThread = null;
                    }
                }
            });

        }
        return menuToolsFilter;
    }

    @Override
    public boolean onHttpRequestSend(HttpMessage msg) {
        return false;
    }

    @Override
    public boolean onHttpResponseReceive(HttpMessage msg) {
        return false;
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

    /**
     * Destroy every filter during extension destroy.
     */
    @Override
    public void destroy() {
        if (timerFilterThread != null) {
            timerFilterThread.setStopped();
            timerFilterThread = null;
        }
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

    static class TimerFilterThread extends Thread {

        private boolean stop;

        public TimerFilterThread() {
            super("ZAP-ExtensionTyposquatter");
            setDaemon(true);
        }

        @Override
        public void run() {
            while (!stop) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e1) {
                }
            }
        }

        public void setStopped() {
            this.stop = true;
        }
    }
}
