package org.parosproxy.paros.extension.phishingprevention.optionsdialog;

import org.parosproxy.paros.common.AbstractParam;

public class PhishingPreventionParam extends AbstractParam {

    private static final String PROXY_BASE_KEY = "phishingprevention";

    private static final String SECURE_KEY = PROXY_BASE_KEY + ".secure";
    private static final String HYGIENE_KEY = PROXY_BASE_KEY + ".hygiene";

    private boolean secure;
    private boolean hygiene;

    @Override
    protected void parse() {
        secure = getBoolean(SECURE_KEY, false);
        secure = getBoolean(HYGIENE_KEY, false);
    }

    public boolean isSecure() {
        return secure;
    }
    public boolean isHygiene() {
        return hygiene;
    }

    public void setSecure(boolean secure) {
        if (this.secure == secure) {
            return;
        }
        this.secure = secure;
        getConfig().setProperty(SECURE_KEY, Boolean.toString(this.secure));
    }
    public void setHygiene(boolean hygiene) {
        if (this.hygiene == hygiene) {
            return;
        }
        this.hygiene = hygiene;
        getConfig().setProperty(HYGIENE_KEY, Boolean.toString(this.hygiene));
    }
}
