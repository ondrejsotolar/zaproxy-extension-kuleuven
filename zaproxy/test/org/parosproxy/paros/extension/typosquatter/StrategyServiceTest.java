package org.parosproxy.paros.extension.typosquatter;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
public class StrategyServiceTest {

    @Test
    public void test() {
        List<TyposquattingStrategy> strategies = new ArrayList<>();

        TyposquattingStrategy longHost = mock(TyposquattingStrategy.class);
        when(longHost.applyStrategy("google.com", "google.co")).thenReturn(true);
        when(longHost.applyStrategy("google.com", "google.com")).thenReturn(false);
        strategies.add(longHost);

        TyposquattingStrategy swappedChar = mock(TyposquattingStrategy.class);
        when(swappedChar.applyStrategy("google.com", "google.cmo")).thenReturn(true);
        when(swappedChar.applyStrategy("google.com", "google.com")).thenReturn(false);
        strategies.add(swappedChar);

        List<String> whitelist = new ArrayList<>();
        whitelist.add("google.com");
        ITyposquattingService service = new TyposquattingService(whitelist);
        service.setStrategies(strategies);

        TyposquattingResult actual1 = service.checkCandidateHost("google.com");
        Assert.assertFalse(actual1.getResult());

        TyposquattingResult actual2 = service.checkCandidateHost("google.co");
        Assert.assertTrue(actual2.getResult());

        TyposquattingResult actual3 = service.checkCandidateHost("google.cmo");
        Assert.assertTrue(actual3.getResult());
    }
}
