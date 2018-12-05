package org.parosproxy.paros.extension.phishingprevention.strategies;

import org.parosproxy.paros.extension.phishingprevention.PasswordHygieneStrategy;
import org.parosproxy.paros.extension.phishingprevention.CrackLib.*;

/**
 * Created by PC10 on 03-12-2018.
 */
public class CrackLibTestStrategy implements PasswordHygieneStrategy{

    String outMsg;

    @Override
    public String getName() {
        return "CrackLibTestStrategy";
    }

    // TODO: remove printout
    @Override
    public boolean applyStrategy(String password){

        String[] params = new String[4];
        params[0] = "-check";
        params[1] = "dictionary";
        params[2] = password;
        params[3] = null; //username

        boolean testRes;

        try {
            CrackLib.main(params);
            testRes = CrackLib.getBoolPasswordFailed();
            outMsg = CrackLib.getOutPutMsg();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("CrackLib main failed to run. Args: " + params);
            testRes = true;
        }

        return testRes;
    }

    public String getOutMsg(){
        return outMsg;
    }
}
