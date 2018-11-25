package org.parosproxy.paros.extensions.typosquatter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.internal.matchers.Any;
import org.parosproxy.paros.extension.typosquatter.ExtensionTyposquatter;
import org.parosproxy.paros.extension.typosquatter.ITyposquattingService;
import org.parosproxy.paros.extension.typosquatter.PersistanceService;
import org.parosproxy.paros.extension.typosquatter.TyposquattingResult;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
public class ExtensionTyposquatterTest {

    @Test
    public void OkRequest() {
        ITyposquattingService mockTSservice = mock(ITyposquattingService.class);

        when(mockTSservice.checkCandidateHost("google.com"))
                .thenReturn(new TyposquattingResult("google.com"));

        TyposquattingResult catchResult = new TyposquattingResult("google.co");
        catchResult.addFailedTyposquattingStrategy("google.com", "LongHostStrategy");
        when(mockTSservice.checkCandidateHost("google.co"))
                .thenReturn(catchResult);

        ExtensionTyposquatter t = new ExtensionTyposquatter();
        t.setON(true);

        PersistanceService persistanceService = mock(PersistanceService.class);
        when(persistanceService.getWhitelistFile(any(), any()))
                .thenReturn(new File(""));

        List<String> whitelist = new ArrayList<>();
        whitelist.add("google.com");
        when(persistanceService.parseWhitelistFile(any()))
                .thenReturn(whitelist);



    }

    @Test
    public void typoRequest() {

    }

    @Test
    public void previouslyTypoRequest() {

    }
}
