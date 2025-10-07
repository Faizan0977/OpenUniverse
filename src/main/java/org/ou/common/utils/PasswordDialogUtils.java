package org.ou.common.utils;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

public class PasswordDialogUtils {

    public static char[] askPassword(String title, String message) {
        AtomicReference<char[]> result = new AtomicReference<>(null);

        // Create components
        JLabel messageLabel = new JLabel(message);
        messageLabel.setFont(messageLabel.getFont().deriveFont(Font.PLAIN));

        JPasswordField passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(0, 24)); // <- fixed height
        passwordField.setMinimumSize(new Dimension(Integer.MAX_VALUE, 24)); // <- grow horizontally only
        JCheckBox showPassword = new JCheckBox("Show password");
        showPassword.setFont(showPassword.getFont().deriveFont(Font.PLAIN));

        showPassword.addActionListener(e -> {
            passwordField.setEchoChar(showPassword.isSelected() ? (char) 0 : '\u2022');
        });

        // Panel layout
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        messageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
        showPassword.setAlignmentX(Component.LEFT_ALIGNMENT);

        inputPanel.add(messageLabel);
        inputPanel.add(Box.createVerticalStrut(4));
        inputPanel.add(passwordField);
        inputPanel.add(Box.createVerticalStrut(8));
        inputPanel.add(showPassword);

        // Buttons
        JButton okButton = new JButton("OK");
        Dimension btnSize = new Dimension(90, okButton.getPreferredSize().height);
        okButton.setPreferredSize(btnSize);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setPreferredSize(btnSize);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 5));
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        // Dialog
        JDialog dialog = new JDialog((Frame) null, title, true); // modal
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setLayout(new BorderLayout());
        dialog.add(inputPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setMinimumSize(new Dimension(400, 150));
        dialog.setPreferredSize(new Dimension(450, 150));
        dialog.setResizable(true);
        dialog.pack();
        dialog.setLocationRelativeTo(null);

        // Actions
        okButton.addActionListener(e -> {
            result.set(passwordField.getPassword());
            dialog.dispose();
        });

        cancelButton.addActionListener(e -> {
            dialog.dispose();
        });

        dialog.setVisible(true);
        return result.get();
    }
}
