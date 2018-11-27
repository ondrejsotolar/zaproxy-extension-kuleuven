package org.parosproxy.paros.extension.typosquatter;

import java.util.Collection;
import java.util.List;

public class ResultPage {

    private String header = "HTTP/1.1 200 OK\r\nContent-Type: text/html; charset=utf-8";

    private String body = "<!doctype html><html lang=\"en\"><head><meta charset=\"utf-8\">" +
            "<title>Blocked by proxy</title></head><body><H1>Blocked by proxy!</H1>" +
            "<p>Are you sure about the address? If yes, click below and you won't be bothered anymore.</p>" +
            "<p>Failed typosquatting strategies:%s</p>%s</body></html>";

    private String proceedButton = "<form action=\"http://%s\" method=\"POST\">" +
            "<button type=\"submit\">Proceed to %s</button>" +
            "<input type=\"hidden\" name=\"save\" value=\"true\" />" +
            "<input type=\"hidden\" name=\"requestId\" value=\"%d\" />" +
            "</form><br>";

    public String getHeader() {
        return header;
    }

    public String getBody(String candidate, int requestId, Collection<String> strategyNames) {
        return String.format(body, getFailedStrategiesList(strategyNames),
                getButton(candidate, requestId));
    }

    public String getButton(String candidate, int requestId) {

        return String.format(proceedButton, candidate, candidate, requestId);
    }

    public String getFailedStrategiesList(Collection<String> names) {
        String start = "<ul>";
        for (String name : names) {
            start += String.format("<li>%s</li>", name);
        }
        return start + "</ul>";
    }

}
