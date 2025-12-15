package Client.utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public final class UIBehavior {

    private UIBehavior() {}

    public static void setTextFieldPlaceholder(JTextField textField, String hintText) {
        textField.addFocusListener(new FocusAdapter() {

            @Override
            public void focusGained(FocusEvent e) {
                String current = String.valueOf(textField.getText());

                if (current.equals(hintText)) {
                    textField.setText("");
                    textField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (textField.getText().isEmpty()) {
                    textField.setText(hintText);
                    textField.setForeground(UIConstants.HINT_GRAY);
                }
            }
        });
    }
    public static void setPasswordPlaceholder(JPasswordField passwordField, String hintText) {
        passwordField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                String current = String.valueOf(passwordField.getPassword());

                //if still shows the hint text, make it empty
                if (current.equals(hintText)) {
                    passwordField.setText("");
                    passwordField.setEchoChar('•');
                    passwordField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                //if user didn't enter anything, back to hint text
                if (passwordField.getPassword().length == 0) {
                    passwordField.setText(hintText);
                    passwordField.setEchoChar((char) 0);
                    passwordField.setForeground(UIConstants.HINT_GRAY);
                }
            }
        });
    }
}
