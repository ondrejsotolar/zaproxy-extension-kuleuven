package org.parosproxy.paros.extension.typosquatter;

import org.parosproxy.paros.extension.ViewDelegate;
import org.parosproxy.paros.model.Model;
import org.parosproxy.paros.network.HttpMessage;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class TextFilePersistence implements PersistanceService {

    @Override
    public List<String> getWhiteList(File whitelistFile) {
        if (whitelistFile == null) {
            return null; // Dialog closed
        }

        return parseWhitelistFile(whitelistFile);
    }

    @Override
    public File getWhitelistFile(Model model, ViewDelegate view) {
        JFileChooser chooser = new JFileChooser(model.getOptionsParam().getUserDirectory());
        File file = null;

        int rc = chooser.showSaveDialog(view.getMainFrame());
        if(rc == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile();
        }
        return file;
    }

    @Override
    public List<String> parseWhitelistFile(File file) {
        List<String> whitelist = new ArrayList<>();
        if (file == null) {
            return whitelist;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                whitelist.add(line);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return whitelist;
    }

//    @Override
//    public void addToWhitelistFile(String host, File whitelistFile) {
//        if (whitelistFile == null) {
//            return;
//        }
//        try {
//            Files.write(whitelistFile.toPath(), (System.lineSeparator() + host).getBytes(),
//                    StandardOpenOption.APPEND);
//        }catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    @Override
    public void persistToWhitelist(String hostName, Path pathToWhitelist) {
        try {
            Files.write(pathToWhitelist, (System.lineSeparator() + hostName).getBytes(),
                    StandardOpenOption.APPEND);
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

//    @Override
//    public boolean addToWhiteListAndPassThrough(HttpMessage msg, String candidate) {
//        return false;
//    }
}
