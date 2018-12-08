package org.parosproxy.paros.extension.phishingprevention.optionsdialog;

import org.parosproxy.paros.extension.phishingprevention.ExtensionPhishingPrevention;
import org.parosproxy.paros.model.OptionsParam;
import org.parosproxy.paros.view.AbstractParamPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class OptionsDialog extends AbstractParamPanel {

    private static final long serialVersionUID = 1L;
    private JPanel panel = null;

    private ExtensionPhishingPrevention ext;
    private JCheckBox secure;
    private JCheckBox hygiene;

    public OptionsDialog(ExtensionPhishingPrevention ext) {
        super();
        this.ext = ext;
        this.setLayout(new CardLayout());
        this.setName("Phishing prevention");
        getOptionsPanel();
    }

    private JPanel getOptionsPanel() {
        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);

        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        JLabel lbSecure = new JLabel("Phishing prevention");
        JCheckBox chSecure = getSecure();
        lbSecure.setLabelFor(chSecure);

        JLabel lbHygiene = new JLabel("Hygiene check");
        JCheckBox chHygiene = getHygiene();
        lbHygiene.setLabelFor(chHygiene);

        layout.setHorizontalGroup(
                layout.createSequentialGroup()
                    .addComponent(lbSecure)
                    .addComponent(chSecure)
                    .addComponent(lbHygiene)
                    .addComponent(chHygiene));

        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(lbSecure)
                        .addComponent(chSecure)
                        .addComponent(lbHygiene)
                        .addComponent(chHygiene));
        return panel;
    }
    private JCheckBox getSecure() {
        if (secure == null) {
            secure = new JCheckBox();
            secure.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    if (!secure.isSelected()) {
                        getHygiene().setSelected(false);
                    }
                    getHygiene().setEnabled(secure.isSelected());

                }
            });
        }
        return secure;
    }
    private JCheckBox getHygiene() {
        if (hygiene == null) {
            hygiene = new JCheckBox();
            hygiene.setEnabled(false);
        }
        return hygiene;
    }

    @Override
    public void initParam(Object obj) {
    }

    @Override
    public void validateParam(Object obj) throws Exception {
    }

    @Override
    public void saveParam(Object obj) throws Exception {
        OptionsParam optionsParam = (OptionsParam) obj;
        PhishingPreventionParam proxyParam = optionsParam
                .getParamSet(PhishingPreventionParam.class);

        proxyParam.setSecure(secure.isSelected());
        proxyParam.setHygiene(secure.isSelected() && hygiene.isSelected());
    }

    @Override // TODO: implement
    public String getHelpIndex() {
        return "ui.dialogs.options.callback";
    }

}
