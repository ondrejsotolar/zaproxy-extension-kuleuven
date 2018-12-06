package org.parosproxy.paros.extension.phishingprevention.requestscan;

import org.parosproxy.paros.network.HttpMessage;

import java.util.List;
import java.util.Map;

public interface RequestParamsScanner {
    Map<String, List<String>> getRequestParams(HttpMessage message);
}
