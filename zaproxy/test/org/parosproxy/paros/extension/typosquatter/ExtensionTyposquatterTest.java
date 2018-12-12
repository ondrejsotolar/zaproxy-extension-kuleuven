package org.parosproxy.paros.extension.typosquatter;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.parosproxy.paros.network.HttpMalformedHeaderException;
import org.parosproxy.paros.network.HttpMessage;
import org.parosproxy.paros.network.HttpRequestHeader;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;

import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
public class ExtensionTyposquatterTest {

    @Test
    public void onHttpRequestSend_passingRequest() throws HttpMalformedHeaderException {
        // setup services
        String candidate = "google.com";

        ITyposquattingService mockTSservice = mock(ITyposquattingService.class);
        when(mockTSservice.checkCandidateHost(candidate))
                .thenReturn(new TyposquattingResult(candidate));

        PersistanceService mockPersistanceService = mock(PersistanceService.class);
        when(mockPersistanceService.getWhitelistFile(any(), any()))
                .thenReturn(new File(""));

        ExtensionTyposquatter t = new ExtensionTyposquatter(
                mockTSservice, mockPersistanceService);
        t.setON(true);

        HttpMessage msg = new HttpMessage();
        msg.setRequestHeader(new HttpRequestHeader("GET /hello.htm HTTP/1.1\n" +
                "Host: "+candidate));

        // catch sent request
        boolean result = t.onHttpRequestSend(msg);

        // assert
        Assert.assertTrue(result);
        Assert.assertTrue(t.getRequestCache().isEmpty());
        Assert.assertEquals(t.getRequestCounter(), 0);
    }

    @Test
    public void onHttpRequestSend_typoRequest_returnResultPage() throws HttpMalformedHeaderException {
        String candidate = "google.com";

        ITyposquattingService mockTSservice = mock(ITyposquattingService.class);
        TyposquattingResult result = new TyposquattingResult(candidate);
        result.addFailedTyposquattingStrategy(candidate, "someStrategy");
        when(mockTSservice.checkCandidateHost(candidate)).thenReturn(result);

        PersistanceService mockPersistanceService = mock(PersistanceService.class);
        when(mockPersistanceService.getWhitelistFile(any(), any())).thenReturn(new File(""));

        ExtensionTyposquatter t = new ExtensionTyposquatter(mockTSservice, mockPersistanceService);
        t.setON(true);

        HttpMessage msg = new HttpMessage();
        msg.setRequestHeader(new HttpRequestHeader("GET /hello.htm HTTP/1.1\n" +
                "Host: "+candidate));

        // catch sent request
        boolean thrown = false;
        try {
            t.onHttpRequestSend(msg);
        }
        catch (TyposquattingException e) {
            thrown = true;
        }

        // assert
        Assert.assertTrue(t.getRequestCache().containsKey(msg));
        Assert.assertEquals(t.getRequestCounter(), 1);

        boolean result2 = t.onHttpResponseReceive(msg);
        Assert.assertTrue(result2);
        Assert.assertTrue(msg.getResponseBody().toString().contains("<title>Blocked by proxy</title>"));
    }
}
