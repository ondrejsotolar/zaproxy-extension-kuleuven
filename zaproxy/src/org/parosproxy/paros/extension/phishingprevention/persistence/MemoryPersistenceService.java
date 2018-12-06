package org.parosproxy.paros.extension.phishingprevention.persistence;

import org.parosproxy.paros.extension.phishingprevention.Credentials;
import org.parosproxy.paros.extension.phishingprevention.PersistenceService;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

public class MemoryPersistenceService implements PersistenceService {

    Map<String, StoredCredentials> store = new HashMap<>();

    static BufferedReader bufferedReader = null;
    static String line = "";
    static String cvsSplitBy = ",";

    @Override
    public StoredCredentials get(String host) {
        return store.get(host);
    }

    @Override
    public void saveOrUpdate(Credentials credentials, boolean allow) {
        StoredCredentials stored = store.get(credentials.getHost());
        if (stored == null) {
            store.put(credentials.getHost(), new StoredCredentials(credentials, allow));
        } else {
            stored.setAllow(allow);
        }
    }

    @Override
    public void setAllowed(String host, boolean allow) {
        StoredCredentials stored = store.get(host);
        if (stored == null) {
            throw new RuntimeException("MemoryPersistenceService: not found: " + host);
        } else {
            stored.setAllow(allow);
        }
    }

    public void remove(String host) {
        store.remove(host);
    }

    public static void saveToFile(String newEntry){
        try {
            FileWriter writer = new FileWriter("test.csv");
            writer.append(newEntry);
            writer.append(",");
            writer.append(newEntry);
            writer.append(",");
            writer.append(newEntry);
            writer.append(",");
            writer.append(newEntry);
            writer.append("/n");

            writer.flush();
            writer.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        System.out.println(System.getProperty("user.dir"));
        saveToFile("hej");
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
}
