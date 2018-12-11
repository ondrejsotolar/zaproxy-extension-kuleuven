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
    public  void fileCreateTest(){

        FilePersistenceService fileTest = new FilePersistenceService();
        Credentials foundCredentials = new Credentials("host", "username", "123456789");
        fileTest.setName("output.csv");
        fileTest.saveToFile(foundCredentials,true);
        File file = new File("src\\org\\parosproxy\\paros\\extension\\phishingprevention\\persistence\\output.csv");
        Assert.assertTrue(file.exists());

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
