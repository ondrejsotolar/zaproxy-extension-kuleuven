package org.parosproxy.paros.extension.phishingprevention.persistence;

import org.hsqldb.util.CSVWriter;
import org.parosproxy.paros.extension.phishingprevention.Credentials;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FilePersistenceService {

    private String name;
    private BufferedReader bufferedReader = null;
    private String line = "";
    private String cvsSplitBy = ",";
    private File file;

    public FilePersistenceService(String name){
        this.name = name;
        CreateFile();
        file = new File ("src"+File.separator+"org"+File.separator+"parosproxy"+File.separator+"paros"+File.separator+"extension"+File.separator+"phishingprevention"+File.separator+"persistence"+File.separator+name);
    }

    public void CreateFile() {

        try {
            FileWriter writer = new FileWriter(file, true);

            writer.flush();
            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<StoredCredentials> readFIle() {
        List<StoredCredentials> storedList = new ArrayList<>();

        try {
            bufferedReader = new BufferedReader(new FileReader(name));
            while ((line = bufferedReader.readLine()) != null) {
                String[] gr = line.split(cvsSplitBy);
                //System.out.println("host: " + gr[0] + " user: " + gr[1] + " pass: " + gr[2] + " isAllowed: " + gr[3]);
                Credentials creds = new Credentials(gr[0],gr[1],gr[2]);
                Boolean whitelisthost = Boolean.valueOf(gr[3]);
                Boolean whitelisthygiene = Boolean.valueOf(gr[4]);
                StoredCredentials storeCred = new StoredCredentials(creds, whitelisthost, whitelisthygiene);
                storedList.add(storeCred);
            }
            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
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

                    writer.flush();
                    writer.close();
                }
            }

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
