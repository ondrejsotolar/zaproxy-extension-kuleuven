package org.parosproxy.paros.extension.typosquatter;

import org.parosproxy.paros.extension.typosquatter.strategies.LongHostStrategy;

import java.util.ArrayList;
import java.util.List;

public class TyposquattingService {

    private List<String> whiteList;
    private List<TyposquattingStrategy> strategies;

    public TyposquattingService(List<String> whiteList) {
        this.whiteList = whiteList;
        initStrategies();
    }

    public TyposquattingResult checkCandidateHost (String candidate) {
        if (candidate == null || candidate.isEmpty()) {
            throw new RuntimeException("TyposquattingService.checkCandidateHost: empty or null candidate host");
        }

        TyposquattingResult result = new TyposquattingResult(candidate);
        if (whiteList.contains(candidate)) {
            return result;
        }

        for (String host : whiteList) {
            for (TyposquattingStrategy strategy : strategies) {
                boolean strategyFailed = strategy.applyStrategy(host, candidate);
                if (strategyFailed) {
                    result.addFailedTyposquattingStrategy(host, strategy.getName());
                }
            }
        }
        return result;
    }

    public void setWhiteList(List<String> newList) {
        this.whiteList = newList;
    }

    private void initStrategies() {
        strategies = new ArrayList<>();

        strategies.add(new LongHostStrategy());
    }


}


