package org.parosproxy.paros.extension.phishingprevention.html;

import org.parosproxy.paros.extension.phishingprevention.PasswordHygieneResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WarningPage {
    private String header = "HTTP/1.1 200 OK\r\nContent-Type: text/html; charset=utf-8";

    private String body = "<!doctype html><html lang=\"en\"><head><meta charset=\"utf-8\">" +
            "<title>Blocked by proxy</title></head><body><H1>Blocked by proxy!</H1>" +
            "<H3>Login attempt detected!</H3>" +
            "%s%s</body></html>";

    private String bodyWithHygiene = "<!doctype html><html lang=\"en\"><head><meta charset=\"utf-8\">" +
            "<title>Blocked by proxy</title></head><body><H1>Blocked by proxy!</H1>" +
            "<H3>Login attempt detected!</H3>" +
            "%s%s%s</body></html>";

    private String proceedButtonBase = "<form action=\"http://%s\" method=\"POST\">" +
            "<button type=\"submit\">Proceed to %s</button>" +
            "<input type=\"hidden\" name=\"save\" value=\"true\" />" +
            "<input type=\"hidden\" name=\"request_id\" value=\"%d\" />" +
            "<input type=\"hidden\" name=\"host_address\" value=\"%s\" />" +
            "</form><br>";
    private String cancelButtonBase = "<form action=\"http://%s\" method=\"POST\">" +
            "<button type=\"submit\">Cancel</button>" +
            "<input type=\"hidden\" name=\"save\" value=\"false\" />" +
            "<input type=\"hidden\" name=\"request_id\" value=\"%d\" />" +
            "<input type=\"hidden\" name=\"host_address\" value=\"%s\" />" +
            "</form><br>";

    private String hygieneListBase = "<p>Password hygiene fail reasons:<br/><ul>%s</ul></p>";


    public String getHeader() {
        return header;
    }

    public String getBody(int requestId, String host) {
        return String.format(body,
                getProceedButton("localhost", requestId, host),
                getCancelButton("localhost", requestId, host));
    }

    public String getBody(int requestId, String host, PasswordHygieneResult hygieneResult) {
        return String.format(bodyWithHygiene,
                getHygieneWarningMessage(hygieneResult),
                getProceedButton(host, requestId, host),
                getCancelButton("localhost", requestId, host));
    }

    private String getHygieneWarningMessage(PasswordHygieneResult hygieneResult) {
        if (hygieneResult.getMapMsgFailedStrategies().keySet().size() <=0) {
            return "";
        }
        String rows = "";
        for (Map.Entry<String, String> entry : hygieneResult.getMapMsgFailedStrategies().entrySet()) {
            rows += "<li>" + entry.getValue() + "</li>";
        }
        return String.format(hygieneListBase, rows);
    }

    public String getProceedButton(String candidate, int requestId, String host) {
        return String.format(proceedButtonBase, candidate, candidate, requestId, host);
    }

    public String getCancelButton(String candidate, int requestId, String host) {
        return String.format(cancelButtonBase, candidate, requestId, host);
    }
}
