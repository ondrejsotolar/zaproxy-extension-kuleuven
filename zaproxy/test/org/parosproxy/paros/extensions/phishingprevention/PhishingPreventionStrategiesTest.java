package org.parosproxy.paros.extensions.phishingprevention;

import org.junit.Assert;
import org.junit.Test;
import org.parosproxy.paros.extension.phishingprevention.Credentials;
import org.parosproxy.paros.extension.phishingprevention.IPasswordHygieneService;
import org.parosproxy.paros.extension.phishingprevention.PasswordHygieneResult;
import org.parosproxy.paros.extension.phishingprevention.PasswordHygieneService;
import org.parosproxy.paros.extension.phishingprevention.strategies.OnlyNumbersStrategy;

public class PhishingPreventionStrategiesTest {

    @Test
    public void catchAllNumbers() {

        OnlyNumbersStrategy expectedStrategy = new OnlyNumbersStrategy();
        Credentials foundCredentials = new Credentials("host", "username", "123456789");
        IPasswordHygieneService passwordHygieneService = new PasswordHygieneService();

        PasswordHygieneResult result = passwordHygieneService.checkPasswordHygiene(foundCredentials);

        Assert.assertTrue(result.getResult());
        Assert.assertTrue(result.getFailedStrategies().contains(expectedStrategy.getName()));
    }

    //@Test TODO: implement
    public void isIn500WorstPasswords() {

        //500WorstPasswordStrategy expectedStrategy = new 500WorstPasswordStrategy();
        Credentials foundCredentials = new Credentials("host", "username", "123456789");
        IPasswordHygieneService passwordHygieneService = new PasswordHygieneService();

        PasswordHygieneResult result = passwordHygieneService.checkPasswordHygiene(foundCredentials);

        Assert.assertTrue(result.getResult());
        //Assert.assertTrue(result.getFailedStrategies().contains(expectedStrategy.getName()));
    }

    //@Test TODO: implement
    public void failsCracklibTest() {

        //CracklibStrategy expectedStrategy = new CracklibStrategy();
        Credentials foundCredentials = new Credentials("host", "username", "123456789");
        IPasswordHygieneService passwordHygieneService = new PasswordHygieneService();

        PasswordHygieneResult result = passwordHygieneService.checkPasswordHygiene(foundCredentials);

        Assert.assertTrue(result.getResult());
        //Assert.assertTrue(result.getFailedStrategies().contains(expectedStrategy.getName()));
    }
}
