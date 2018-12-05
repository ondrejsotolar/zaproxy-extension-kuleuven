package org.parosproxy.paros.extensions.phishingprevention;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.parosproxy.paros.extension.phishingprevention.*;
import org.parosproxy.paros.extension.phishingprevention.persistence.MemoryPersistenceService;
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
        ExtensionPhishingPrevention extension = new ExtensionPhishingPrevention(
                mockCrecentialService, passwordHygieneService, mockPersistenceService);
        extension.setON(true);

        HttpMessage msg = new HttpMessage();
        msg.setRequestBody("");

        // assert
        Assert.assertFalse(extension.getNewOverrideListener().onHttpRequestSend(msg));
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
        ExtensionPhishingPrevention extension = new ExtensionPhishingPrevention(
                mockCrecentialService, passwordHygieneService, memoryPersistenceService);
        extension.setON(true);

        HttpMessage msg = new HttpMessage();
        msg.setRequestBody("");

        // assert
        Assert.assertTrue(extension.getRequestCache().size() == 0);
        Assert.assertTrue(extension.getNewOverrideListener().onHttpRequestSend(msg));
        Assert.assertTrue(extension.getRequestCache().size() == 1);
        Assert.assertTrue(extension.getRequestCache().containsKey(msg));

        Assert.assertTrue(memoryPersistenceService.get(requestCreds.getHost()).isAllow() == false);

        String reqParam = "<input type=\"hidden\" name=\"%s\" value=\"%d\" />";
        Assert.assertTrue(msg.getResponseBody().toString().contains(
                String.format(reqParam, extension.REQUEST_ID, extension.getRequestCounter()-1)));

        String hostParam = "<input type=\"hidden\" name=\"%s\" value=\"%s\" />";
        Assert.assertTrue(msg.getResponseBody().toString().contains(
                String.format(hostParam, extension.HOST_KEYWORD, requestCreds.getHost())));
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
        ExtensionPhishingPrevention extension = new ExtensionPhishingPrevention(
                mockCrecentialService, passwordHygieneService, mockPersistenceService);
        extension.setON(true);

        // run
        boolean thrown = false;
        try {
            extension.getNewOverrideListener().onHttpRequestSend(new HttpMessage());
        } catch (PhishingPreventionException e) {
            thrown = true;
        }

        // assert
        Assert.assertTrue(thrown);
    }


    @Test
    public void stringParamTest() {
        ExtensionPhishingPrevention extension = new ExtensionPhishingPrevention();
        String body1 = "";
        String body2 = "username=tom";
        String body3 = "id=1";
        String body4 = "username=tom&id=1";
        String body5 = "username=tom&id=1&pass=123";

        Assert.assertNull(extension.getParamStringFromBody(body1, "any"));
        Assert.assertTrue(extension.getParamIntFromBody(body1, "any") == -1);

        Assert.assertEquals("tom", extension.getParamStringFromBody(body2, "username"));

        Assert.assertEquals(1, extension.getParamIntFromBody(body3, "id"));

        Assert.assertEquals("tom", extension.getParamStringFromBody(body4, "username"));
        Assert.assertEquals(1, extension.getParamIntFromBody(body4, "id"));

        Assert.assertEquals("tom", extension.getParamStringFromBody(body5, "username"));
        Assert.assertEquals(1, extension.getParamIntFromBody(body5, "id"));
    }

}
