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
    public void SwappedCharacterStrategy_test1() {
        SwappedCharacterStrategy strategy = new SwappedCharacterStrategy();

        String host = "google.com";
        String candidate = "google.com";
        boolean result = strategy.applyStrategy(host, candidate);

        Assert.assertFalse(result);
    }

    @Test
    public void SwappedCharacterStrategy_test2() {
        SwappedCharacterStrategy strategy = new SwappedCharacterStrategy();

        String host = "googel.com";
        String candidate = "google.com";
        boolean result = strategy.applyStrategy(host, candidate);

        Assert.assertTrue(result);
    }

    @Test
    public void SwappedCharacterStrategy_test3() {
        SwappedCharacterStrategy strategy = new SwappedCharacterStrategy();

        String host = "google.cmo";
        String candidate = "google.com";
        boolean result = strategy.applyStrategy(host, candidate);

        Assert.assertTrue(result);
    }

    @Test
    public void SwappedCharacterStrategy_test4() {
        SwappedCharacterStrategy strategy = new SwappedCharacterStrategy();

        String host = "gogle.com";
        String candidate = "google.com";
        boolean result = strategy.applyStrategy(host, candidate);

        Assert.assertFalse(result);
    }

    @Test
    public void SwappedCharacterStrategy_test5() {
        SwappedCharacterStrategy strategy = new SwappedCharacterStrategy();

        String host = "gooegl.com";
        String candidate = "google.com";
        boolean result = strategy.applyStrategy(host, candidate);

        Assert.assertFalse(result);
    }

    @Test
    public void SwappedCharacterStrategy_test6() {
        SwappedCharacterStrategy strategy = new SwappedCharacterStrategy();

        String host = "fajsgyghed";
        String candidate = "google.com";
        boolean result = strategy.applyStrategy(host, candidate);

        Assert.assertFalse(result);
    }

    @Test
    public void SwappedCharacterStrategy_test7() {
        SwappedCharacterStrategy strategy = new SwappedCharacterStrategy();

        String host = "googlec.om";
        String candidate = "google.com";
        boolean result = strategy.applyStrategy(host, candidate);

        Assert.assertTrue(result);
    }

}