package Client.panels;


import Client.utils.LogoMaker;
import Client.utils.UIMaker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public abstract class BaseAuthPanel extends JPanel {
    private JButton backButton = new JButton("←");
    private JLabel errorLabel;

    protected BaseAuthPanel() {
        //unchanged parts in panels
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        setOpaque(false);

        UIMaker.styleBackButton(backButton);
        backButton.setAlignmentX(LEFT_ALIGNMENT);

        JPanel top = new JPanel();
        top.setLayout(new BoxLayout(top, BoxLayout.X_AXIS));
        top.setOpaque(false);

        Dimension topBarSize = new Dimension(Integer.MAX_VALUE, 40);
        top.setPreferredSize(topBarSize);
        top.setMaximumSize(topBarSize);
        top.setMinimumSize(topBarSize);

        top.add(backButton);
        top.add(Box.createHorizontalGlue());

        add(top);

        LogoMaker.addLogoTo( this,"/Client/assets/logo_placeholder.png",220, 220, CENTER_ALIGNMENT);

        this.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent e) {
                // when panel is visible again (with back button),
                // this will hide the previous error
                if (errorLabel != null) {
                    hideError();
                }
                resetFields();
            }
        });
    }

    protected abstract void build(); //each panel going to add their own components
    protected abstract void resetFields(); //it will reset the texts user entered

    //only some panels will use back button
    protected void enableBackButton(Runnable action) {
        backButton.setVisible(true);
        for (java.awt.event.ActionListener al : backButton.getActionListeners()) {
            backButton.removeActionListener(al);
        }
        backButton.addActionListener(e -> action.run());
    }
    protected void hideBackButton() {
        backButton.setVisible(false);
    }

    protected void setErrorLabel() {
        errorLabel = new JLabel("");
        UIMaker.styleErrorLabel(errorLabel);
        errorLabel.setVisible(false);
        add(errorLabel);
    }
    //auth errors to show in panels
    protected void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    protected void hideError() {
        errorLabel.setVisible(false);
    }
}