package org.parosproxy.paros.extension.typosquatter;

import java.util.*;

public class TyposquattingResult {
    private String candidate;
    private Map<String, List<String>> failedTyposquattingStrategies;

    public TyposquattingResult(String candidate) {
        this.candidate = candidate;
        this.failedTyposquattingStrategies = new HashMap<>();
    }

    public String getCandidate() {
        return candidate;
    }

    public void setCandidate(String candidate) {
        this.candidate = candidate;
    }

    public void addFailedTyposquattingStrategy(String host, String name) {
        if (!failedTyposquattingStrategies.containsKey(host)) {
            List<String> strategyNames = new ArrayList<>();
            strategyNames.add(name);
            failedTyposquattingStrategies.put(host, strategyNames);
        } else {
            failedTyposquattingStrategies.get(host).add(name);
        }
    }

    public boolean getResult() {
        return this.failedTyposquattingStrategies.size() > 0;
    }

    public Collection<String> getFailedStrategyNames() {
        Set<String> names = new HashSet<>();
        for (Map.Entry<String, List<String>> entry: this.failedTyposquattingStrategies.entrySet()) {
            for (String name: entry.getValue()) {
                names.add(name);
            }
        }
        return names;
    }
}
