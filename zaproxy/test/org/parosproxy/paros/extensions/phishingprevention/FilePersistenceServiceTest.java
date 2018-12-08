package org.parosproxy.paros.extensions.phishingprevention;

import org.junit.Assert;
import org.parosproxy.paros.extension.phishingprevention.persistence.FilePersistenceService;

import org.junit.Test;
import java.io.File;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;


public class FilePersistenceServiceTest {

    @Test
    public  void fileCreateTest(){

        FilePersistenceService fileTest = new FilePersistenceService();
        Path path = Paths.get("output.csv");
        Assert.assertTrue(file.exists());

    }

}
