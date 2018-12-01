package org.parosproxy.paros.extension.phishingprevention.strategies;

import org.parosproxy.paros.extension.phishingprevention.PasswordHygieneStrategy;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.ArrayList;

public class CommonPasswordsStrategy implements PasswordHygieneStrategy {

    @Override
    public String getName() {
        return "CommonPasswordsStrategy";
    }

    @Override
    public boolean applyStrategy(String password) {

        Scanner s = null;
        ArrayList<String> list = null;
        try {
            s = new Scanner(new File("C:\\Users\\alexa\\Documents\\University\\KUL\\M1\\Software\\Group11\\zaproxy\\src\\org\\parosproxy\\paros\\extension\\phishingprevention\\strategies\\CommonPasswords.txt"));
            list = new ArrayList<>();
            while (s.hasNext()){
                list.add(s.next());
            }
        } catch(IOException e) {
            System.err.println(e.getMessage());
        } finally {
            if (s!=null) {
                s.close();
            }
        }
        if (list!=null) {
            return list.contains(password);
        }
        return false;
    }
}
