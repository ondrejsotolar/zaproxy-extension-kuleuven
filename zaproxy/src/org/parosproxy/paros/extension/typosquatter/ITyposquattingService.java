package org.parosproxy.paros.extension.typosquatter;

import java.util.List;

public interface ITyposquattingService {
    TyposquattingResult checkCandidateHost(String candidate);

    void setWhiteList(List<String> newList);

    void setStrategies(List<TyposquattingStrategy> strategies);
}
