package org.parosproxy.paros.extension.phishingprevention.strategies;

import org.parosproxy.paros.extension.phishingprevention.PasswordHygieneStrategy;
import org.parosproxy.paros.extension.phishingprevention.CrackLib.*;

/**
 * Created by PC10 on 03-12-2018.
 */
public class CrackLibTestStrategy implements PasswordHygieneStrategy{

    @Override
    public String getName() {
        return "CrackLibTestStrategy";
    }


    // TODO: remove ALL printouts !!!
    @Override
    public String applyStrategy(String password){

        String[] params = new String[4];
        params[0] = "-check";
        params[1] = "dictionary";
        params[2] = password;
        params[3] = null; //username

        String outMsg;

        try {
            CrackLib.main(params);
            outMsg = CrackLib.getOutPutMsg();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("CrackLib main failed to run. Args: " + params);
            outMsg = new String();
        }

        return outMsg;
    }
}
