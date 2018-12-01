package org.parosproxy.paros.extension.phishingprevention.requestscan;

import org.parosproxy.paros.network.HttpMessage;

import java.util.List;
import java.util.Map;

public class PostRequestScanner implements RequestScanner {

    @Override
    public Map<String, List<String>> getRequestParams(HttpMessage message) {
        return null;
    }
}
