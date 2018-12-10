package org.parosproxy.paros.extension.phishingprevention.persistence;

import org.hsqldb.util.CSVWriter;
import org.parosproxy.paros.extension.phishingprevention.Credentials;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FilePersistenceService extends MemoryPersistenceService {

    private String name;
    private BufferedReader bufferedReader = null;
    private String line = "";
    private String cvsSplitBy = ",";

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    /*public FilePersistenceService(){

        CreateFile();
    }


    public void CreateFile(String name) {

        try {
            File file = new File ("src"+File.separator+"org"+File.separator+"parosproxy"+File.separator+"paros"+File.separator+"extension"+File.separator+"phishingprevention"+File.separator+"persistence"+File.separator+name);
            FileWriter writer = new FileWriter(file, true);

            writer.append("aditya");
            writer.append(",");
            writer.append("aditya");
            writer.append(",");
            writer.append("aditya");
            writer.append(",");
            writer.append("aditya");
            writer.append("\n");

            writer.flush();

            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    */

    public List<StoredCredentials> readFIle() {
        List<StoredCredentials> storedList = new ArrayList<>();

        try {
            bufferedReader = new BufferedReader(new FileReader(name));
            while ((line = bufferedReader.readLine()) != null) {
                String[] gr = line.split(cvsSplitBy);
                System.out.println("host: " + gr[0] + " user: " + gr[1] + " pass: " + gr[2] + " isAllowed: " + gr[3]);
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

    public void saveToFile(Credentials credentials, Boolean allowed) {

        // char[] pass1 ;
        // pass1 = new char[]{'P', 'a', 's', 's', 'w', 'o', 'r','d'};
        if(allowed) {
            String password = credentials.getPassword();
            char p = password.charAt(0);
            char[] pass = password.toCharArray();


            byte[] newpass =  hashedPasswords.hash(pass,hashedPasswords.getNextSalt());
            // System.out.println(newpass);

            try {
                File file = new File ("src"+File.separator+"org"+File.separator+"parosproxy"+File.separator+"paros"+File.separator+"extension"+File.separator+"phishingprevention"+File.separator+"persistence"+File.separator+name);
                FileWriter writer = new FileWriter(file, true);

                writer.append(credentials.getHost());
                writer.append(",");
                writer.append(credentials.getUsername());
                writer.append(",");
                writer.append(new String(newpass));
                writer.append(",");
                writer.append(allowed.toString());
                writer.append("\n");

                writer.flush();
                writer.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



}
