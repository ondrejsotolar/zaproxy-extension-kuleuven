package org.parosproxy.paros.extension.typosquatter;

import org.junit.Assert;
import org.junit.Test;

import java.io.InputStream;
import java.net.*;
import java.util.Scanner;

public class IntegrationTest {

    private boolean isProxyRunning() throws Exception {
        String urlString = "http://google.com";
        String charset = "UTF-8";

        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 8081));
        URLConnection connection = null;
        InputStream response = null;
        try {
            connection = new URL(urlString).openConnection(proxy);
            connection.setRequestProperty("Accept-Charset", charset);
            response = connection.getInputStream();
        } catch (ConnectException e) {
            return false;
        }
        return true;
    }

    private String makeRequest(String address) throws Exception {
        String urlString = address;
        String charset = "UTF-8";

        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 8081));
        URLConnection connection = new URL(urlString).openConnection(proxy);
        connection.setRequestProperty("Accept-Charset", charset);
        InputStream  response = connection.getInputStream();

        try (Scanner scanner = new Scanner(response)) {
            return scanner.useDelimiter("\\A").next();
        }
    }

    /**
     * Run only when proxy is running, Typosquatting extension is enabled and test-whitelist.txt is loaded
     * @throws Exception
     */
    @Test
    public void passThroughProxy_OKrequest_TyposquatterON() throws Exception {
        if (!isProxyRunning()) {
            return;
        }

        String expected = "<title>StealMyLogin.com - exposing the dangers of insecure login forms</title>";
        String actual = makeRequest("http://stealmylogin.com");

        Assert.assertTrue(actual.contains(expected));
    }

    /**
     * Run only when proxy is running, Typosquatting extension is enabled and test-whitelist.txt is loaded
     * @throws Exception
     */
    @Test
    public void passThroughProxy_NOKrequest_TyposquatterON() throws Exception {
        if (!isProxyRunning()) {
            return;
        }

        String expectedWong = "<title>Google</title>";
        String expected = "<title>Blocked by proxy</title>";
        String actual = makeRequest("http://google.com");

        Assert.assertFalse(actual.contains(expectedWong));
        Assert.assertTrue(actual.contains(expected));
    }
}
