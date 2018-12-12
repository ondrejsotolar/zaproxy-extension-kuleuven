package org.parosproxy.paros.extension.typosquatter;

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

    @Test
    public void testStringDiff0(){
        String host = "google.com";
        String cand = "google.com";
        int res = strat.checkString(host,cand);

        Assert.assertEquals(res, 0);
    }

    @Test
    public void testStringDiff1(){
        String host = "gobgle.com";
        String cand = "google.com";
        int res = strat.checkString(host,cand);

        Assert.assertEquals(res, 1);
    }

    @Test
    public void testStringDiff2(){
        String host = "gbbgle.com";
        String cand = "google.com";
        int res = strat.checkString(host,cand);

        Assert.assertEquals(res, 2);
    }

    @Test
    public void testStringDiff1a(){
        String host = "google,com";
        String cand = "google.com";
        int res = strat.checkString(host,cand);

        Assert.assertEquals(res, 1);
    }

    @Test
    public void testStringDiff10(){
        String host = "asdfgjklut";
        String cand = "google.com";
        int res = strat.checkString(host,cand);

        Assert.assertEquals(res, 10);
    }

}