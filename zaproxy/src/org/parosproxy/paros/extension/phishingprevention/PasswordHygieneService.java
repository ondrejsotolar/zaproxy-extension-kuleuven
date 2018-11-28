package org.parosproxy.paros.extension.phishingprevention;

import org.parosproxy.paros.extension.phishingprevention.strategies.OnlyNumbersStrategy;

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
    }

    public void setStrategies(List<PasswordHygieneStrategy> strategies) {
        this.strategies = strategies;
    }

    @Override
    public PasswordHygieneResult checkPasswordHygiene(String password) {
        if (password == null || password.isEmpty()) {
            throw new RuntimeException("PasswordHygieneService.checkPasswordHygiene: empty or null password");
        }

        PasswordHygieneResult result = new PasswordHygieneResult(password);

            for (PasswordHygieneStrategy strategy : strategies) {
                boolean strategyFailed = strategy.applyStrategy(password);
                if (strategyFailed) {
                    result.addFailedTyposquattingStrategy(strategy.getName());
                }
            }
        return result;
    }
}
