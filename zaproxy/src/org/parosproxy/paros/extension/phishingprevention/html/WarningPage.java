package org.parosproxy.paros.extension.phishingprevention.html;

import org.parosproxy.paros.extension.phishingprevention.PasswordHygieneResult;

import java.util.Collection;

public class WarningPage {
    private String header = "HTTP/1.1 200 OK\r\nContent-Type: text/html; charset=utf-8";

    private String body = "<!doctype html><html lang=\"en\"><head><meta charset=\"utf-8\">" +
            "<title>Blocked by proxy</title></head><body><H1>Blocked by proxy!</H1>" +
            "<p>Login detected! TODO: state reason...</p>" +
            "%s%s%s</body></html>";

    private String bodyWithHygiene = "<!doctype html><html lang=\"en\"><head><meta charset=\"utf-8\">" +
            "<title>Blocked by proxy</title></head><body><H1>Blocked by proxy!</H1>" +
            "<p>Login detected! TODO: state reason...</p>" +
            "%s%s</body></html>";

    private String proceedButton = "<form action=\"http://%s\" method=\"POST\">" +
            "<button type=\"submit\">Proceed to %s</button>" +
            "<input type=\"hidden\" name=\"save\" value=\"true\" />" +
            "<input type=\"hidden\" name=\"request_id\" value=\"%d\" />" +
            "<input type=\"hidden\" name=\"host_address\" value=\"%s\" />" +
            "</form><br>";
    private String cancelButton = "<form action=\"http://%s\" method=\"POST\">" +
            "<button type=\"submit\">Cancel request %s</button>" +
            "<input type=\"hidden\" name=\"save\" value=\"false\" />" +
            "<input type=\"hidden\" name=\"request_id\" value=\"%d\" />" +
            "<input type=\"hidden\" name=\"host_address\" value=\"%s\" />" +
            "</form><br>";

    public String getHeader() {
        return header;
    }

    public String getBody(int requestId, String host) {
        return String.format(body,
                getButton(proceedButton, "localhost", requestId, host),
                getButton(cancelButton, "localhost", requestId, host));
    }

    public String getBody(int requestId, String host, PasswordHygieneResult hygieneResult) {
        return String.format(bodyWithHygiene,
                getHygieneWarningMessage(hygieneResult),
                getButton(proceedButton, "localhost", requestId, host),
                getButton(cancelButton, "localhost", requestId, host));
    }

    private String getHygieneWarningMessage(PasswordHygieneResult hygieneResult) {
        return "<p>Password hygiene results... TODO</p>";
    }

    public String getButton(String base, String candidate, int requestId, String host) {

        return String.format(base, candidate, candidate, requestId, host);
    }
}
