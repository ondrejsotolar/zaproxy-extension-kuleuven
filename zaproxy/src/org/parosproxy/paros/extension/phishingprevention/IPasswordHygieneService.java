package org.parosproxy.paros.extension.phishingprevention;

import org.parosproxy.paros.extension.phishingprevention.hygiene.PasswordHygieneResult;

public interface IPasswordHygieneService {

    PasswordHygieneResult checkPasswordHygiene(Credentials credentials);
}
