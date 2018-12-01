package org.parosproxy.paros.extension.phishingprevention.requestscan;

import org.apache.commons.httpclient.URIException;
import org.parosproxy.paros.network.HttpMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetRequestScanner implements RequestScanner {

    public String getQuery(HttpMessage message) throws URIException {
        return message.getRequestHeader().getURI().getQuery();
    }

    public Map<String, List<String>> getRequestParams(HttpMessage message) {
        try {
            String queryString = getQuery(message);
            if (queryString == null || queryString == "") {
                return null;
            }

            Map<String, List<String>> queryParameters = getParameters(queryString);
            return queryParameters;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Map<String, List<String>> getParameters(String queryString) {
        Map<String, List<String>> queryParameters = new HashMap<>();
        String[] parameters = queryString.split("&");

        for (String parameter : parameters) {
            String[] keyValuePair = parameter.split("=");
            if (keyValuePair[0] == null || keyValuePair[0] == "" ||
                    keyValuePair[1] == null || keyValuePair[1] == "") {
                continue;
            }
            List<String> name = queryParameters.get(keyValuePair[0]);
            if (name == null) {
                List<String> values = new ArrayList<>();
                values.add(keyValuePair[1]);
                queryParameters.put(keyValuePair[0], values);
            } else {
                name.add(keyValuePair[1]);
            }
        }
        return queryParameters;
    }
}
