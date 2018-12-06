package org.parosproxy.paros.extensions.phishingprevention;

import org.apache.commons.httpclient.URIException;
import org.junit.Assert;
import org.junit.Test;
import org.parosproxy.paros.extension.phishingprevention.Credentials;
import org.parosproxy.paros.extension.phishingprevention.RequestCredentialScannerService;
import org.parosproxy.paros.network.HttpMalformedHeaderException;
import org.parosproxy.paros.network.HttpMessage;

public class RequestCredentialScannerServiceTest {

    private final String requestHeaderGet_withPassword = "GET https://myserver.com/?user=myUsername&pass=MyPasswort HTTP/1.1\n" +
            "Host: myserver.com\n" +
            "Connection: keep-alive\n" +
            "Upgrade-Insecure-Requests: 1\n" +
            "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.110 Safari/537.36\n" +
            "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8\n" +
            "Accept-Encoding: gzip, deflate\n" +
            "Accept-Language: en-GB,en;q=0.9,en-US;q=0.8,cs;q=0.7,sk;q=0.6,nl;q=0.5\n" +
            "Cookie: _ga=GA1.2.714783586.1541761213\n";

    private final String requestHeaderGet_noPassword = "GET https://myserver.com/ HTTP/1.1\n" +
            "Host: myserver.com\n" +
            "Connection: keep-alive\n" +
            "Upgrade-Insecure-Requests: 1\n" +
            "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.110 Safari/537.36\n" +
            "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8\n" +
            "Accept-Encoding: gzip, deflate\n" +
            "Accept-Language: en-GB,en;q=0.9,en-US;q=0.8,cs;q=0.7,sk;q=0.6,nl;q=0.5\n" +
            "Cookie: _ga=GA1.2.714783586.1541761213\n";

    private final String requestPost_withPassword_head = "POST https://myserver.com/en/ HTTP/1.1\n" +
            "Host: myserver.com\n" +
            "Connection: keep-alive\n" +
            "Content-Length: 90\n" +
            "X-Build-Id: 1481\n" +
            "Origin: https://myserver.com\n" +
            "X-Requested-With: XMLHttpRequest\n" +
            "X-User-State: anonymous\n" +
            "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.110 Safari/537.36\n" +
            "Content-Type: application/x-www-form-urlencoded\n" +
            "Accept: */*\n" +
            "Referer: https://myserver.com/en/\n" +
            "Accept-Encoding: gzip, deflate, br\n" +
            "Accept-Language: en-GB,en;q=0.9,en-US;q=0.8,cs;q=0.7,sk;q=0.6,nl;q=0.5\n" +
            "Cookie: __cfduid=ddafbe789b2fe0f4646276a159e584bbb1543680112; lang=en; loc=world; _ga=GA1.2.697169934.1543680114; _gid=GA1.2.1296932571.1543680114; _gat=1; AWSALB=PYjOelget6fncsNZzh+ekBq37hDqjJgaE+wqf6zJEQ6Ad0KWPuZ64Fp2UrxkgXNKkNfzCEpdSVA9tMnKu5d38HvQEGP7b6nEzzyHZoeFDu26/jJHU/OPGnj4bBJn;\n";

    private final String requestPost_withPassword_body =
            "username=myUsername&password=MyPasswort&remember=1&_do=login-menu-loginForm-submit&_submit=Log+in";


    private final String requestPost_withPartialCredentials_body =
            "nick=myUsername&password=MyPasswort&remember=1&_do=login-menu-loginForm-submit&_submit=Log+in";

    @Test
    public void recognizeGetRequest_returnTrue() throws HttpMalformedHeaderException {
        RequestCredentialScannerService service = new RequestCredentialScannerService();
        HttpMessage message = new HttpMessage();
        message.setRequestHeader(requestHeaderGet_withPassword);

        boolean isAllowed = service.isAllowedMethod(message.getRequestHeader().getMethod());
        Assert.assertTrue(isAllowed);

        boolean isPost = service.isPost(message);
        Assert.assertFalse(isPost);
    }

    @Test
    public void getRequets_containsPassword_returnCredentials() throws HttpMalformedHeaderException, URIException {
        Credentials expected = new Credentials("myserver.com", "myUsername", "MyPasswort");

        HttpMessage message = new HttpMessage();
        message.setRequestHeader(requestHeaderGet_withPassword);

        RequestCredentialScannerService service = new RequestCredentialScannerService();
        Credentials actual = service.getCredentialsInRequest(message);

        Assert.assertEquals(expected.getHost(), actual.getHost());
        Assert.assertEquals(expected.getUsername(), actual.getUsername());
        Assert.assertEquals(expected.getPassword(), actual.getPassword());
    }

    @Test
    public void getRequets_doesntContainPassword_returnNull() throws HttpMalformedHeaderException, URIException {
        HttpMessage message = new HttpMessage();
        message.setRequestHeader(requestHeaderGet_noPassword);

        RequestCredentialScannerService service = new RequestCredentialScannerService();
        Credentials actual = service.getCredentialsInRequest(message);

        boolean isPost = service.isPost(message);
        Assert.assertFalse(isPost);
        Assert.assertNull(actual);
    }

    @Test
    public void recognizePostRequest_returnTrue() throws HttpMalformedHeaderException {
        RequestCredentialScannerService service = new RequestCredentialScannerService();
        HttpMessage message = new HttpMessage();
        message.setRequestHeader(requestPost_withPassword_head);
        message.setRequestBody(requestPost_withPassword_body);

        boolean isAllowed = service.isAllowedMethod(message.getRequestHeader().getMethod());
        Assert.assertTrue(isAllowed);

        boolean isPost = service.isPost(message);
        Assert.assertTrue(isPost);
    }

    @Test
    public void postRequest_containsPassword_returnCredentials() throws HttpMalformedHeaderException, URIException {
        Credentials expected = new Credentials("myserver.com", "myUsername", "MyPasswort");

        HttpMessage message = new HttpMessage();
        message.setRequestHeader(requestPost_withPassword_head);
        message.setRequestBody(requestPost_withPassword_body);

        RequestCredentialScannerService service = new RequestCredentialScannerService();
        Credentials actual = service.getCredentialsInRequest(message);

        Assert.assertEquals(expected.getHost(), actual.getHost());
        Assert.assertEquals(expected.getUsername(), actual.getUsername());
        Assert.assertEquals(expected.getPassword(), actual.getPassword());
    }

    @Test
    public void postRequest_partialCredentials_returnNull() throws HttpMalformedHeaderException, URIException {
        HttpMessage message = new HttpMessage();
        message.setRequestHeader(requestPost_withPassword_head);
        message.setRequestBody(requestPost_withPartialCredentials_body);

        RequestCredentialScannerService service = new RequestCredentialScannerService();
        Credentials actual = service.getCredentialsInRequest(message);

        boolean isPost = service.isPost(message);
        Assert.assertTrue(isPost);
        Assert.assertNull(actual);
    }
}
