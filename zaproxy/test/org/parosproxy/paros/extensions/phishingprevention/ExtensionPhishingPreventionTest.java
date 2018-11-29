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
    public void hygieneCheckCatch_throwException() {

        // setup services
        Credentials foundCredentials = new Credentials("host", "username", "123456789");
        CredentialScanerService mockCrecentialService = mock(CredentialScanerService.class);
        when(mockCrecentialService.getCredentialsInRequest(any()))
                .thenReturn(foundCredentials);

        IPasswordHygieneService passwordHygieneService = new PasswordHygieneService();
        ExtensionPhishingPrevention t = new ExtensionPhishingPrevention(
                mockCrecentialService, passwordHygieneService);
        t.setON(true);

        // run
        boolean thrown = false;
        try {
            t.onHttpRequestSend(new HttpMessage());
        } catch (PhishingPreventionException e) {
            thrown = true;
        }

        // assert
        Assert.assertTrue(thrown);
    }


}
