package org.parosproxy.paros.extension.phishingprevention.requestscan;

import org.apache.commons.httpclient.URIException;
import org.parosproxy.paros.network.HttpMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetRequestScanner implements RequestParamsScanner {

    StringParamScanner stringParamScanner = new QueryStringParamScanner();

    public String getQuery(HttpMessage message) throws URIException {
        return message.getRequestHeader().getURI().getQuery();
    }

    public Map<String, List<String>> getRequestParams(HttpMessage message) {
        Map<String, List<String>> result = null;
        try {
            result = stringParamScanner.getRequestParams(getQuery(message));
        } catch (URIException e) {
            e.printStackTrace();
        }
        return result;
    }
}
