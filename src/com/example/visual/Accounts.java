package com.example.visual;

import com.example.Utils;
import com.example.WindowManager;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class Accounts extends JFrame {
    private JTextField emailField;
    private JLabel createAccountLbl, accountTypeLbl, emailLabel, passwordLabel;
    private JPasswordField passwordField;
    private JButton loginButton, selectButton, deleteButton;
    private JScrollPane scrollPane;
    private ImagePanel logoPanel;

    private JComboBox accountType;

    private final Image account = new ImageIcon("images/account.png").getImage();
    private final Image ely_byLogo = new ImageIcon("images/Ely_by.png").getImage();

    private final Image account25 = new ImageIcon("images/account25.png").getImage();
    private final Image ely_byLogo25 = new ImageIcon("images/Ely_by25.png").getImage();
    JList<Account> accountList = new JList<>();
    DefaultListModel<Account> model = new DefaultListModel<>();

    JPanel accountsPanel = new JPanel();

    private final Color buttonColor = new Color(37, 131, 52);

    public Accounts() {
        setLayout(null);
        setTitle("Ely.by Login");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(580, 340);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setBounds(0, 0, 580, 340);
        panel.setBackground(Color.darkGray);
        panel.setLayout(null);

        logoPanel = new ImagePanel(account);
        logoPanel.setLocation(115, 70);

        accountTypeLbl = new JLabel("Account Type:");
        accountTypeLbl.setBounds(10, 10, 250, 25);
        accountTypeLbl.setForeground(Color.WHITE);
        accountTypeLbl.setHorizontalAlignment(JLabel.CENTER);

        emailLabel = new JLabel("Username:");
        emailLabel.setBounds(10, 110, 250, 25);
        emailLabel.setForeground(Color.WHITE);
        emailLabel.setHorizontalAlignment(JLabel.CENTER);

        emailField = new JTextField();
        emailField.setBounds(10, 135, 250, 25);
        emailField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                if (accountType.getSelectedItem().equals("Ely.by account")) {
                    createAccountLbl.setForeground(Color.WHITE);
                    createAccountLbl.setText("Don't have an Ely.by account? make one");
                } else {
                    createAccountLbl.setText("");
                }
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
            }
            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });

        passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(10, 165, 250, 25);
        passwordLabel.setForeground(Color.GRAY);
        passwordLabel.setHorizontalAlignment(JLabel.CENTER);
        passwordField = new JPasswordField();
        passwordField.setBounds(10, 190, 250, 25);
        passwordField.setEnabled(false);
        passwordField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                if (accountType.getSelectedItem().equals("Ely.by account")) {
                    createAccountLbl.setForeground(Color.WHITE);
                    createAccountLbl.setText("Don't have an Ely.by account? make one");
                }
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
            }
            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });

        loginButton = new JButton("Login");
        loginButton.setBounds(10, 260, 250, 30);
        loginButton.setForeground(Color.WHITE);
        loginButton.setBackground(buttonColor);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loginButton.setEnabled(false);
                String password = String.valueOf(passwordField.getPassword());

                if (accountType.getSelectedItem().equals("Ely.by account")) {
                    if (!password.equals("") && !emailField.getText().equals("")) {
                        startLoginProcess(emailField.getText(), password);
                    } else {
                        loginButton.setEnabled(true);
                        createAccountLbl.setForeground(Color.RED);
                        createAccountLbl.setText("enter password or Email");
                    }
                } else {
                    Utils.playerUUID = "9cb6a52c55bc456b9513f4cf19cdf9e3";
                    Utils.saveUserPrefs();
                    JSONObject accountInfo = new JSONObject();
                    accountInfo.put("id", Utils.playerUUID);
                    accountInfo.put("name", emailField.getText());
                    accountInfo.put("type", "Local account");

                    String idFilePath = Utils.getWorkingDirectory() + "\\.minecraft\\OSLauncher\\Ely.by\\" + emailField.getText() + ".json";

                    Path directoryPath = Paths.get(idFilePath).getParent();
                    if (directoryPath != null){
                        try {
                            Files.createDirectories(directoryPath);
                        } catch (IOException exception) {
                            throw new RuntimeException(exception);
                        }
                    }

                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(idFilePath))){
                        writer.write(accountInfo.toJSONString());
                        System.out.println("Json written successfully");
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }

                    model.addElement(new Account((String) accountInfo.get("id"), (String) accountInfo.get("name"), "0", (String) accountInfo.get("type")));
                    loginButton.setEnabled(true);
                }
            }
        });

        createAccountLbl = new JLabel("");
        createAccountLbl.setBounds(10, 230, 250, 25);
        createAccountLbl.setForeground(Color.WHITE);
        createAccountLbl.setHorizontalAlignment(JLabel.CENTER);

        accountType = new JComboBox<>();
        accountType.setBounds(10, 35, 250, 25);
        accountType.setUI(new CustomComboBoxUI());
        accountType.addItem("Local account");
        accountType.addItem("Ely.by account");
        accountType.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (Objects.equals(accountType.getSelectedItem(), "Local account")){
                    logoPanel.setImage(account);
                    logoPanel.repaint();
                    passwordField.setEnabled(false);
                    passwordLabel.setForeground(Color.GRAY);
                    emailLabel.setText("Username:");
                    createAccountLbl.setText("");
                } else {
                    logoPanel.setImage(ely_byLogo);
                    logoPanel.repaint();
                    passwordField.setEnabled(true);
                    passwordLabel.setForeground(Color.WHITE);
                    emailLabel.setText("Email or Username:");
                    createAccountLbl.setForeground(Color.WHITE);
                    createAccountLbl.setText("Don't have an Ely.by account? make one");
                }
            }
        });

        setAccountList();

        readAccountsJson();

        panel.add(accountsPanel);
        panel.add(emailLabel);
        panel.add(emailField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(loginButton);
        panel.add(createAccountLbl);
        panel.add(accountType);
        panel.add(accountTypeLbl);

        add(scrollPane);
        add(logoPanel);
        add(selectButton);
        add(deleteButton);
        add(panel);
        setVisible(true);
    }

    private void setAccountList() {
        accountList.setModel(model);
        accountList.setBackground(Color.darkGray);
        accountList.setForeground(Color.WHITE);
        accountList.setCellRenderer(new DefaultListCellRenderer(){
            final JLabel cellLabel = new JLabel();

            final  Color selectedColor = new Color(70, 120, 129);

            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value
                    , int index, boolean isSelected, boolean cellHasFocus) {

                cellLabel.setOpaque(true);
                if (value instanceof Account) {
                    Account account1 = (Account) value;
                    if (Objects.equals(account1.getType(), "Local account")) {
                        cellLabel.setIcon(new ImageIcon(account25));
                    } else {
                        cellLabel.setIcon(new ImageIcon(ely_byLogo25));
                    }
                } else {
                    cellLabel.setIcon(new ImageIcon(account25));
                }

                cellLabel.setText(value.toString());

                if (isSelected) {
                    cellLabel.setBackground(selectedColor);
                    cellLabel.setForeground(Color.WHITE);
                } else {
                    cellLabel.setBackground(Color.DARK_GRAY);
                    cellLabel.setForeground(Color.WHITE);
                }

                if (cellHasFocus) {
                    cellLabel.setBackground(Color.gray);
                    cellLabel.setForeground(Color.WHITE);
                } else {
                    cellLabel.setBackground(Color.darkGray);
                    cellLabel.setForeground(Color.WHITE);
                }

                return cellLabel;
            }
        });

        scrollPane = new JScrollPane(accountList);
        scrollPane.setBounds(300, 10, 250, 235);

        selectButton = new JButton("Select");
        selectButton.setBounds(300, 260, 122, 30);
        selectButton.setForeground(Color.WHITE);
        selectButton.setBackground(Color.GRAY);
        selectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = accountList.getSelectedValue().emailUsername;
                WindowManager.Instance.playerNameField.setText(name);
                Utils.auth_player_name = name;
                Utils.saveUserPrefs();
            }
        });

        deleteButton = new JButton("Delete");
        deleteButton.setBounds(427, 260, 122, 30);
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setBackground(Color.GRAY);

        accountsPanel.setBounds(290, 10, 260, 280);
        accountsPanel.setBackground(Color.darkGray);
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

                StringBuilder response = new StringBuilder();
                // open the contents of the URL as an inputStream and print to stdout
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        urlConnection.getInputStream()));
                while ((inputString = in.readLine()) != null) {
                    System.out.println(inputString);
                    response.append(inputString);
                }
                in.close();
                urlConnection.disconnect();
                System.out.println("-------------------------------------------------------------------------------------");

                saveJsonResponse(response.toString());

            } catch (IOException e) {
                loginButton.setEnabled(true);
                throw new RuntimeException(e);
            }
        });
        loginThread.start();
    }

    private void saveJsonResponse(String jsonResponse) {
        JSONParser parser = new JSONParser();

        Object response = null;
        try {
            response = parser.parse(jsonResponse);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        JSONObject jsonResponseObj = (JSONObject) response;

        JSONObject selectedProfile = (JSONObject) jsonResponseObj.get("selectedProfile");
        selectedProfile.put("type", "Ely.by account");
        String id = (String) selectedProfile.get("id");
        String idFilePath = Utils.getWorkingDirectory() + "\\.minecraft\\OSLauncher\\Ely.by\\" + id + ".json";

        Path directoryPath = Paths.get(idFilePath).getParent();
        if (directoryPath != null){
            try {
                Files.createDirectories(directoryPath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(idFilePath))){
            writer.write(selectedProfile.toJSONString());
            System.out.println("Json written successfully");
        } catch (Exception e) {
            e.printStackTrace();
        }

        model.addElement(new Account((String) selectedProfile.get("id"), (String) selectedProfile.get("name"), "0", "Ely.by account"));
        loginButton.setEnabled(true);
    }

    private void readAccountsJson() {
        String overallPath = Utils.getWorkingDirectory() + "\\.minecraft\\OSLauncher\\Ely.by";

        try {
            Set<String> files = listFilesUsingDirectoryStream(overallPath);

            JSONParser parser = new JSONParser();

            for (String fileName: files) {
                String file = overallPath + "\\" + fileName;
                Path path = Paths.get(file);

                byte[] bytes = Files.readAllBytes(path);
                String lines = new String(bytes, StandardCharsets.UTF_8);

                Object parseObj = null;
                try {
                    parseObj = parser.parse(lines);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }

                JSONObject accountInfo = (JSONObject) parseObj;

                model.addElement(new Account((String) accountInfo.get("id"), (String) accountInfo.get("name"), "0", (String) accountInfo.get("type")));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Set<String> listFilesUsingDirectoryStream(String dir) throws IOException {
        Set<String> fileSet = new HashSet<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(dir))) {
            for (Path path : stream) {
                if (!Files.isDirectory(path)) {
                    fileSet.add(path.getFileName()
                            .toString());
                }
            }
        }
        return fileSet;
    }

    private class Account {
        String uuid = "";
        String emailUsername = "";
        String accessToken = "";

        String type = "";

        public Account(String uuid, String emailUsername, String accessToken, String type) {
            this.uuid = uuid;
            this.emailUsername = emailUsername;
            this.accessToken = accessToken;
            this.type = type;
        }

        public String getType() {
            return type;
        }

        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }

        public String getEmailUsername() {
            return emailUsername;
        }

        public void setEmailUsername(String emailUsername) {
            this.emailUsername = emailUsername;
        }

        public String getAccessToken() {
            return accessToken;
        }

        public void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }

        @Override
        public String toString() {
            return emailUsername;
        }
    }
}
