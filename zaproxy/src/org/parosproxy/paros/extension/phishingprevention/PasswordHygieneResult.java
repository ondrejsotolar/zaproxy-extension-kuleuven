package org.parosproxy.paros.extension.phishingprevention;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PasswordHygieneResult {

    private Credentials credentials;
    private List<String> failedStrategies;

    public PasswordHygieneResult(Credentials credentials) {

        this.credentials = credentials;
        this.failedStrategies = new ArrayList<>();
    }

    public void addFailedStrategy(String name) {
        failedStrategies.add(name);
    }

    public boolean getResult() {
        return this.failedStrategies.size() > 0;
    }
}
