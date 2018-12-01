package org.parosproxy.paros.extensions.typosquatter;

import org.junit.Assert;
import org.junit.Test;
import org.parosproxy.paros.extension.typosquatter.strategies.LongHostStrategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LongHostStrategyTest {

    @Test
    public void testRegexBuild_LongHostStrategy() throws Exception{
        LongHostStrategy strategy = new LongHostStrategy();

        String expected = "(?=[google.com]{9})g?o?o?g?l?e?.?c?o?m?";
        String actual = strategy.getRegex("google.com");

        Assert.assertEquals(expected, actual);
    }

    // todo match correct length
    @Test
    public void testRegexMatch_LongHostStrategy_HostEqualsCandidate() {
        LongHostStrategy strategy = new LongHostStrategy();

        String host = "google.com";
        String candidate = "google.com";
        boolean result = strategy.applyStrategy(host, candidate);

        Assert.assertFalse(result);
    }

    @Test
    public void testRegexMatch_LongHostStrategy_TyposquattMinus1() {
        LongHostStrategy strategy = new LongHostStrategy();

        String host = "google.com";
        String candidate = "google.co";
        boolean result = strategy.applyStrategy(host, candidate);

        Assert.assertTrue(result);
    }

    @Test
    public void testRegexMatch_LongHostStrategy_TyposquattMinus2() {
        LongHostStrategy strategy = new LongHostStrategy();

        String host = "google.com";
        String candidate = "google.c";
        boolean result = strategy.applyStrategy(host, candidate);

        Assert.assertFalse(result);
    }

    @Test
    public void testRegexMatch_LongHostStrategy_TyposquattPlus1() {
        LongHostStrategy strategy = new LongHostStrategy();

        String host = "google.com";
        String candidate = "google.comm";
        boolean result = strategy.applyStrategy(host, candidate);

        Assert.assertFalse(result);
    }

    @Test
    public void myJavaPlayground() {
        Map<String, List<String>> map = new HashMap<>();
        Assert.assertTrue(map.size() == 0);

        List<String> list = new ArrayList<>();
        list.add("1");
        map.put("key", list);
        Assert.assertTrue(map.size() == 1);

        map.get("key").add("2");
        Assert.assertTrue(map.get("key").size() == 2);
        Assert.assertTrue(map.size() == 1);

        List<String> list2 = new ArrayList<>();
        list.add("1");
        map.put("key2", list);
        Assert.assertTrue(map.size() == 2);

        Map<String, List<String>> queryParameters = new HashMap<>();
        List<String> r1 = map.get("bbb");
        Assert.assertNull(r1);

        map.put("aaa", new ArrayList<String>());
        List<String> r2 = map.get("aaa");
        r2.add("val");
        Assert.assertTrue(map.get("aaa").size() == 1);


    }
}
