package org.parosproxy.paros.extension.phishingprevention;

import org.parosproxy.paros.extension.phishingprevention.CrackLib.CrackLib;
import org.parosproxy.paros.extension.phishingprevention.requestscan.StringParamScanner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PasswordHygieneResult {

    private Credentials credentials;
    private List<String> failedStrategies;
    private HashMap<String, List<String>> mapMsgFailedStrats;
    private String CrackLibMsg;

    public PasswordHygieneResult(Credentials credentials) {

        this.credentials = credentials;
        this.failedStrategies = new ArrayList<>();
        this.mapMsgFailedStrats = new HashMap<String, List<String>>();
    }

    public void addFailedStrategy(String name) {
        failedStrategies.add(name);
    }

    public boolean getResult() {
        return failedStrategies.size() > 0; // check the list aka the value of msg
    }

    public List<String> getFailedStrategies() {
        return failedStrategies;
    }

    public void makeMap(){
        mapMsgFailedStrats.put(CrackLibMsg, failedStrategies);
    }

    public Map<String, List<String>> getMapMsgFailedStrats(){
        return mapMsgFailedStrats;
    }

    public void setCrackLibMsg(String msg){
        CrackLibMsg = msg;
    }
}
