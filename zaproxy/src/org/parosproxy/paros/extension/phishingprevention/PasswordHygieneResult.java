package org.parosproxy.paros.extension.phishingprevention;

import java.util.HashMap;

public class PasswordHygieneResult {

    private Credentials credentials;

    private HashMap<String, String> mapMsgfailedStrategies;

    public PasswordHygieneResult(Credentials credentials) {

        this.credentials = credentials;
        this.mapMsgfailedStrategies = new HashMap<>();
    }

    public void addMsgFailedStrategy(String name, String reason) {
        mapMsgfailedStrategies.put(name, reason);
    }

    public boolean getResult() {
        return mapMsgfailedStrategies.size() > 0;
    }

    public HashMap<String, String> getMapMsgFailedStrategies() {
        return mapMsgfailedStrategies;
    }

}
