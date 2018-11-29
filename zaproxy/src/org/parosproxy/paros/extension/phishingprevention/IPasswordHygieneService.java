package org.parosproxy.paros.extension.phishingprevention;

public interface IPasswordHygieneService {

    PasswordHygieneResult checkPasswordHygiene(Credentials credentials);
}
