package org.parosproxy.paros.extension.phishingprevention.hygiene;

import org.parosproxy.paros.extension.phishingprevention.Credentials;

import java.util.HashMap;

public class PasswordHygieneResult {

    private HashMap<String, String> mapMsgfailedStrategies = new HashMap<>();

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
