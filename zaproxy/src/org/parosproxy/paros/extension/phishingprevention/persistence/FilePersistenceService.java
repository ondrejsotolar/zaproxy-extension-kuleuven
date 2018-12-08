package org.parosproxy.paros.extension.phishingprevention.persistence;

import org.hsqldb.util.CSVWriter;
import org.parosproxy.paros.extension.phishingprevention.Credentials;

import java.io.*;

public class FilePersistenceService extends MemoryPersistenceService {


    public void saveFile() {

        try {
            FileWriter writer = new FileWriter("output.csv", true);

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

  /*  public void readFIle() {
        try {

            bufferedReader = new BufferedReader(new FileReader("test.csv"));
            while ((line = bufferedReader.readLine()) != null) {
                String[] gr = line.split(cvsSplitBy);
                System.out.println("host: " + gr[0] + " user: " + gr[1] + " pass: " + gr[2] + " isAllowed: " + gr[3]);
            }
            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveToFile(Credentials credentials, Boolean allowed) {

        // char[] pass1 ;
        // pass1 = new char[]{'P', 'a', 's', 's', 'w', 'o', 'r','d'};

        String password = credentials.getPassword();
        char p = password.charAt(0);
        char[] pass = password.toCharArray();


        byte[] newpass =  hashedPasswords.hash(pass,hashedPasswords.getNextSalt());
        // System.out.println(newpass);

        try {
            FileWriter writer = new FileWriter("test.csv", true);

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
*/


}
