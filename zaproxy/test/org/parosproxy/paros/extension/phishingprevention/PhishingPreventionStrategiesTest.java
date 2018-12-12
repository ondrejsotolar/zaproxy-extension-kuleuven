package org.parosproxy.paros.extension.phishingprevention;

import org.junit.Assert;
import org.junit.Test;
import org.parosproxy.paros.extension.phishingprevention.hygiene.PasswordHygieneResult;
import org.parosproxy.paros.extension.phishingprevention.hygiene.PasswordHygieneService;
import org.parosproxy.paros.extension.phishingprevention.hygiene.CrackLibTestStrategy;
import org.parosproxy.paros.extension.phishingprevention.hygiene.OnlyNumbersStrategy;
import org.parosproxy.paros.extension.phishingprevention.hygiene.CommonPasswordsStrategy;

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
        
        testRes = clStrategy.applyStrategy("a2sdFGJ12");
        Assert.assertTrue(testRes == null);

        //Assert.assertTrue(result.getResult());
        //Assert.assertTrue(result.getFailedStrategies().contains(expectedStrategy.getName()));
    }
}
