package org.parosproxy.paros.extensions.phishingprevention;

import org.junit.Assert;
import org.parosproxy.paros.extension.phishingprevention.Credentials;
import org.parosproxy.paros.extension.phishingprevention.persistence.*;

import org.junit.Test;

import java.io.File;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class MemoryPersistenceTest {

    @Test
    public void createFile (){
        MemoryPersistenceService memTest = new MemoryPersistenceService();
        File file = new File("src\\org\\parosproxy\\paros\\extension\\phishingprevention\\persistence\\storedCredentials.csv");
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

    @Test
    public void updateTest() {
        MemoryPersistenceService memTest = new MemoryPersistenceService();
        PasswordHashingService hashingService = new SimpleHashingService();
        Credentials cred1 = new Credentials("host1", "usr1", "pass1");
        memTest.saveOrUpdate( cred1, true, false);
        StoredCredentials memStored = memTest.get(cred1.getHost(), cred1.getUsername());
        StoredCredentials storedCred = new StoredCredentials(new Credentials("host1", "usr1", "pass2"),true, true);
        storedCred.hashPassword(hashingService);

        Assert.assertFalse(memStored.getPassword().equals(storedCred.getPassword()));

        memTest.updatePassword(cred1,storedCred);

        Assert.assertTrue(memTest.get(cred1.getHost(), cred1.getUsername()).isHygieneWhitelisted());
    }

    @Test
    public void removeTest() {
        MemoryPersistenceService memTest = new MemoryPersistenceService();
        Credentials cred1 = new Credentials("host1", "usr1", "pass1");
        memTest.saveOrUpdate( cred1, true, false);

        Assert.assertTrue((memTest.get(cred1.getHost(), cred1.getUsername())!=null));

        memTest.remove(cred1.getHost(), cred1.getUsername());

        Assert.assertTrue((memTest.get(cred1.getHost(), cred1.getUsername())==null));

        memTest.getFPS().deleteFile(); //just added to last test to delete csv file from folder
    }

}
