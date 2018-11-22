package org.parosproxy.paros.extension.typosquatter.strategies;

import org.parosproxy.paros.extension.typosquatter.TyposquattingStrategy;


public class SwappedCharacterStrategy implements TyposquattingStrategy {

    @Override
    public String getName() {
        return "SwappedCharacterStrategy";
    }

    /**
     * Apply strategy. Returns true if typosqatting is detected.
     * @param host
     * @param candidate
     * @return true if typosquatting is detected
     */

    public boolean applyStrategy(String host, String candidate){


        int len1 = host.length();
        int len2 = candidate.length();

        if (len1 != len2)
            return false;

        int count = 0, pos = 0;

        for (int i = 0; i < len1; i++)
        {
            if (host.charAt(i)  != candidate.charAt(i))
            {
                count++;
                pos = i;
                if (count > 2)
                    return false;
            }
        }

        return (count == 2
                &&
                host.charAt(pos-1) == candidate.charAt(pos)
                &&
                host.charAt(pos) == candidate.charAt(pos-1));
    }

}
