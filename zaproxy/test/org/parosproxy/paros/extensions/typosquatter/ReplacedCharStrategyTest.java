package org.parosproxy.paros.extensions.typosquatter;

import org.junit.Assert;
import org.junit.Test;
import org.parosproxy.paros.extension.typosquatter.strategies.ReplacedCharStrategy;

public class ReplacedCharStrategyTest {

    ReplacedCharStrategy strat = new ReplacedCharStrategy();

    @Test
    public void testExactMatch(){

        String host = "google.com";
        String cand = "google.com";
        boolean res = strat.applyStrategy(host, cand);

        Assert.assertFalse(res);
    }

    @Test
    public void test1ReplacedChar(){
        String host = "googke.com";
        String cand = "google.com";
        boolean res = strat.applyStrategy(host, cand);

        Assert.assertTrue(res);
    }

    @Test
    public void test1ReplacedChar2(){
        String host = "google.con";
        String cand = "google.com";
        boolean res = strat.applyStrategy(host, cand);

        Assert.assertTrue(res);
    }

    @Test
    public void test2ReplacedChar(){
        String host = "gbbgle.com";
        String cand = "google.com";
        boolean res = strat.applyStrategy(host, cand);

        Assert.assertFalse(res);
    }

   @Test
    public void test1missingChar(){
        String host = "googl.com";
        String cand = "google.com";
        boolean res = strat.applyStrategy(host, cand);

        Assert.assertFalse(res);
    }

    @Test
    public void test1extraChar(){
        String host = "googlee.com";
        String cand = "google.com";
        boolean res = strat.applyStrategy(host, cand);

        Assert.assertFalse(res);
    }
}