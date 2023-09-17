package com.example.visual;

import com.example.Utils;
import com.example.WindowManager;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashSet;
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

    private final Image account = new ImageIcon(getClass().getClassLoader().getResource("account.png")).getImage();
    private final Image ely_byLogo = new ImageIcon(getClass().getClassLoader().getResource("Ely_by.png")).getImage();

    private final Image account25 = new ImageIcon(getClass().getClassLoader().getResource("account25.png")).getImage();
    private final Image ely_byLogo25 = new ImageIcon(getClass().getClassLoader().getResource("Ely_by25.png")).getImage();
    JList<Account> accountList = new JList<>();
    DefaultListModel<Account> model = new DefaultListModel<>();

    JPanel accountsPanel = new JPanel();

    private final Color buttonColor = new Color(37, 131, 52);

    public Accounts() {
        setLayout(null);
        setTitle("Account Login");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(580, 340);
        setResizable(false);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setBounds(0, 0, 580, 340);
        panel.setBackground(Color.darkGray);
        panel.setLayout(null);

        logoPanel = new ImagePanel(account);
        logoPanel.setBackground(Color.DARK_GRAY);
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
                    createAccountLbl.setText("Don't have an Ely.by account? register");
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
                    createAccountLbl.setText("Don't have an Ely.by account? register");
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
                    JSONObject accountInfo = new JSONObject();
                    accountInfo.put("id", "9cb6a52c55bc456b9513f4cf19cdf9e3");
                    accountInfo.put("name", emailField.getText());
                    accountInfo.put("type", "Local account");

                    String idFilePath = Utils.getWorkingDirectory() + "\\.minecraft\\OSLauncher\\Ely.by\\" + emailField.getText() + ".json";
                    String localSkinPath = Utils.getWorkingDirectory() + "\\.minecraft\\OSLauncher\\skins\\" + emailField.getText() + ".png";

                    accountInfo.put("localPath", idFilePath);
                    accountInfo.put("localSkinPath", localSkinPath);
                    accountInfo.put("accessToken", "");

                    Path directoryPath = Paths.get(idFilePath).getParent();
                    if (directoryPath != null){
                        try {
                            Files.createDirectories(directoryPath);
                        } catch (IOException exception) {
                            throw new RuntimeException(exception);
                        }
                    }
                    Path skinDirectoryPath = Paths.get(localSkinPath).getParent();
                    if (skinDirectoryPath != null){
                        try {
                            Files.createDirectories(skinDirectoryPath);
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

                    Image skinImage = new ImageIcon(getClass().getClassLoader().getResource("steve.png")).getImage();
                    BufferedImage bSkinImage = UvSkinMap.toBufferedImage(skinImage);

                    try {
                        ImageIO.write(bSkinImage, "png",new File(localSkinPath));

                        System.out.println("Skin saved successfully");
                    } catch (IOException exception) {
                        throw new RuntimeException(exception);
                    }

                    model.addElement(new Account((String) accountInfo.get("id"), (String) accountInfo.get("name"), "",
                            (String) accountInfo.get("type"), idFilePath, localSkinPath));
                    loginButton.setEnabled(true);
                }
            }
        });

        createAccountLbl = new JLabel("");
        createAccountLbl.setBounds(10, 230, 250, 25);
        createAccountLbl.setForeground(Color.WHITE);
        createAccountLbl.setHorizontalAlignment(JLabel.CENTER);
        createAccountLbl.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        createAccountLbl.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI("https://account.ely.by/register"));

                } catch (IOException | URISyntaxException e1) {
                    e1.printStackTrace();
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
            }
        });

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
                    createAccountLbl.setText("Don't have an Ely.by account? register");
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
                String name = accountList.getSelectedValue().getEmailUsername();
                WindowManager.Instance.playerNameField.setText(name);
                Utils.auth_player_name = name;
                Utils.accountType = accountList.getSelectedValue().getType();
                Utils.playerUUID = accountList.getSelectedValue().getUuid();
                Utils.accountLocalPath = accountList.getSelectedValue().getLocalPath();
                WindowManager.Instance.accountTypeLabel.setText(Utils.accountType);

                Utils.localSkinPath = accountList.getSelectedValue().getLocalSkinPath();

                Image skinImage = new ImageIcon(Utils.localSkinPath).getImage();
                WindowManager.Instance.uvPlayerHead.setImg(skinImage);
                WindowManager.Instance.uvPlayerHead.validate();
                WindowManager.Instance.uvPlayerHead.repaint();

                Utils.saveUserPrefs();

                if (accountList.getSelectedValue().getType().equals("Ely.by account")) {
                    Utils.accessToken = accountList.getSelectedValue().getAccessToken();
                    decodeToken(accountList.getSelectedValue().getAccessToken());
                }

                if (Utils.accountType.equals("Local account")) {
                    Image icon = WindowManager.Instance.accountLocalIcon;
                    WindowManager.Instance.accountTypeLabel.setIcon(new ImageIcon(icon));
                    WindowManager.Instance.playerNameField.setEnabled(true);
                    WindowManager.Instance.saveButton.setEnabled(true);
                    WindowManager.Instance.useButton.setEnabled(true);
                } else {
                    Image icon = WindowManager.Instance.accountEly_byIcon;
                    WindowManager.Instance.playerNameField.setEnabled(false);
                    WindowManager.Instance.saveButton.setEnabled(false);
                    WindowManager.Instance.useButton.setEnabled(false);
                    WindowManager.Instance.accountTypeLabel.setIcon(new ImageIcon(icon));
                }
            }
        });

        deleteButton = new JButton("Delete");
        deleteButton.setBounds(427, 260, 122, 30);
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setBackground(Color.GRAY);
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String filePath = accountList.getSelectedValue().getLocalPath();
                String accountType = accountList.getSelectedValue().getType();
                String localSkinPath = accountList.getSelectedValue().getLocalSkinPath();
                try {
                    Files.delete(Paths.get(filePath));
                    Files.delete(Paths.get(localSkinPath));
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                int index = accountList.getSelectedIndex();

                if (index != -1) {
                    model.remove(index);
                    accountList.setSelectedIndex(0);

                    if (accountList.getSelectedValue() != null) {
                        Utils.localSkinPath = accountList.getSelectedValue().getLocalSkinPath();
                        Utils.saveUserPrefs();

                        Image skinImage = new ImageIcon(Utils.localSkinPath).getImage();
                        WindowManager.Instance.uvPlayerHead.setImg(skinImage);
                        WindowManager.Instance.uvPlayerHead.validate();
                        WindowManager.Instance.uvPlayerHead.repaint();
                    }

                    if (accountType.equals("Ely.by account") && model.getSize() < 1) {
                        Image icon = WindowManager.Instance.accountLocalIcon;
                        WindowManager.Instance.accountTypeLabel.setIcon(new ImageIcon(icon));
                        Utils.accountType = "Local account";
                        WindowManager.Instance.playerNameField.setEnabled(true);
                        WindowManager.Instance.saveButton.setEnabled(true);
                        WindowManager.Instance.useButton.setEnabled(true);
                        WindowManager.Instance.accountTypeLabel.setText(Utils.accountType);
                        Utils.clientToken = "";
                        Utils.saveUserPrefs();
                    } else {
                        String name = accountList.getSelectedValue().getEmailUsername();
                        WindowManager.Instance.playerNameField.setText(name);
                        Utils.auth_player_name = name;
                        Utils.accountType = accountList.getSelectedValue().getType();
                        Utils.playerUUID = accountList.getSelectedValue().getUuid();
                        Utils.accountLocalPath = accountList.getSelectedValue().getLocalPath();
                        WindowManager.Instance.accountTypeLabel.setText(Utils.accountType);
                        Utils.saveUserPrefs();

                        if (accountList.getSelectedValue().getType().equals("Ely.by account")) {
                            Utils.accessToken = accountList.getSelectedValue().getAccessToken();
                            decodeToken(accountList.getSelectedValue().getAccessToken());
                        }
                        if (Utils.accountType.equals("Local account")) {
                            Image icon = WindowManager.Instance.accountLocalIcon;
                            WindowManager.Instance.accountTypeLabel.setIcon(new ImageIcon(icon));
                            WindowManager.Instance.playerNameField.setEnabled(true);
                            WindowManager.Instance.saveButton.setEnabled(true);
                            WindowManager.Instance.useButton.setEnabled(true);
                        } else {
                            Image icon = WindowManager.Instance.accountEly_byIcon;
                            WindowManager.Instance.playerNameField.setEnabled(false);
                            WindowManager.Instance.saveButton.setEnabled(false);
                            WindowManager.Instance.useButton.setEnabled(false);
                            WindowManager.Instance.accountTypeLabel.setIcon(new ImageIcon(icon));
                        }
                    }
                }
            }
        });

        accountsPanel.setBounds(290, 10, 260, 280);
        accountsPanel.setBackground(Color.darkGray);
    }

    private void decodeToken(String token) {
        String[] chunks = token.split("\\.");

        Base64.Decoder decoder = Base64.getUrlDecoder();

        String header = new String(decoder.decode(chunks[0]));
        String payload = new String(decoder.decode(chunks[1]));

        JSONParser parser = new JSONParser();

        try {
            Object parseObj = parser.parse(payload);

            JSONObject payloadJson = (JSONObject) parseObj;

            Utils.clientToken = (String) payloadJson.get("ely-client-token");
            Utils.saveUserPrefs();

        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
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

                if (responseCode == 404) {
                    createAccountLbl.setText("Not Found");
                    createAccountLbl.setForeground(Color.RED);
                }

                if (responseCode == 200) {
                    createAccountLbl.setForeground(Color.green);
                    createAccountLbl.setText("Log in successfully");
                }

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

    public static void refreshAccessToken(String accessToken) {
        Thread refreshThread = new Thread(() -> {

            int responseCode = 0;
            String inputString = null;

            URL url = null;
            try {
                url = new URL("https://authserver.ely.by/auth/refresh");
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }

            try {

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);

                // create the query params
                StringBuffer queryParam = new StringBuffer();
                queryParam.append("accessToken=");
                queryParam.append(accessToken);
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

                JSONParser parser = new JSONParser();

                Object responseObj = null;
                try {
                    responseObj = parser.parse(response.toString());
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }

                JSONObject jsonResponseObj = (JSONObject) responseObj;

                String accessTokenS = (String) jsonResponseObj.get("accessToken");

                JSONObject selectedProfile = (JSONObject) jsonResponseObj.get("selectedProfile");
                selectedProfile.put("type", "Ely.by account");
                String name = (String) selectedProfile.get("name");
                String idFilePath = Utils.getWorkingDirectory() + "\\.minecraft\\OSLauncher\\Ely.by\\" + name + ".json";
                String localSkinPath = Utils.getWorkingDirectory() + "\\.minecraft\\OSLauncher\\skins\\" + name + ".png";
                selectedProfile.put("localPath", idFilePath);
                selectedProfile.put("localSkinPath", localSkinPath);
                selectedProfile.put("accessToken", accessTokenS);

                Utils.accessToken = accessTokenS;
                Utils.auth_player_name = name;
                Utils.playerUUID = (String) selectedProfile.get("id");
                Utils.localSkinPath = localSkinPath;
                Utils.accountLocalPath = idFilePath;
                Utils.saveUserPrefs();

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
                    System.out.println("account refresh written successfully");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                String[] chunks = accessTokenS.split("\\.");

                Base64.Decoder decoder = Base64.getUrlDecoder();

                String header = new String(decoder.decode(chunks[0]));
                String payload = new String(decoder.decode(chunks[1]));

                try {
                    Object parseObj = parser.parse(payload);

                    JSONObject payloadJson = (JSONObject) parseObj;

                    Utils.clientToken = (String) payloadJson.get("ely-client-token");
                    Utils.saveUserPrefs();

                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }

                //Makes skins directory
                Path skinDirectoryPath = Paths.get(localSkinPath).getParent();
                if (skinDirectoryPath != null){
                    try {
                        Files.createDirectories(skinDirectoryPath);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }

                //Downloads and stores the skin png
                BufferedImage skinImage = null;
                try {
                    URL skinUrl = new URL("http://skinsystem.ely.by/skins/" + name);

                    skinImage = ImageIO.read(skinUrl);

                    ImageIO.write(skinImage, "png",new File(localSkinPath));

                    System.out.println("Skin saved successfully");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        refreshThread.start();
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

        String accessToken = (String) jsonResponseObj.get("accessToken");

        JSONObject selectedProfile = (JSONObject) jsonResponseObj.get("selectedProfile");
        selectedProfile.put("type", "Ely.by account");
        String name = (String) selectedProfile.get("name");
        String idFilePath = Utils.getWorkingDirectory() + "\\.minecraft\\OSLauncher\\Ely.by\\" + name + ".json";
        String localSkinPath = Utils.getWorkingDirectory() + "\\.minecraft\\OSLauncher\\skins\\" + name + ".png";
        selectedProfile.put("localPath", idFilePath);
        selectedProfile.put("localSkinPath", localSkinPath);
        selectedProfile.put("accessToken", accessToken);

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

        //Makes skins directory
        Path skinDirectoryPath = Paths.get(localSkinPath).getParent();
        if (skinDirectoryPath != null){
            try {
                Files.createDirectories(skinDirectoryPath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        //Downloads and stores the skin png
        BufferedImage skinImage = null;
        try {
            URL skinUrl = new URL("http://skinsystem.ely.by/skins/" + name);

            skinImage = ImageIO.read(skinUrl);

            ImageIO.write(skinImage, "png",new File(localSkinPath));

            System.out.println("Skin saved successfully");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //Adds the new element Account with its own attributes
        model.addElement(new Account((String) selectedProfile.get("id"), (String) selectedProfile.get("name"), (String) jsonResponseObj.get("accessToken"),
                "Ely.by account", (String) selectedProfile.get("localPath"), (String) selectedProfile.get("localSkinPath")));

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

                model.addElement(new Account((String) accountInfo.get("id"), (String) accountInfo.get("name"), (String) accountInfo.get("accessToken"),
                        (String) accountInfo.get("type"), (String) accountInfo.get("localPath"), (String) accountInfo.get("localSkinPath")));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Set<String> listFilesUsingDirectoryStream(String dir) throws IOException {
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

    public class Account {
        String uuid = "";
        String emailUsername = "";
        String accessToken = "";

        String type = "";
        String localPath = "";

        String localSkinPath = "";

        public Account(String uuid, String emailUsername, String accessToken, String type, String localPath, String localSkinPath) {
            this.uuid = uuid;
            this.emailUsername = emailUsername;
            this.accessToken = accessToken;
            this.type = type;
            this.localPath = localPath;
            this.localSkinPath = localSkinPath;
        }

        public String getLocalSkinPath() {
            return localSkinPath;
        }

        public String getLocalPath() {
            return localPath;
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
