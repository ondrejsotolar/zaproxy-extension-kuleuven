package org.parosproxy.paros.extensions.phishingprevention;

import org.junit.Assert;
import org.parosproxy.paros.extension.phishingprevention.Credentials;
import org.parosproxy.paros.extension.phishingprevention.persistence.FilePersistenceService;

import org.junit.Test;
import org.parosproxy.paros.extension.phishingprevention.persistence.StoredCredentials;

import java.io.File;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;


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

        StoredCredentials stc2 = new StoredCredentials(new Credentials("host2", "usr2", "pass2"),true,true);
        store.add(stc2);

        StoredCredentials stc3 = new StoredCredentials(new Credentials("host3", "usr3", "pass3"),true,true);
        store.add(stc3);
        fileTest.saveToFile(store);
        List<StoredCredentials> list =  fileTest.readFIle();
        File file = new File("src\\org\\parosproxy\\paros\\extension\\phishingprevention\\persistence\\test.csv");

        Assert.assertTrue(store.equals(list));
    }

    /*public  void readFile(){

        FilePersistenceService fileTest = new FilePersistenceService();
        Credentials Creds1 = new Credentials("host", "username", "123456789");
        Credentials Creds2 = new Credentials("alex", "azalex", "123456789");
        Credentials Creds3 = new Credentials("dennis", "den", "123456789");
        List<Credentials> testList = new ArrayList<>();

        fileTest.setName("output.csv");
        fileTest.saveToFile(foundCredentials,true);
        File file = new File("src\\org\\parosproxy\\paros\\extension\\phishingprevention\\persistence\\output.csv");
        Assert.assertTrue(file.exists());

    }
    */

}
