package org.parosproxy.paros.extension.phishingprevention.html;

import org.parosproxy.paros.extension.phishingprevention.hygiene.PasswordHygieneResult;
import org.parosproxy.paros.network.HttpMalformedHeaderException;
import org.parosproxy.paros.network.HttpMessage;

public class ResponseMessageHandler {
    public void setResponseBodyContent(HttpMessage msg, int requestId, String host,
                                       PasswordHygieneResult hygieneResult) {
        WarningPage warningPage = new WarningPage();

        msg.setResponseBody((hygieneResult == null)
                ? warningPage.getBody(requestId, host)
                : warningPage.getBody(requestId, host, hygieneResult));

        try {
            msg.setResponseHeader(warningPage.getHeader());
        } catch (HttpMalformedHeaderException e) {
            e.printStackTrace();
        }
        msg.getResponseHeader().setContentLength(msg.getResponseBody().length());
    }

    public void setResponseBodyForCancelPage(HttpMessage msg) {
        CancelPage cancelPage = new CancelPage();
        msg.setResponseBody(cancelPage.getBody());

        try {
            msg.setResponseHeader(cancelPage.getHeader());
        } catch (HttpMalformedHeaderException e) {
            e.printStackTrace();
        }
        msg.getResponseHeader().setContentLength(msg.getResponseBody().length());
    }
}
