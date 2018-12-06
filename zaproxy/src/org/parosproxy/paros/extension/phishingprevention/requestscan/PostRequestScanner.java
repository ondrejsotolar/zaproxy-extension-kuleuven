package org.parosproxy.paros.extension.phishingprevention.requestscan;

import org.apache.commons.lang.StringUtils;
import org.parosproxy.paros.network.HttpHeader;
import org.parosproxy.paros.network.HttpMessage;
import org.parosproxy.paros.network.HttpRequestHeader;

import java.util.List;
import java.util.Map;

public class PostRequestScanner implements RequestParamsScanner {

    StringParamScanner stringParamScanner = new QueryStringParamScanner();

    public Map<String, List<String>> getRequestParams(HttpMessage message) {
        return stringParamScanner.getRequestParams(getQuery(message));
    }

    public String getQuery(HttpMessage message){
        String contentType = message.getRequestHeader().getHeader(HttpRequestHeader.CONTENT_TYPE);
        if (!StringUtils.startsWithIgnoreCase(
                        contentType.trim(), HttpHeader.FORM_URLENCODED_CONTENT_TYPE)) {
            throw new UnsupportedOperationException(
                    "POST request with other than content type: " +
                            "application/x-www-form-urlencoded are not supported.");
        }

        return message.getRequestBody().toString();
    }

}
