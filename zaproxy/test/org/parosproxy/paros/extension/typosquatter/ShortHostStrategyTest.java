package org.parosproxy.paros.extension.typosquatter;

import org.junit.Assert;
import org.junit.Test;
import org.parosproxy.paros.extension.typosquatter.strategies.ShortHostStrategy;

public class ShortHostStrategyTest {

    @Test
    public void testRegexBuild_ShortHostStrategy() throws Exception{
        ShortHostStrategy strategy = new ShortHostStrategy();

        String expected = ".?g.?o.?o.?g.?l.?e.?\\..?c.?o.?m.?";
        String actual = strategy.getRegex("google.com");

        Assert.assertEquals(expected, actual);
    }

    // todo match correct length
    @Test
    public void testRegexMatch_ShortHostStrategy_HostEqualsCandidate() {
        ShortHostStrategy strategy = new ShortHostStrategy();

        String host = "google.com";
        String candidate = "google.com";
        boolean result = strategy.applyStrategy(host, candidate);

        Assert.assertFalse(result);
    }

    @Test
    public void testRegexMatch_ShortHostStrategy_TyposquattMinus1() {
        ShortHostStrategy strategy = new ShortHostStrategy();

        String host = "google.com";
        String candidate = "google.co";
        boolean result = strategy.applyStrategy(host, candidate);

        Assert.assertFalse(result);
    }

    @Test
    public void testRegexMatch_ShortHostStrategy_TyposquattMinus2() {
        ShortHostStrategy strategy = new ShortHostStrategy();

        String host = "google.com";
        String candidate = "google.c";
        boolean result = strategy.applyStrategy(host, candidate);

        Assert.assertFalse(result);
    }

    @Test
    public void testRegexMatch_ShortHostStrategy_TyposquattPlus1() {
        ShortHostStrategy strategy = new ShortHostStrategy();

        String host = "google.com";
        String candidate = "google.coma";
        boolean result = strategy.applyStrategy(host, candidate);

        Assert.assertTrue(result);
    }

    @Test
    public void testRegexMatch_ShortHostStrategy_TyposquattPlus2() {
        ShortHostStrategy strategy = new ShortHostStrategy();

        String host = "google.com";
        String candidate = "googl1e.coma";
        boolean result = strategy.applyStrategy(host, candidate);

        Assert.assertFalse(result);
    }

    @Test
    public void testRegexMatch_ShortHostStrategy_TyposquattPlus1Middle() {
        ShortHostStrategy strategy = new ShortHostStrategy();

        String host = "google.com";
        String candidate = "googl1e.com";
        boolean result = strategy.applyStrategy(host, candidate);

        Assert.assertTrue(result);
    }

}
