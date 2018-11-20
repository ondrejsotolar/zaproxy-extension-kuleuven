package org.parosproxy.paros.extension.typosquatter;

import org.parosproxy.paros.extension.ViewDelegate;
import org.parosproxy.paros.model.Model;
import org.parosproxy.paros.network.HttpMessage;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

public interface PersistanceService {

    List<String> getWhiteList(File file);

    File getWhitelistFile(Model model, ViewDelegate view);

    List<String> parseWhitelistFile(File file);

    void addToWhitelistFile(String host, File whitelistFile);

    void persistToWhitelist(String hostName, Path pathToWhitelist);

    boolean addToWhiteListAndPassThrough(HttpMessage msg, String candidate);
}
