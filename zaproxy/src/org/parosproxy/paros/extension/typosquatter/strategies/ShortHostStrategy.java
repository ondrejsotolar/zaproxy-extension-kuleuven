package org.parosproxy.paros.extension.typosquatter.strategies;

import org.parosproxy.paros.extension.typosquatter.TyposquattingStrategy;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ShortHostStrategy implements TyposquattingStrategy{

    @Override
    public String getName() {

        return "ShortHostStrategy";
    }

    /**
     * Apply strategy. Returns true if typosqatting is detected.
     * @param host
     * @param candidate
     * @return true if typosquatting is detected
     */
    public boolean applyStrategy(String host, String candidate) {
        if (host.length() == candidate.length())
            return false;

        String regex = getRegex(host);
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(candidate);

        return matcher.matches();
    }

    /**
     * Build the regex from host name
     * Example:
     *  Host: google.com
     *  Desired regex: (?=[google.com]{9})g?o?o?g?l?e?\.?c?o?m?
     * @param host
     * @return regex string
     */
    public String getRegex(String host) {
        String regexBase = "(?=[%s]{%d})\\w?";
        //regexBase += "\\w?";

        for (Character c : host.toCharArray()) {
            regexBase += c + "\\w?";
        }

        String result = String.format(regexBase, host, host.length() + 1);
        return result;
    }

}
