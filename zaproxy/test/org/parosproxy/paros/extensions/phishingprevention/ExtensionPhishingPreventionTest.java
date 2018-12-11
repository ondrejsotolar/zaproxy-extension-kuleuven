package org.parosproxy.paros.extensions.phishingprevention;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.parosproxy.paros.extension.phishingprevention.*;
import org.parosproxy.paros.extension.phishingprevention.persistence.MemoryPersistenceService;
import org.parosproxy.paros.extension.phishingprevention.persistence.SimpleHashingService;
import org.parosproxy.paros.extension.phishingprevention.requestscan.OverrideListener;
import org.parosproxy.paros.network.HttpMessage;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
public class ExtensionPhishingPreventionTest {


}
