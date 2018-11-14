package org.parosproxy.paros.extensions.typosquatter;

import org.junit.Assert;
import org.junit.Test;
import org.parosproxy.paros.extension.typosquatter.ResultPage;

import java.util.ArrayList;
import java.util.List;

public class ResultPageTest {

    @Test
    public void resultPageBuild_single() {
        String expected = "<!doctype html><html lang=\"en\"><head><meta charset=\"utf-8\"><title>Blocked by proxy</title></head><body><H1>Blocked by proxy!</H1><form action=\"http://google.com\" method=\"GET\"><button type=\"submit\">Proceed to google.com</button></form><br></body></html>";

        ResultPage resultPage = new ResultPage();
        List<String> hosts = new ArrayList<>();
        hosts.add("google.com");
        String actual = resultPage.getBody(hosts);

        Assert.assertEquals(expected, actual);

    }

    @Test
    public void resultPageBuild_two() {
        String expected = "<!doctype html><html lang=\"en\"><head><meta charset=\"utf-8\"><title>Blocked by proxy</title></head><body><H1>Blocked by proxy!</H1><form action=\"http://google.com\" method=\"GET\"><button type=\"submit\">Proceed to google.com</button></form><br><form action=\"http://duckduckgo.com\" method=\"GET\"><button type=\"submit\">Proceed to duckduckgo.com</button></form><br></body></html>";

        ResultPage resultPage = new ResultPage();
        List<String> hosts = new ArrayList<>();
        hosts.add("google.com");
        hosts.add("duckduckgo.com");
        String actual = resultPage.getBody(hosts);

        Assert.assertEquals(expected, actual);

    }
}
