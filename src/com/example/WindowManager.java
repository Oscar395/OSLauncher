package com.example;

import com.example.visual.*;
import javafx.scene.control.Skin;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class WindowManager extends JFrame{
    public JButton settings, playButton, accountBtn, searchBtn, useButton, saveButton;
    public JLabel playerNameLb, selectedVersion, vTypeLb, enterUsername, searchStateLb, requestApiLbl, skinLabel, capeLabel,
            accountTypeLabel;
    public JProgressBar progressBar;
    public JTextField playerNameField, searchUsernameField;

    public JPanel panel1;
    public  JPanel panel2;
    public JPanel skinsPanel;
    public JTabbedPane jTabbedPane;
    public JComboBox versionsList, versionType, requestAPIType;
    public static WindowManager Instance;

    private ImagePanel imagePanel, versionIcon;

    private UvSkinMap uvSkin;
    public uvCapeMap uvCape, uvPlayerHead;

    private final Color buttonsColor = new Color(110, 110, 110);

    private final Image forgeIcon = new ImageIcon("images/forge_icon.png").getImage();
    private final Image vanillaIcon = new ImageIcon("images/vanilla_icon.png").getImage();
    private final Image optifineIcon = new ImageIcon("images/optifine_icon.png").getImage();
    private final Image snapshotIcon = new ImageIcon("images/dirt_icon.png").getImage();

    public final Image accountEly_byIcon = new ImageIcon("images/Ely_by25.png").getImage();
    public final Image accountLocalIcon = new ImageIcon("images/account25.png").getImage();

    //JSON writer
    private JsonWriterAndReader jsonWriterAndReader = new JsonWriterAndReader();
    public WindowManager() {

        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(Utils.WIDTH, Utils.HEIGHT);
        setResizable(false);
        setTitle("McLauncher");
        setVisible(true);
        setLocationRelativeTo(null);
        Instance = this;

        imagePanel = new ImagePanel(new ImageIcon("images/background.png").getImage());
        versionIcon = new ImagePanel(new ImageIcon("images/vanilla_icon.png").getImage());

        panel1 = new JPanel();
        panel1.setLayout(null);
        panel1.setBackground(Color.darkGray);

        //panel1.setBorder(new EmptyBorder(10, 10,10 ,10));

        panel2 = new JPanel();
        panel2.setLayout(null);

        Console console = new Console();
        panel2.add(console.scrollPane);

        skinsPanel = new JPanel();
        skinsPanel.setLayout(null);
        skinsPanel.setBackground(Color.darkGray);

        initComponents();
        addSkinUI();

        uvPlayerHead = new uvCapeMap(8, 8, 8, 8, 4);
        uvPlayerHead.setBounds(10, 35, 32, 32);
        uvPlayerHead.setImg(new ImageIcon(Utils.localSkinPath).getImage());
        panel1.add(uvPlayerHead);
        uvPlayerHead.repaint();

        jTabbedPane = new JTabbedPane();

        jTabbedPane.setBounds(0,0, Utils.WIDTH - 5, Utils.HEIGHT - this.getInsets().top);
        jTabbedPane.setBackground(new Color(178, 182, 185));
        jTabbedPane.setForeground(Color.WHITE);
        jTabbedPane.setUI(new CustomTabbedPaneUI());
        jTabbedPane.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));

        jTabbedPane.add("Main Page", panel1);
        jTabbedPane.add("Console", panel2);
        jTabbedPane.add("Skins", skinsPanel);
        add(jTabbedPane);

        versionIcon.setLocation(280, 482);
        panel1.add(imagePanel);
        panel1.add(versionIcon);
        if (Utils.accountType.equals("Ely.by account")) {
            Accounts.refreshAccessToken(Utils.accessToken);
        }
    }

    private void initComponents() {
        playerNameLb = new JLabel("Player Name:");
        playerNameLb.setBounds(680, 5, 150, 25);
        playerNameLb.setForeground(Color.white);
        playerNameField = new JTextField();
        playerNameField.setBounds(765, 5, 120, 25);
        playerNameField.setFont(new Font(Font.SANS_SERIF,  Font.BOLD, 12));
        playerNameField.setText(Utils.auth_player_name);
        playerNameField.setEnabled(Utils.accountType.equals("Local account"));

        panel1.add(playerNameField);
        panel1.add(playerNameLb);
        addButtons(panel1);

        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setString("");
        progressBar.setSize(890, 4);
        progressBar.setLocation(0,452);
        progressBar.setBackground(Color.DARK_GRAY);
        progressBar.setForeground(new Color(105, 157, 94));
        panel1.add(progressBar);

        versionsList = new JComboBox<>();
        versionsList.setBounds(660, 505, 220, 25);
        versionsList.setUI(new CustomComboBoxUI());
        versionsList.setMaximumRowCount(12);
        versionsList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String item = (String) versionsList.getSelectedItem();

                if (item != null) {
                    if (item.contains("forge")) {
                        versionIcon.setImage(forgeIcon);
                        versionIcon.repaint();
                    } else if (item.contains("OptiFine")) {
                        versionIcon.setImage(optifineIcon);
                        versionIcon.repaint();
                    } else if (item.contains("w")) {
                        versionIcon.setImage(snapshotIcon);
                        versionIcon.repaint();
                    } else {
                        versionIcon.setImage(vanillaIcon);
                        versionIcon.repaint();
                    }
                }
            }
        });
        InitVersions(versionsList);
        panel1.add(versionsList);

        selectedVersion = new JLabel("Selected Version: ");
        selectedVersion.setBounds(550, 505, 130, 25 );
        selectedVersion.setForeground(Color.white);
        panel1.add(selectedVersion);

        versionType = new JComboBox<>();
        versionType.setBounds(660, 475, 220, 25);
        versionType.addItem("release");
        versionType.addItem("snapshot");
        versionType.addItem("old_beta");
        versionType.addItem("old_alpha");
        versionType.setUI(new CustomComboBoxUI());
        versionType.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Utils.VersionType = versionType.getSelectedItem().toString();
                jsonWriterAndReader.updateVersionsList(versionsList);
                //System.out.println(versionType.getSelectedItem().toString());
            }
        });
        panel1.add(versionType);

        accountTypeLabel = new JLabel(Utils.accountType);
        accountTypeLabel.setBounds(10, 5, 150, 25);
        accountTypeLabel.setForeground(Color.WHITE);
        if (Utils.accountType.equals("Local account")) {
            accountTypeLabel.setIcon(new ImageIcon(accountLocalIcon));
        } else {
            accountTypeLabel.setIcon(new ImageIcon(accountEly_byIcon));
        }
        panel1.add(accountTypeLabel);

        vTypeLb = new JLabel("Type: ");
        vTypeLb.setBounds(550, 475, 130, 25);
        vTypeLb.setForeground(Color.white);
        panel1.add(vTypeLb);
    }

    private void InitVersions(JComboBox versionsBox) {
        jsonWriterAndReader.readVersionsList(versionsBox);
    }

    public void addButtons(JPanel panelToAdd) {

        settings = new JButton("Settings");
        settings.setBounds(10, 475, 220, 25);
        settings.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SettingsFrame settingsFrame = new SettingsFrame();

                setEnabled(false);
                settingsFrame.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosed(WindowEvent e) {
                        super.windowClosed(e);
                        setEnabled(true);
                        setFocusable(true);
                    }
                });
            }
        });
        settings.setFont(new Font("Arial", Font.BOLD, 12));
        settings.setBackground(buttonsColor);
        settings.setForeground(Color.WHITE);
        panelToAdd.add(settings);

        accountBtn = new JButton("Account");
        accountBtn.setBounds(10, 505, 220, 25);
        accountBtn.setFont(new Font("Arial", Font.BOLD, 12));
        accountBtn.setBackground(buttonsColor);
        accountBtn.setForeground(Color.WHITE);
        accountBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Accounts accounts = new Accounts();

                setEnabled(false);
                accounts.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosed(WindowEvent e) {
                        super.windowClosed(e);
                        setEnabled(true);
                        setFocusable(true);
                    }
                });
            }
        });
        panelToAdd.add(accountBtn);

        playButton = new JButton("Play");
        playButton.setBounds((int) (Utils.WIDTH * 0.5) - 70, 482, 150, 40);
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                playButton.setEnabled(false);
                progressBar.setString("Copying Java Runtime Environment...");

                //sets a java agent if the account is Ely.by account
                if (Utils.accountType.equals("Ely.by account")) {
                    Utils.javaAgentArgs = " -javaagent:" + Utils.javaAgentPath + "=ely.by";
                } else {
                    Utils.javaAgentArgs = "";
                    Utils.clientToken = "";
                    Utils.playerUUID = "9cb6a52c55bc456b9513f4cf19cdf9e3";
                    Utils.saveUserPrefs();
                }
                Main.tryToCopyJre(Instance);
                progressBar.setString("Getting Version...");
                jsonWriterAndReader.downloadSelectedVersion(versionsList, progressBar);
            }
        });
        playButton.setFont(new Font("Arial", Font.BOLD, 14));
        playButton.setBackground(buttonsColor);
        playButton.setForeground(Color.WHITE);
        panelToAdd.add(playButton);

        saveButton = new JButton("Save");
        saveButton.setBounds(765, 35, 120, 25);
        saveButton.setFont(new Font("Arial", Font.BOLD, 12));
        saveButton.setBackground(buttonsColor);
        saveButton.setForeground(Color.WHITE);
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Utils.auth_player_name = playerNameField.getText();
                Utils.saveUserPrefs();
                updateAccountJson();
            }
        });
        saveButton.setEnabled(Utils.accountType.equals("Local account"));
        panelToAdd.add(saveButton);
    }

    private void addSkinUI() {
        enterUsername = new JLabel("Enter Username");
        enterUsername.setForeground(Color.WHITE);
        enterUsername.setBounds(5, 5, 150, 25);
        skinsPanel.add(enterUsername);

        searchUsernameField = new JTextField();
        searchUsernameField.setBounds(5, 30, 220, 25);
        skinsPanel.add(searchUsernameField);

        searchStateLb = new JLabel("...");
        searchStateLb.setForeground(Color.WHITE);
        searchStateLb.setBounds(230, 30, 150, 25);
        skinsPanel.add(searchStateLb);

        requestApiLbl = new JLabel("Request API:");
        requestApiLbl.setBounds(5, 60, 120, 25);
        requestApiLbl.setForeground(Color.WHITE);
        skinsPanel.add(requestApiLbl);

        requestAPIType = new JComboBox<>();
        requestAPIType.setBounds(90, 60, 135, 25);
        requestAPIType.setUI(new CustomComboBoxUI());
        requestAPIType.addItem("Mojang API");
        requestAPIType.addItem("Ely.by API");
        skinsPanel.add(requestAPIType);

        searchBtn = new JButton("Search...");
        searchBtn.setBounds(5, 95, 220, 25);
        searchBtn.setFont(new Font("Arial", Font.BOLD, 12));
        searchBtn.setBackground(buttonsColor);
        searchBtn.setForeground(Color.WHITE);
        searchBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (searchUsernameField.getText() != null) {
                    searchBtn.setEnabled(false);
                    searchStateLb.setText("Searching...");
                    uvCape.setImg(null);
                    uvCape.repaint();
                    SkinRequest skinRequest = new SkinRequest(searchUsernameField.getText(), (String) requestAPIType.getSelectedItem());
                    skinRequest.start();
                }
            }
        });
        skinsPanel.add(searchBtn);

        //skinPreview = new ImagePanel(null);
        //skinPreview.setBounds(10, 90, 64, 64);
        //skinsPanel.add(skinPreview);
        skinLabel = new JLabel("Skin");
        skinLabel.setBounds(350, 415, 260, 35);
        skinLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        skinLabel.setHorizontalAlignment(JLabel.CENTER);
        skinLabel.setForeground(Color.WHITE);
        skinsPanel.add(skinLabel);

        uvSkin = new UvSkinMap(new ImageIcon(Utils.localSkinPath).getImage());
        uvSkin.setBounds(350, 10, 260, 400);
        uvSkin.setBackground(Color.gray);
        skinsPanel.add(uvSkin);

        capeLabel = new JLabel("Cape");
        capeLabel.setBounds(615, 415, 260, 35);
        capeLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        capeLabel.setHorizontalAlignment(JLabel.CENTER);
        capeLabel.setForeground(Color.WHITE);
        skinsPanel.add(capeLabel);

        uvCape = new uvCapeMap(10, 17, 1, 0, 8);
        uvCape.setBounds(615, 10, 260, 400);
        uvCape.setBackground(Color.gray);
        skinsPanel.add(uvCape);

        useButton = new JButton("Equip skin");
        useButton.setBounds(5, 125, 220, 25);
        useButton.setFont(new Font("Arial", Font.BOLD, 12));
        useButton.setBackground(buttonsColor);
        useButton.setForeground(Color.WHITE);
        useButton.setToolTipText("You must have a Local account to use this feature");
        useButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                useButton.setEnabled(false);
                if (SkinRequest.uuid != null) {
                    Utils.playerUUID = SkinRequest.uuid;
                    Image saveImage = uvSkin.getImage();
                    String localSkinPath = Utils.localSkinPath;

                    BufferedImage skinImage = UvSkinMap.toBufferedImage(saveImage);
                    try {
                        ImageIO.write(skinImage, "png",new File(localSkinPath));

                        System.out.println("Skin saved successfully");
                    } catch (IOException exception) {
                        throw new RuntimeException(exception);
                    }

                    uvPlayerHead.setImg(skinImage);
                    uvPlayerHead.repaint();

                    Utils.saveUserPrefs();
                    updateAccountJson();
                }
            }
        });
        useButton.setEnabled(Utils.accountType.equals("Local account"));
        skinsPanel.add(useButton);
    }

    private void updateAccountJson() {
        if (Utils.accountLocalPath != null) {
            try {
                Path accountPath = Paths.get(Utils.accountLocalPath);

                JSONParser parser = new JSONParser();

                byte[] bytes = Files.readAllBytes(accountPath);
                String lines = new String(bytes, StandardCharsets.UTF_8);

                Object parseObj = null;
                try {
                    parseObj = parser.parse(lines);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }

                JSONObject accountInfo = (JSONObject) parseObj;

                accountInfo.put("id", Utils.playerUUID);
                accountInfo.put("name", Utils.auth_player_name);
                accountInfo.put("localSkinPath", Utils.localSkinPath);

                try (BufferedWriter writer = new BufferedWriter(new FileWriter(Utils.accountLocalPath))){
                    writer.write(accountInfo.toJSONString());
                    System.out.println("Json written successfully");
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void loadSkin(Image skinImg) {
        if (skinImg != null) {
            System.out.println("setting Skin");
            useButton.setEnabled(Utils.accountType.equals("Local account"));
            searchBtn.setEnabled(true);

            uvSkin.setImage(skinImg);
            uvSkin.repaint();

            searchStateLb.setText("...");
            searchStateLb.setForeground(Color.green);

            System.out.println("skin set");
        }
    }

    public void loadCape(Image capeImg) {
        System.out.println("setting cape");

        uvCape.setImg(capeImg);
        uvCape.repaint();

        System.out.println("cape set");
    }

}
