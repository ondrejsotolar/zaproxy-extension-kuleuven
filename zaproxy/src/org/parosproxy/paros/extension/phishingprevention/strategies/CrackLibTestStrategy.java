package org.parosproxy.paros.extension.phishingprevention.strategies;

import org.parosproxy.paros.extension.phishingprevention.PasswordHygieneStrategy;
import org.parosproxy.paros.extension.phishingprevention.CrackLib.*;

/**
 * Created by PC10 on 03-12-2018.
 */
public class CrackLibTestStrategy implements PasswordHygieneStrategy{

    boolean result;

    @Override
    public String getName() {
        return "CrackLibTestStrategy";
    }

    @Override
    public boolean applyStrategy(String password) {



        return result;
    }
}
