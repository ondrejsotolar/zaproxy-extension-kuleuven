package org.parosproxy.paros.extension.phishingprevention;

import org.parosproxy.paros.extension.phishingprevention.requestscan.GetRequestScanner;
import org.parosproxy.paros.extension.phishingprevention.requestscan.PostRequestScanner;
import org.parosproxy.paros.extension.phishingprevention.requestscan.RequestParamsScanner;
import org.parosproxy.paros.extension.phishingprevention.requestscan.StringParamScanner;
import org.parosproxy.paros.network.HttpMessage;

import java.util.*;

public class RequestCredentialScannerService implements CredentialScanerService {

    private final String methodGet = "get";
    private final String methodPost = "post";

    private String[] passwordKeywords =
            new String[] { "password", "pwd", "pass" };

    private String[] usernameKeywords =
            new String[] { "uname", "user", "username", "user_name" };

    private RequestParamsScanner requestScannerGet = new GetRequestScanner();
    private RequestParamsScanner requestScannerPost = new PostRequestScanner();

    @Override
    public Credentials getCredentialsInRequest(HttpMessage message) {

        if (!isAllowedMethod(message.getRequestHeader().getMethod())) {
            return null;
        }

        Map<String, List<String>> requesetParams = isPost(message)
                ? requestScannerPost.getRequestParams(message)
                : requestScannerGet.getRequestParams(message);
        if (requesetParams == null) {
            return null;
        }

        String host = message.getRequestHeader().getHostName();
        Credentials result = setCredentials(host, requesetParams);
        return result;
    }

    public Credentials setCredentials(String host, Map<String, List<String>> queryParameters) {
        Credentials result = new Credentials();
        result.setHost(host);

        for (Map.Entry<String, List<String>> parameter : queryParameters.entrySet()) {
            if (isUsername(parameter.getKey())) {
                result.setUsername(parameter.getValue().get(0));
                continue;
            }
            if (isPassword(parameter.getKey())) {
                result.setPassword(parameter.getValue().get(0));
                continue;
            }
        }
        if (result.getPassword() == null || result.getPassword() == "" ||
            result.getUsername() == null || result.getUsername() == "") {
            return null;
        }
        return result;
    }

    public boolean isAllowedMethod(String method) {
        return method.equalsIgnoreCase(methodGet) || method.equalsIgnoreCase(methodPost);
    }

    public boolean isPost(HttpMessage message) {
        return message.getRequestHeader().getMethod().equalsIgnoreCase(methodPost);
    }

    public boolean isPassword(String s) {
        return (Arrays.stream(this.passwordKeywords)).anyMatch(x -> s.contains(x));
    }

    public boolean isUsername(String s) {
        return (Arrays.stream(this.usernameKeywords)).anyMatch(x -> s.contains(x));
    }
}
