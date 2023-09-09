package com.example.visual;

import com.example.SkinRequest;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Accounts extends JFrame {
    private JTextField emailField;
    private JLabel usernameLbl;
    private JPasswordField passwordField;
    private JButton loginButton;

    public Accounts() {
        setLayout(null);
        setTitle("Ely.by Login");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setBounds(0, 0, 400, 250);
        panel.setBackground(Color.darkGray);
        panel.setLayout(null);

        JLabel emailLabel = new JLabel("Email or Username:");
        emailLabel.setBounds(10, 40, 120, 25);
        emailLabel.setForeground(Color.WHITE);
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(10, 75, 120, 25);
        passwordLabel.setForeground(Color.WHITE);
        emailField = new JTextField();
        emailField.setBounds(140, 40, 210, 25);
        passwordField = new JPasswordField();
        passwordField.setBounds(140, 75, 210, 25);
        loginButton = new JButton("Login");
        loginButton.setBounds(140, 110, 80, 30);
        loginButton.setForeground(Color.WHITE);
        loginButton.setBackground(Color.GRAY);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String password = String.valueOf(passwordField.getPassword());
                startLoginProcess(emailField.getText(), password);
            }
        });

        usernameLbl = new JLabel("Username: ");
        usernameLbl.setBounds(10, 160, 150, 25);
        usernameLbl.setForeground(Color.WHITE);

        panel.add(emailLabel);
        panel.add(emailField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(loginButton);
        panel.add(usernameLbl);

        add(panel);
        setVisible(true);
    }

    public void startLoginProcess(String UsernameEmail, String password) {
        Thread loginThread = new Thread(() -> {

            int responseCode = 0;
            String inputString = null;

            URL url = null;
            try {
                url = new URL("https://authserver.ely.by/auth/authenticate");
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }

            try {

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);

                // create the query params
                StringBuffer queryParam = new StringBuffer();
                queryParam.append("username=");
                queryParam.append(UsernameEmail);
                queryParam.append("&");
                queryParam.append("password=");
                queryParam.append(password);
                queryParam.append("&");
                queryParam.append("clientToken=");
                queryParam.append("oslauncher");
                queryParam.append("&");
                queryParam.append("requestUser=");
                queryParam.append("true");

                // Output the results
                OutputStream output = urlConnection.getOutputStream();
                output.write(queryParam.toString().getBytes());
                output.flush();

                // get the response-code from the response
                responseCode = urlConnection.getResponseCode();

                // print out URL details
                System.out.format("Connecting to %s\nConnection Method: '%s'\nResponse Code is: %d\n", url, "POST", responseCode);
                System.out.println("----[ URL DETAILS ]-----------------");
                System.out.println("URL Protocol....: " + url.getProtocol());
                System.out.println("URL Host........: " + url.getHost());
                System.out.println("URL Port........: " + url.getPort());
                System.out.println("URL Authority...: " + url.getAuthority());
                System.out.println("URL Path........: " + url.getPath());
                System.out.println("URL User Info...: " + url.getUserInfo());
                System.out.println("URL Query Info..: " + url.getQuery());

                System.out.println("----[ OUTPUT BELOW ]-----------------------------------------------------------------");

                // open the contents of the URL as an inputStream and print to stdout
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        urlConnection.getInputStream()));
                while ((inputString = in.readLine()) != null) {
                    System.out.println(inputString);
                }
                in.close();
                urlConnection.disconnect();
                System.out.println("-------------------------------------------------------------------------------------");

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        loginThread.start();
    }
}
