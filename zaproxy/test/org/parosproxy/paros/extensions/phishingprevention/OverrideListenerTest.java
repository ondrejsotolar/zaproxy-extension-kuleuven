package org.parosproxy.paros.extensions.phishingprevention;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.parosproxy.paros.extension.phishingprevention.*;
import org.parosproxy.paros.extension.phishingprevention.persistence.FilePersistenceService;
import org.parosproxy.paros.extension.phishingprevention.persistence.TextFileStorage;
import org.parosproxy.paros.extension.phishingprevention.OverrideListener;
import org.parosproxy.paros.extension.phishingprevention.hygiene.PasswordHygieneService;
import org.parosproxy.paros.network.HttpMessage;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
public class OverrideListenerTest {
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

        TextFileStorage fps = mock(TextFileStorage.class);
        when(fps.loadStoredCredentials()).thenReturn(new ArrayList<>());

        PersistenceService persistenceService = new FilePersistenceService(fps);
        IPasswordHygieneService passwordHygieneService = new PasswordHygieneService();
        OverrideListener overrideListener = new OverrideListener(
                mockCrecentialService, passwordHygieneService, persistenceService);
        overrideListener.setON(true);

        HttpMessage msg = new HttpMessage();
        msg.setRequestBody("");

        // assert
        Assert.assertTrue(overrideListener.getRequestCache().getRequestCache().size() == 0);
        Assert.assertTrue(overrideListener.onHttpRequestSend(msg));
        Assert.assertTrue(overrideListener.getRequestCache().getRequestCache().size() == 1);
        Assert.assertTrue(overrideListener.getRequestCache().getRequestCache().containsKey(msg));

        Assert.assertFalse(persistenceService.get(
                requestCreds.getHost(), requestCreds.getUsername()).isHostWhitelisted());

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
        // setup services
        Logger.getRootLogger().setLevel(Level.OFF);

        Credentials requestCreds = new Credentials("test.com", "tom", "pass");
        CredentialScanerService mockCrecentialService = mock(CredentialScanerService.class);
        when(mockCrecentialService.getCredentialsInRequest(any()))
                .thenReturn(requestCreds);

        TextFileStorage fps = mock(TextFileStorage.class);
        when(fps.loadStoredCredentials()).thenReturn(new ArrayList<>());

        PersistenceService persistenceService = new FilePersistenceService(fps);
        IPasswordHygieneService passwordHygieneService = new PasswordHygieneService();
        OverrideListener overrideListener = new OverrideListener(
                mockCrecentialService, passwordHygieneService, persistenceService);
        overrideListener.setON(true);

        // act
        HttpMessage originalRequest = new HttpMessage();
        String expectedBody = "thats_me";
        originalRequest.setRequestBody(expectedBody);
        overrideListener.onHttpRequestSend(originalRequest);

        HttpMessage controlRequest = new HttpMessage();
        controlRequest.setRequestBody("phishing_prevention_cr=true&save=true&request_id=0");

        // assert
        Assert.assertFalse(overrideListener.onHttpRequestSend(controlRequest));
        Assert.assertEquals(expectedBody, controlRequest.getRequestBody().toString());
        Assert.assertTrue(
                persistenceService.get(requestCreds.getHost(), requestCreds.getUsername())
                        .isHostWhitelisted());
    }

    @Test
    public void warningSite_cancel_removeFromStoreAndShowCancelPage() {
        // setup services
        Logger.getRootLogger().setLevel(Level.OFF);

        Credentials requestCreds = new Credentials("test.com", "tom", "pass");
        CredentialScanerService mockCrecentialService = mock(CredentialScanerService.class);
        when(mockCrecentialService.getCredentialsInRequest(any()))
                .thenReturn(requestCreds);

        TextFileStorage fps = mock(TextFileStorage.class);
        when(fps.loadStoredCredentials()).thenReturn(new ArrayList<>());

        PersistenceService persistenceService = new FilePersistenceService(fps);
        IPasswordHygieneService passwordHygieneService = new PasswordHygieneService();
        OverrideListener overrideListener = new OverrideListener(
                mockCrecentialService, passwordHygieneService, persistenceService);
        overrideListener.setON(true);

        // act
        HttpMessage originalRequest = new HttpMessage();
        String expectedBody = "thats_me";
        originalRequest.setRequestBody(expectedBody);
        overrideListener.onHttpRequestSend(originalRequest);
        Assert.assertTrue(overrideListener.getRequestCache().getRequestCache().size() == 1);
        Assert.assertNotNull(overrideListener.getRequestCache().getRequestById(0));

        HttpMessage controlRequest = new HttpMessage();
        controlRequest.setRequestBody("phishing_prevention_cr=true&save=false&request_id=0");

        // assert
        Assert.assertFalse(overrideListener.onHttpRequestSend(controlRequest));
        Assert.assertNull(
                persistenceService.get(requestCreds.getHost(), requestCreds.getUsername()));
        Assert.assertTrue(controlRequest.getResponseBody().toString().contains("Login canceled!"));
        Assert.assertTrue(overrideListener.getRequestCache().getRequestCache().size() == 0);
    }

    @Test
    public void reqCredsInStore_hostWhitelisted_requestUnchanged() {
        // setup services
        Logger.getRootLogger().setLevel(Level.OFF);

        Credentials requestCreds = new Credentials("test.com", "tom", "pass");
        CredentialScanerService mockCrecentialService = mock(CredentialScanerService.class);
        when(mockCrecentialService.getCredentialsInRequest(any()))
                .thenReturn(requestCreds);

        PersistenceService memoryPersistenceService = new FilePersistenceService();
        memoryPersistenceService.saveOrUpdate(requestCreds, true, false);

        IPasswordHygieneService passwordHygieneService = new PasswordHygieneService();
        OverrideListener overrideListener = new OverrideListener(
                mockCrecentialService, passwordHygieneService, memoryPersistenceService);
        overrideListener.setON(true);

        // act
        HttpMessage originalRequest = new HttpMessage();
        String expectedBody = "thats_me";
        originalRequest.setRequestBody(expectedBody);
        String expectedPassHash = memoryPersistenceService
                .get(requestCreds.getHost(), requestCreds.getUsername()).getPassword();
        // assert

        Assert.assertFalse(overrideListener.onHttpRequestSend(originalRequest));
        Assert.assertEquals(expectedBody, originalRequest.getRequestBody().toString());
        Assert.assertEquals(expectedPassHash, memoryPersistenceService
                .get(requestCreds.getHost(), requestCreds.getUsername()).getPassword());
    }
}
