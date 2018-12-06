package org.parosproxy.paros.extension.phishingprevention;

import java.util.Collection;
import java.util.List;

public class WarningPage2 {

    //TODO - List all the hygene issues and a tickbox for dont remind me again

    /* If Clicked, store the website and the user */

    private String header = "HTTP/1.1 200 OK\r\nContent-Type: text/html; charset=utf-8";

    private String body = "<!doctype html><html lang=\"en\"><head><meta charset=\"utf-8\">" +
            "<title>Warning</title></head><body><H1>WARNING !!!</H1>" +
            "<p>Your Password is not secure as it falls under some of our Password Hygene Strategies</p>" +
            "<p>The Problems are:%s</p>%s</body></html>";

    private String checkbox = "<input type=\"checkbox\" name=\"dnd\" value=\"\"> Do not Show this message again<br> " ;

    private String button = "<form action=\"save value\" method=\"POST\">" +
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



    public String getFailedStrategiesList(Collection<String> names) {
        String start = "<ul>";
        for (String name : names) {
            start += String.format("<li>%s</li>", name);
        }
        return start + "</ul>";
    }
}
