package com.example.visual;

import com.example.SkinRequest;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Accounts extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;

    public Accounts() {
        setLayout(null);
        setTitle("Account Login");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 250);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setBounds(0, 0, 400, 250);
        panel.setLayout(null);

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setBounds(20, 20, 50, 25);
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(20, 50, 50, 25);
        emailField = new JTextField();
        emailField.setBounds(70, 20, 100, 25);
        passwordField = new JPasswordField();
        passwordField.setBounds(70, 50, 100, 25);
        loginButton = new JButton("Login");
        loginButton.setBounds(20, 80, 80, 25);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        panel.add(emailLabel);
        panel.add(emailField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(loginButton);

        add(panel);
        setVisible(true);
    }
}
