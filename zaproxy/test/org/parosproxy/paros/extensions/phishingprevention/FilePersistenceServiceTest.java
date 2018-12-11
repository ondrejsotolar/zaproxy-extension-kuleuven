package org.parosproxy.paros.extensions.phishingprevention;

import org.junit.Assert;
import org.parosproxy.paros.extension.phishingprevention.Credentials;
import org.parosproxy.paros.extension.phishingprevention.persistence.FilePersistenceService;

import org.junit.Test;
import org.parosproxy.paros.extension.phishingprevention.persistence.StoredCredentials;

import java.io.File;

import java.util.ArrayList;
import java.util.List;


public class FilePersistenceServiceTest {

    @Test
    public void fileCreateTest(){

        FilePersistenceService fileTest = new FilePersistenceService("dennis.csv");
        File file = new File("src\\org\\parosproxy\\paros\\extension\\phishingprevention\\persistence\\dennis.csv");
        Assert.assertTrue(file.exists());

    }

    @Test
    public void deleteFileTest(){
        FilePersistenceService fileTest = new FilePersistenceService("alex.csv");
        fileTest.deleteFile();
        File file = new File("src\\org\\parosproxy\\paros\\extension\\phishingprevention\\persistence\\alex.csv");
        Assert.assertFalse(file.exists());

    }

    @Test
    public void addAndReadFile(){
        FilePersistenceService fileTest = new FilePersistenceService("test.csv");
        List<StoredCredentials> store = new ArrayList<>();

        StoredCredentials stc1 = new StoredCredentials(new Credentials("host1", "usr1", "pass1"),true,true);
        store.add(stc1);

        StoredCredentials stc2 = new StoredCredentials(new Credentials("host2", "usr2", "pass2"),true,false);
        store.add(stc2);

        StoredCredentials stc3 = new StoredCredentials(new Credentials("host3", "usr3", "pass3"),true,true);
        store.add(stc3);
        fileTest.saveToFile(store);
        List<StoredCredentials> list =  fileTest.readFIle();
        System.out.println(list);
        System.out.println(store);

        for(int i = 0; i<store.size(); i++)
        {
            StoredCredentials stca = store.get(i);
            StoredCredentials stcb = list.get(i);

            Assert.assertTrue(stca.getHost().equals(stcb.getHost()));
            Assert.assertTrue(stca.getUsername().equals(stcb.getUsername()));
            Assert.assertTrue(stca.getPassword().equals(stcb.getPassword()));
            Assert.assertTrue(stca.isHostWhitelisted() == stcb.isHostWhitelisted());
            Assert.assertTrue(stca.isHygieneWhitelisted() == stcb.isHygieneWhitelisted());
        }
    }


}