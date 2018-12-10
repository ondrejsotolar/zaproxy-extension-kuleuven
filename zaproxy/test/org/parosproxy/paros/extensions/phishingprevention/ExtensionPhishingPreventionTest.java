package org.parosproxy.paros.extensions.phishingprevention;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.parosproxy.paros.extension.phishingprevention.*;
import org.parosproxy.paros.extension.phishingprevention.persistence.MemoryPersistenceService;
import org.parosproxy.paros.extension.phishingprevention.requestscan.OverrideListener;
import org.parosproxy.paros.network.HttpMessage;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
public class ExtensionPhishingPreventionTest {

    @Test
    public void noCredentialsInRequest_returnTrue() {
        // setup services
        CredentialScanerService mockCrecentialService = mock(CredentialScanerService.class);
        when(mockCrecentialService.getCredentialsInRequest(any()))
                .thenReturn(null);

        PersistenceService mockPersistenceService = mock(PersistenceService.class);
        IPasswordHygieneService passwordHygieneService = new PasswordHygieneService();
        OverrideListener overrideListener = new OverrideListener(
                mockCrecentialService, passwordHygieneService, mockPersistenceService);
        overrideListener.setON(true);

        HttpMessage msg = new HttpMessage();
        msg.setRequestBody("");

        // assert
        Assert.assertFalse(overrideListener.onHttpRequestSend(msg));
    }

    @Test
    public void newCredentialsInRequest_saveCredAndRetWarningSite_hygieneOff() {
        // setup services
        Logger.getRootLogger().setLevel(Level.OFF);

        Credentials requestCreds = new Credentials("test.com", "tom", "pass");
        CredentialScanerService mockCrecentialService = mock(CredentialScanerService.class);
        when(mockCrecentialService.getCredentialsInRequest(any()))
                .thenReturn(requestCreds);

        PersistenceService memoryPersistenceService = new MemoryPersistenceService();
        IPasswordHygieneService passwordHygieneService = new PasswordHygieneService();
        OverrideListener overrideListener = new OverrideListener(
                mockCrecentialService, passwordHygieneService, memoryPersistenceService);
        overrideListener.setON(true);

        HttpMessage msg = new HttpMessage();
        msg.setRequestBody("");

        // assert
        Assert.assertTrue(overrideListener.getRequestCache().getRequestCache().size() == 0);
        Assert.assertTrue(overrideListener.onHttpRequestSend(msg));
        Assert.assertTrue(overrideListener.getRequestCache().getRequestCache().size() == 1);
        Assert.assertTrue(overrideListener.getRequestCache().getRequestCache().containsKey(msg));

        //Assert.assertTrue(memoryPersistenceService.get(requestCreds.getHost()).isHostWhitelisted() == false);

        String reqParam = "<input type=\"hidden\" name=\"%s\" value=\"%d\" />";
        Assert.assertTrue(msg.getResponseBody().toString().contains(
                String.format(reqParam, overrideListener.REQUEST_ID,
                        overrideListener.getRequestCache().getRequestCounter()-1)));

        String hostParam = "<input type=\"hidden\" name=\"%s\" value=\"%s\" />";
        Assert.assertTrue(msg.getResponseBody().toString().contains(
                String.format(hostParam, overrideListener.HOST_KEYWORD, requestCreds.getHost())));
    }

    @Test
    public void warningSite_confirm_updateStoreAndResendRequest() {

    }

    @Test
    public void warningSite_cancel_removeFromStoreAndShowCancelPage() {

    }

    @Test
    public void reqCredsInStore_allowed_returnTrue() {

    }

    @Test
    public void reqCredsInStore_notAllowed_throw() {

    }

    // TODO: implement hygiene service
    public void hygieneCheckCatch_throwException() {

        // setup services
        Credentials foundCredentials = new Credentials("host", "username", "123456789");
        CredentialScanerService mockCrecentialService = mock(CredentialScanerService.class);
        when(mockCrecentialService.getCredentialsInRequest(any()))
                .thenReturn(foundCredentials);

        IPasswordHygieneService passwordHygieneService = new PasswordHygieneService();
        PersistenceService mockPersistenceService = mock(PersistenceService.class);
        OverrideListener overrideListener = new OverrideListener(
                mockCrecentialService, passwordHygieneService, mockPersistenceService);
        overrideListener.setON(true);

        // run
        boolean thrown = false;
        try {
            overrideListener.onHttpRequestSend(new HttpMessage());
        } catch (PhishingPreventionException e) {
            thrown = true;
        }

        // assert
        Assert.assertTrue(thrown);
    }


    @Test
    public void stringParamTest() {
        CredentialScanerService service = new RequestCredentialScannerService();
        String body1 = "";
        String body2 = "username=tom";
        String body3 = "id=1";
        String body4 = "username=tom&id=1";
        String body5 = "username=tom&id=1&pass=123";

        Assert.assertNull(service.getParamStringFromBody(body1, "any"));
        Assert.assertTrue(service.getParamIntFromBody(body1, "any") == -1);

        Assert.assertEquals("tom", service.getParamStringFromBody(body2, "username"));

        Assert.assertEquals(1, service.getParamIntFromBody(body3, "id"));

        Assert.assertEquals("tom", service.getParamStringFromBody(body4, "username"));
        Assert.assertEquals(1, service.getParamIntFromBody(body4, "id"));

        Assert.assertEquals("tom", service.getParamStringFromBody(body5, "username"));
        Assert.assertEquals(1, service.getParamIntFromBody(body5, "id"));
    }

}
