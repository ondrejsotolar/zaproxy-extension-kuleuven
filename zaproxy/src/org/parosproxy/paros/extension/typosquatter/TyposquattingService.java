package org.parosproxy.paros.extension.typosquatter;

import org.parosproxy.paros.extension.typosquatter.strategies.LongHostStrategy;

import java.util.ArrayList;
import java.util.List;

public class TyposquattingService {

    private List<String> whitelist;
    private List<TyposquattingStrategy> strategies;

    public TyposquattingService(List<String> whitelist) {
        this.whitelist = whitelist;
        initStrategies();
    }

    public TyposquattingResult checkCandidateHost (String candidate) {
        if (candidate == null || candidate.isEmpty()) {
            throw new RuntimeException("TyposquattingService.checkCandidateHost: empty or null candidate host");
        }
        TyposquattingResult result = new TyposquattingResult(candidate);

        for (String host : whitelist) {
            for (TyposquattingStrategy strategy : strategies) {
                boolean strategyFailed = strategy.applyStrategy(host, candidate);
                if (strategyFailed) {
                    result.addFailedTyposquattingStrategy(host, strategy.getName());
                }
            }
        }
        return result;
    }

    private void initStrategies() {
        strategies = new ArrayList<>();

        strategies.add(new LongHostStrategy());
    }
}


