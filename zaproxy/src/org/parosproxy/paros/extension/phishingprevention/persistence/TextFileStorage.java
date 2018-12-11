package org.parosproxy.paros.extension.phishingprevention.persistence;

import org.apache.log4j.Logger;
import org.parosproxy.paros.extension.phishingprevention.Credentials;
import org.parosproxy.paros.extension.phishingprevention.requestscan.QueryStringParamScanner;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FilePersistenceService {

    private final String cvsSplitBy = ",";

    private File file;

    private static Logger log = Logger.getLogger(QueryStringParamScanner.class);

    public FilePersistenceService(String name){
        this.file = new File (
                "src"+File.separator+
                        "org"+File.separator+
                        "parosproxy"+File.separator+
                        "paros"+File.separator+
                        "extension"+File.separator+
                        "phishingprevention"+File.separator+
                        "persistence"+File.separator+
                        name
        );
        initStorageFile();
    }

    public void initStorageFile() {
        try {
            FileWriter writer = new FileWriter(file, true);
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<StoredCredentials> loadStoredCredentials() {
        List<StoredCredentials> storedList = new ArrayList<>();

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                String[] gr = line.split(cvsSplitBy);

                StoredCredentials storeCred = new StoredCredentials(
                        new Credentials(gr[0],gr[1],gr[2]),
                        Boolean.valueOf(gr[3]),
                        Boolean.valueOf(gr[4])
                );
                storedList.add(storeCred);
            }
            bufferedReader.close();
        } catch (Exception e) {
            log.info(e.toString());
        }
        return storedList;
    }

    public void saveToFile(List<StoredCredentials> list) {
        deleteFile();
        try {
            CreateFile();
            FileWriter writer = new FileWriter(file, true);

        for (StoredCredentials storedCreds : list) {

            Boolean hostWhitelisted = storedCreds.isHostWhitelisted();
            Boolean hygieneWhitelisted = storedCreds.isHygieneWhitelisted();
            if (hostWhitelisted) {
                    writer.append(storedCreds.getHost());
                    writer.append(",");
                    writer.append(storedCreds.getUsername());
                    writer.append(",");
                    writer.append(storedCreds.getPassword());
                    writer.append(",");
                    writer.append(hostWhitelisted.toString());
                    writer.append(",");
                    writer.append(hygieneWhitelisted.toString());
                    writer.append("\n");


                }
            }
            writer.flush();
            writer.close();

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteFile(){
        try {
            if(file.exists())
                file.delete();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }



}
