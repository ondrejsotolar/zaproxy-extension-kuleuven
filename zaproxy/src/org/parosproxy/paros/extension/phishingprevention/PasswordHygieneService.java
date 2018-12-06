package org.parosproxy.paros.extension.phishingprevention;

import org.parosproxy.paros.extension.phishingprevention.strategies.CrackLibTestStrategy;
import org.parosproxy.paros.extension.phishingprevention.strategies.OnlyNumbersStrategy;
import org.parosproxy.paros.extension.phishingprevention.strategies.CommonPasswordsStrategy;

import java.util.ArrayList;
import java.util.List;

public class PasswordHygieneService implements IPasswordHygieneService {

    private List<PasswordHygieneStrategy> strategies;

    public PasswordHygieneService() {
        initStrategies();
    }

    private void initStrategies() {
        strategies = new ArrayList<>();

        strategies.add(new OnlyNumbersStrategy());
        strategies.add(new CommonPasswordsStrategy());

        // TODO: fix these
        //strategies.add(new CrackLibTestStrategy());
    }

    public void setStrategies(List<PasswordHygieneStrategy> strategies) {
        this.strategies = strategies;
    }

    @Override
    public PasswordHygieneResult checkPasswordHygiene(Credentials credentials) {
        if (credentials == null || credentials.getPassword() == null || credentials.getPassword().isEmpty()) {
            throw new RuntimeException("PasswordHygieneService.checkPasswordHygiene: empty or null password");
        }
        PasswordHygieneResult result = new PasswordHygieneResult(credentials);

        for (PasswordHygieneStrategy strategy : strategies) {
            if (strategy.applyStrategy(credentials.getPassword()) != null) {
                String reason = strategy.applyStrategy(credentials.getPassword());
                result.addMsgFailedStrategy(strategy.getName(), reason);
            }
        }
        return result;
    }
}
