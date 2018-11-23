package org.parosproxy.paros.extension.typosquatter.strategies;

import org.apache.commons.codec.binary.StringUtils;
import org.parosproxy.paros.extension.typosquatter.TyposquattingStrategy;
import org.zaproxy.zap.utils.StringUIUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by PC10 on 20-11-2018.
 */
public class ReplacedCharStrategy  implements TyposquattingStrategy{

    @Override
    public String getName() {
        return "ReplacedCharStrategy";
    }

    /**
     * Apply strategy. Returns true if typosqatting is detected.
     * @param host
     * @param candidate
     * @return true if typosquatting is detected
     */
    public boolean applyStrategy(String host, String candidate) {
        if (host.length() != candidate.length())
            return false;

        int res = checkString(host, candidate);

        if(res == 1)
            return true;
        else
            return false;

    }

    public int checkString(String host, String candidate){

        char[] hostChars = host.toCharArray();
        char[] candidateChar = candidate.toCharArray();
        int length = hostChars.length;
        int res = 0;

        for (int i = 0; i<length; i++){
            if(hostChars[i] != candidateChar[i])
                res++;
        }

        return res;
    }
    /*
        String regex = getRegex(host);
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(candidate);

        return matcher.matches();
    }*/

    /**
     * Build the regex from host name
     * Example:
     *  Host: google.com
     *  Desired regex: (?=[google.com]{9})g?o?o?g?l?e?\.?c?o?m?
     * @param host
     * @return regex string
     */
   /* public String getRegex(String host) {
        String regexBase = "(?=[%s]{%d})";

        for (Character c : host.toCharArray()) {
            regexBase += c + "?";
        }

        String result = String.format(regexBase, host, host.length() - 1);
        return result;
    }*/
}
