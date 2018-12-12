package org.parosproxy.paros.extension.phishingprevention;

import org.parosproxy.paros.extension.ExtensionAdaptor;
import org.parosproxy.paros.extension.ExtensionHook;
import org.parosproxy.paros.extension.OptionsChangedListener;
import org.parosproxy.paros.extension.ViewDelegate;
import org.parosproxy.paros.extension.phishingprevention.optionsdialog.OptionsDialog;
import org.parosproxy.paros.extension.phishingprevention.optionsdialog.PhishingPreventionParam;
import org.parosproxy.paros.model.Model;
import org.parosproxy.paros.model.OptionsParam;
import org.parosproxy.paros.view.View;

public class ExtensionPhishingPrevention extends ExtensionAdaptor implements OptionsChangedListener {

    public final String NAME = "ExtensionPhishingPrevention";

    private PhishingPreventionParam phishingPreventionParam;
    private OptionsDialog optionsDialog;
    private OverrideListener overrideListener;

    public ExtensionPhishingPrevention() {
        super();
        setOrder(778);
        overrideListener = new OverrideListener();
    }

    @Override
    public void init() {
        this.setName(NAME);
    }

    @Override
    public void initModel(Model model) { super.initModel(model); }

    @Override
    public void initView(ViewDelegate view) {
        super.initView(view);
    }

    @Override
    public void hook(ExtensionHook extensionHook) {
        super.hook(extensionHook);
        extensionHook.addOverrideMessageProxyListener(overrideListener);
        extensionHook.addOptionsParamSet(getPhishingPreventionParam());
        extensionHook.addOptionsChangedListener(this);
        if (View.isInitialised()) {
            extensionHook.getHookView().addOptionPanel(
                    getOptionsDialog());
        }
    }

    @Override
    public String getAuthor() {
        return "ondrej.sotolar@gmail.com";
    }

    @Override
    public String getUIName() {
        return "ExtensionPhishingPrevention extension name";
    }

    /**
     * No database tables used, so all supported
     */
    @Override
    public boolean supportsDb(String type) {
        return true;
    }

    @Override
    public void optionsChanged(OptionsParam optionsParam) {
        overrideListener.setON(this.getPhishingPreventionParam().isSecure());
        overrideListener.setHygieneON(this.getPhishingPreventionParam().isHygiene());
    }

    private PhishingPreventionParam getPhishingPreventionParam() {
        if (this.phishingPreventionParam == null) {
            this.phishingPreventionParam = new PhishingPreventionParam();
        }
        return this.phishingPreventionParam;
    }

    private OptionsDialog getOptionsDialog() {
        if (optionsDialog == null) {
            optionsDialog = new OptionsDialog(this);
        }
        return optionsDialog;
    }
}
