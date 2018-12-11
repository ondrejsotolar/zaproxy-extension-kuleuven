package org.parosproxy.paros.extensions.phishingprevention;

import org.junit.Assert;
import org.junit.Test;
import org.parosproxy.paros.core.scanner.Alert;
import org.parosproxy.paros.extension.phishingprevention.CrackLib.CrackLib;
import org.parosproxy.paros.extension.phishingprevention.Credentials;
import org.parosproxy.paros.extension.phishingprevention.IPasswordHygieneService;
import org.parosproxy.paros.extension.phishingprevention.PasswordHygieneResult;
import org.parosproxy.paros.extension.phishingprevention.PasswordHygieneService;
import org.parosproxy.paros.extension.phishingprevention.strategies.CrackLibTestStrategy;
import org.parosproxy.paros.extension.phishingprevention.strategies.OnlyNumbersStrategy;
import org.parosproxy.paros.extension.phishingprevention.strategies.CommonPasswordsStrategy;

public class PhishingPreventionStrategiesTest {

    @Test
    public void catchAllNumbers() {

        OnlyNumbersStrategy expectedStrategy = new OnlyNumbersStrategy();
        Credentials foundCredentials = new Credentials("host", "username", "123456789");
        IPasswordHygieneService passwordHygieneService = new PasswordHygieneService();

        PasswordHygieneResult result = passwordHygieneService.checkPasswordHygiene(foundCredentials);

        Assert.assertTrue(result.getResult());
        Assert.assertTrue(result.getMapMsgFailedStrategies().keySet().contains(expectedStrategy.getName()));
    }

    @Test
    public void isIn500WorstPasswords() {

        CommonPasswordsStrategy expectedStrategy = new CommonPasswordsStrategy();
        Credentials foundCredentials = new Credentials("host", "username", "amateur");
        IPasswordHygieneService passwordHygieneService = new PasswordHygieneService();

        PasswordHygieneResult result = passwordHygieneService.checkPasswordHygiene(foundCredentials);

        Assert.assertTrue(result.getResult());
        Assert.assertTrue(result.getMapMsgFailedStrategies().keySet().contains(expectedStrategy.getName()));
    }

    @Test
    public void failsCracklibTest() {

        CrackLibTestStrategy clStrategy = new CrackLibTestStrategy();
        Credentials foundCredentials = new Credentials("host", "username", "123456789");
        IPasswordHygieneService passwordHygieneService = new PasswordHygieneService();
        PasswordHygieneResult result = passwordHygieneService.checkPasswordHygiene(foundCredentials);

        String testRes = clStrategy.applyStrategy("aarhus");
        Assert.assertTrue(testRes != null);

        testRes = clStrategy.applyStrategy("aarhus123213");
        Assert.assertTrue(testRes != null);

        testRes = clStrategy.applyStrategy("Elephant");
        Assert.assertTrue(testRes != null);

        testRes = clStrategy.applyStrategy("Elephant42");
        Assert.assertTrue(testRes != null);

        testRes = clStrategy.applyStrategy("fE123123f");
        Assert.assertTrue(testRes != null);

        // TODO: case fails. why?
        testRes = clStrategy.applyStrategy("a2sdFGJ12");
        Assert.assertFalse(testRes == null);

        //Assert.assertTrue(result.getResult());
        //Assert.assertTrue(result.getFailedStrategies().contains(expectedStrategy.getName()));
    }
}
