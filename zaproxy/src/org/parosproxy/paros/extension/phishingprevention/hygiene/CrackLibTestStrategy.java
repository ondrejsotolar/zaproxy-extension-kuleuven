package org.parosproxy.paros.extension.phishingprevention.hygiene;

import org.apache.log4j.Logger;
import org.parosproxy.paros.extension.phishingprevention.CrackLib.*;

/**
 * Created by PC10 on 03-12-2018.
 */
public class CrackLibTestStrategy implements PasswordHygieneStrategy{

    private static Logger log = Logger.getLogger(CrackLibTestStrategy.class);
    private CrackLib crackLib = new CrackLib();

    @Override
    public String getName() {
        return "CrackLibTestStrategy";
    }

    @Override
    public String applyStrategy(String password){

        String[] params = new String[4];
        params[0] = "-check";
        params[1] = "dictionary";
        params[2] = password;
        params[3] = null; //username

        String outMsg = null;
        try {
            crackLib.run(params);
            outMsg = crackLib.getOutPutMsg();
        } catch (Exception e) {
            log.info("CrackLib main failed to run. Args: " + params);
        }
        return outMsg;
    }
}
