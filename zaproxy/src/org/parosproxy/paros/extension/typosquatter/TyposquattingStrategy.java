package org.parosproxy.paros.extension.typosquatter;

import java.util.List;

public interface TyposquattingStrategy {
    String getName();

    boolean applyStrategy(String host, String candidate);
}