package org.parosproxy.paros.extension.phishingprevention;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PasswordHygieneResult {

    private String password;
    private List<String> failedStrategies;

    public PasswordHygieneResult(String password) {
        this.password = password;
    }

    public void addFailedTyposquattingStrategy(String name) {
        failedStrategies.add(name);
    }
}
