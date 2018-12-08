package org.parosproxy.paros.extension.phishingprevention.strategies;

import org.parosproxy.paros.extension.phishingprevention.PasswordHygieneStrategy;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OnlyNumbersStrategy implements PasswordHygieneStrategy {

    private Pattern pattern = Pattern.compile("[0-9]+", Pattern.CASE_INSENSITIVE);

    @Override
    public String getName() {
        return "OnlyNumbersStrategy";
    }

    @Override
    public String applyStrategy(String password) {
        Matcher matcher = this.pattern.matcher(password);

        if(matcher.matches()){
            return "Password only uses numbers";
        }
        else
        {
            return new String();
        }
    }
}
