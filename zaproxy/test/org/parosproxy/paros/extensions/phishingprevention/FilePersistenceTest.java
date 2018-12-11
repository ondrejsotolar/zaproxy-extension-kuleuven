package org.parosproxy.paros.extensions.phishingprevention;

import org.junit.Assert;
import org.parosproxy.paros.extension.phishingprevention.Credentials;
import org.parosproxy.paros.extension.phishingprevention.persistence.*;

import org.junit.Test;

import java.io.File;

public class FilePersistenceTest {

    @Test
    public void createFile (){
        FilePersistenceService memTest = new FilePersistenceService();
        File file = new File("src\\org\\parosproxy\\paros\\extension\\phishingprevention\\persistence\\storedCredentials.csv");
        Assert.assertTrue(file.exists());
    }

    @Test
    public void saveTest(){
        FilePersistenceService memTest = new FilePersistenceService();
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
        FilePersistenceService memTest = new FilePersistenceService();
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
        FilePersistenceService memTest = new FilePersistenceService();
        Credentials cred1 = new Credentials("host1", "usr1", "pass1");
        memTest.saveOrUpdate( cred1, true, false);

        Assert.assertTrue((memTest.get(cred1.getHost(), cred1.getUsername())!=null));

        memTest.remove(cred1.getHost(), cred1.getUsername());

        Assert.assertTrue((memTest.get(cred1.getHost(), cred1.getUsername())==null));

        memTest.getFPS().deleteFile(); //just added to last test to delete csv file from folder
    }

    @Test
    public void persistanceOfPassword(){
        FilePersistenceService memTest = new FilePersistenceService();
        Credentials cred1 = new Credentials("host1", "usr1", "pass1");
        memTest.saveOrUpdate( cred1, true, false);
        System.out.println(memTest.get(cred1.getHost(), cred1.getUsername()).getPassword());
        Assert.assertTrue((memTest.get(cred1.getHost(), cred1.getUsername())!=null));



    }
}
