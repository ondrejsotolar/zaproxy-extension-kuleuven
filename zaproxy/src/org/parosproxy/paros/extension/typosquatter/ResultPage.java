package org.parosproxy.paros.extension.typosquatter;

import java.util.Collection;
import java.util.List;

public class ResultPage {

    private String header = "HTTP/1.1 200 OK\r\nContent-Type: text/html; charset=utf-8";
    private String body = "<!doctype html><html lang=\"en\"><head><meta charset=\"utf-8\"><title>Blocked by proxy</title></head><body><H1>Blocked by proxy!</H1>%s</body></html>";
    private String proceedButton = "<form action=\"http://%s\" method=\"GET\"><button type=\"submit\">Proceed to %s</button></form><br>";

    public String getHeader() {
        return header;
    }

    public String getBody(Collection<String> hosts) {
        return String.format(body, getButtons(hosts));
    }

    public String getButtons(Collection<String> hosts) {
        String buttons = "";
        for (String host : hosts) {
            buttons += String.format(proceedButton, host, host);
        }
        return buttons;
    }

}
