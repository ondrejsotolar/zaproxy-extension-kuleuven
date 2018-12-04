package org.parosproxy.paros.extension.phishingprevention.html;

public class CancelPage {
    private String header = "HTTP/1.1 200 OK\r\nContent-Type: text/html; charset=utf-8";

    private String body = "<!doctype html><html lang=\"en\"><head><meta charset=\"utf-8\">" +
            "<title>Login canceled by user</title></head><body><H1>Login canceled!</H1>" +
            "<p>Reason: canceled by user</p>" +
            "</body></html>";

    public String getHeader() {
        return header;
    }

    public String getBody() {
        return body;
    }
}
