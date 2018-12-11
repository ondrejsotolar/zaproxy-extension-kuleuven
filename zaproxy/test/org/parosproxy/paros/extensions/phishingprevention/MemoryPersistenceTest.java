package org.parosproxy.paros.extensions.phishingprevention;

import org.junit.Assert;
import org.parosproxy.paros.extension.phishingprevention.Credentials;
import org.parosproxy.paros.extension.phishingprevention.persistence.FilePersistenceService;
import org.parosproxy.paros.extension.phishingprevention.persistence.MemoryPersistenceService;

import org.junit.Test;
import org.parosproxy.paros.extension.phishingprevention.persistence.StoredCredentials;

import java.io.File;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class MemoryPersistenceTest {

    @Test
    public void createFile (){
        MemoryPersistenceService memTest = new MemoryPersistenceService();
        File file = new File("src\\org\\parosproxy\\paros\\extension\\phishingprevention\\persistence\\filetest.csv");
        Assert.assertTrue(file.exists());
    }

    @Test
    public void saveTest(){
        MemoryPersistenceService memTest = new MemoryPersistenceService();
        Credentials cred = new Credentials("host1", "usr1", "pass1");
        memTest.saveOrUpdate( cred, true, false);
        StoredCredentials storedCred = memTest.get("host1","usr1");

        Assert.assertTrue(storedCred.getHost().equals(cred.getHost()));
        Assert.assertTrue(storedCred.getUsername().equals(cred.getUsername()));
        Assert.assertFalse(storedCred.getPassword().equals(cred.getPassword())); // we do not want the pwd to be similar as we hash it
        Assert.assertTrue(storedCred.isHostWhitelisted() == true);
        Assert.assertTrue(storedCred.isHygieneWhitelisted() == false);
    }

    public void updateTest() {
        MemoryPersistenceService memTest = new MemoryPersistenceService();
        Credentials cred1 = new Credentials("host1", "usr1", "pass1");
        Credentials cred2 = new Credentials("host1", "usr2", "pass2");
        memTest.saveOrUpdate( cred1, true, false);
        memTest.saveOrUpdate(cred2, true, false);
        StoredCredentials storedCred = memTest.get("host1","usr1");

    }

}
