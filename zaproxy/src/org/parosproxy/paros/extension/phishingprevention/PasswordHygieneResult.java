package org.parosproxy.paros.extension.phishingprevention;

import org.parosproxy.paros.extension.phishingprevention.CrackLib.CrackLib;
import org.parosproxy.paros.extension.phishingprevention.requestscan.StringParamScanner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PasswordHygieneResult {

    private Credentials credentials;
    //private List<String> failedStrategies;
    private HashMap<String, List<String>> failedStrategies;
    //private String CrackLibMsg;

    public PasswordHygieneResult(Credentials credentials) {

        this.credentials = credentials;
        this.failedStrategies = new HashMap<>();
    }

    public void addFailedStrategy(String name, List<String> reasons) {
        failedStrategies.put(name, reasons);
    }

    public boolean getResult() {
        return failedStrategies.size() > 0;
    }

    public HashMap<String, List<String>> getFailedStrategies() {
        return failedStrategies;
    }

    // TODO: delete
//    public void makeMap(){
//        mapMsgFailedStrats.put(CrackLibMsg, failedStrategies);
//    }
//
//    public Map<String, List<String>> getMapMsgFailedStrats(){
//        return mapMsgFailedStrats;
//    }
//
//    public void setCrackLibMsg(String msg){
//        CrackLibMsg = msg;
//    }
}
