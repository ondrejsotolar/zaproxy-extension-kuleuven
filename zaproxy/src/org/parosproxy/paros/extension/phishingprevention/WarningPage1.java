package org.parosproxy.paros.extension.phishingprevention;

import java.util.Collection;
import java.util.List;

public class WarningPage1 {

    // TODO: Two buttons
    /* 1- Cancel Request
       2- Continue  */

    private String header = "HTTP/1.1 200 OK\r\nContent-Type: text/html; charset=utf-8";

    private String body = "<!doctype html><html lang=\"en\"><head><meta charset=\"utf-8\">" +
            "<title>Warning</title></head><body><H1>WARNING !!!</H1>" +
            "<p>Are you sure about the address? If yes, click below and you won't be bothered anymore.</p>" +
            "<p>Failed typosquatting strategies:%s</p>%s</body></html>";


}
