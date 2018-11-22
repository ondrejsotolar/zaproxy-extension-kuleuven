package org.parosproxy.paros.extensions.typosquatter;

import org.junit.Assert;
import org.junit.Test;
import org.parosproxy.paros.extension.typosquatter.strategies.SwappedCharacterStrategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SwappedCharacterStrategyTest {

    @Test
    public void testRegexBuild_SwappedCharacterStrategy() throws Exception {
        SwappedCharacterStrategy strategy = new SwappedCharacterStrategy();

        String expected = "google.com";
        String actual = strategy.getCandidate("google.com");

        Assert.assertEquals(expected, actual);
    }

    // todo match correct length
    @Test
    public void SwappedCharacterStrategy_HostEqualsCandidate1() {
        SwappedCharacterStrategy strategy = new SwappedCharacterStrategy();

        String host = "google.com";
        String candidate = "google.com";
        boolean result = strategy.applyStrategy(host, candidate);

        Assert.assertFalse(result);
    }

    @Test
    public void SwappedCharacterStrategy_HostEqualsCandidate2() {
        SwappedCharacterStrategy strategy = new SwappedCharacterStrategy();

        String host = "google.com";
        String candidate = "googel.com";
        boolean result = strategy.applyStrategy(host, candidate);

        Assert.assertTrue(result);
    }
}