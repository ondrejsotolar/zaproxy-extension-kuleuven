package org.parosproxy.paros.extensions.phishingprevention;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.parosproxy.paros.extension.phishingprevention.*;
import org.parosproxy.paros.network.HttpMessage;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
public class ExtensionPhishingPreventionTest {

    @Test
    public void onHttpRequest_noCredentialsInRequest_returnTrue() {

        HttpMessage msg = new HttpMessage();
        msg.setRequestBody("");

        // setup services
        CredentialScanerService mockCrecentialService = mock(CredentialScanerService.class);
        when(mockCrecentialService.getCredentialsInRequest(any()))
                .thenReturn(null);

        PersistenceService mockPersistenceService = mock(PersistenceService.class);
        IPasswordHygieneService passwordHygieneService = new PasswordHygieneService();
        ExtensionPhishingPrevention extension = new ExtensionPhishingPrevention(
                mockCrecentialService, passwordHygieneService, mockPersistenceService);
        extension.setON(true);

        // assert
        Assert.assertTrue(extension.onHttpRequestSend(msg));
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
            extension.onHttpRequestSend(new HttpMessage());
        } catch (PhishingPreventionException e) {
            thrown = true;
        }

        // assert
        Assert.assertTrue(thrown);
    }


}
