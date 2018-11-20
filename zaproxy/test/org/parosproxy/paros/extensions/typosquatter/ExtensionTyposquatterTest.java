package org.parosproxy.paros.extensions.typosquatter;

import org.junit.Test;
import org.parosproxy.paros.extension.typosquatter.ExtensionTyposquatter;

public class ExtensionTyposquatterTest {

    @Test
    public void OkRequest() {
        ExtensionTyposquatter t = new ExtensionTyposquatter();
        t.setON(true);


    }

    @Test
    public void typoRequest() {

    }

    @Test
    public void previouslyTypoRequest() {

    }
}
