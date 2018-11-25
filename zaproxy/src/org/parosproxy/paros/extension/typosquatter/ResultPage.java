package org.parosproxy.paros.extension.typosquatter;

public class ResultPage {

    private String header = "HTTP/1.1 200 OK\r\nContent-Type: text/html; charset=utf-8";

    private String body = "<!doctype html><html lang=\"en\"><head><meta charset=\"utf-8\">" +
            "<title>Blocked by proxy</title></head><body><H1>Blocked by proxy!</H1>%s</body></html>";

    private String proceedButton = "<form action=\"http://%s\" method=\"POST\">" +
            "<button type=\"submit\">Proceed to %s</button>" +
            "<input type=\"hidden\" name=\"save\" value=\"true\" />" +
            "<input type=\"hidden\" name=\"requestId\" value=\"%d\" />" +
            "</form><br>";

    public String getHeader() {
        return header;
    }

    public String getBody(String candidate, int requestId) {
        return String.format(body, getButton(candidate, requestId));
    }

    public String getButton(String candidate, int requestId) {

        return String.format(proceedButton, candidate, candidate, requestId);
    }

}
