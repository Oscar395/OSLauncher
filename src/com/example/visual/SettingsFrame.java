package com.example.visual;

import com.example.Utils;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.reflect.Method;

public class SettingsFrame extends JFrame {

    private JPanel mainPanel,jvmPanel;
    private JTextField jvmArgs, LegacyPathField, runtimeGammaField;
    private JButton jreFileChooser, jdkFileChooser, SaveBtn, CancelBtn, DefaultsBtn;
    private TitledBorder border;
    private Border blackline;

    private JComboBox dedicatedMemory;
    private JLabel memory , Args, LegacyPathLbl, GammaPathLbl;

    private final Color buttonsColor = new Color(110, 110, 110);

    public SettingsFrame() {
        setLayout(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setTitle("Settings");
        setSize(400, 400);
        setBackground(Color.darkGray);
        setResizable(false);
        setVisible(true);
        setLocationRelativeTo(null);

        border = BorderFactory.createTitledBorder("JVM Settings");
        border.setTitleColor(Color.WHITE);
        blackline = border;

        mainPanel = new JPanel();
        mainPanel.setBounds(0, 0, 400, 400);
        mainPanel.setLayout(null);
        mainPanel.setBackground(Color.darkGray);

        InitBox();

        memory = new JLabel("Dedicated Memory");
        memory.setBounds(20, 20, 300, 25);
        memory.setForeground(Color.WHITE);

        Args = new JLabel("JVM Arguments");
        Args.setBounds(20, 80, 300, 25);
        Args.setForeground(Color.WHITE);

        setJvmArgs();
        setUpButtons();
        setupJrePath();

        jvmPanel = new JPanel();
        jvmPanel.setBounds(5, 5, 385, 270);
        jvmPanel.setLayout(null);
        jvmPanel.setBackground(Color.darkGray);
        jvmPanel.setBorder(blackline);

        jvmPanel.add(dedicatedMemory);
        jvmPanel.add(memory);
        jvmPanel.add(Args);
        jvmPanel.add(jvmArgs);
        jvmPanel.add(LegacyPathLbl);
        jvmPanel.add(LegacyPathField);
        jvmPanel.add(jreFileChooser);

        jvmPanel.add(runtimeGammaField);
        jvmPanel.add(GammaPathLbl);
        jvmPanel.add(jdkFileChooser);

        mainPanel.add(SaveBtn);
        mainPanel.add(CancelBtn);
        mainPanel.add(DefaultsBtn);

        mainPanel.add(jvmPanel);
        add(mainPanel);
    }

    private void setJvmArgs() {
        jvmArgs = new JTextField(" -Xmx2G -XX:+UnlockExperimentalVMOptions -XX:+UseG1GC -XX:G1NewSizePercent=20" +
                " -XX:G1ReservePercent=20 -XX:MaxGCPauseMillis=50 -XX:G1HeapRegionSize=32M");
        jvmArgs.setBounds(20, 105, 345, 25);
    }

    private void InitBox() {

        dedicatedMemory = new JComboBox<>();
        dedicatedMemory.setBounds(20, 45, 345, 25);

        OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);

        // Get the total physical memory (RAM) size in bytes
        try {
            Method m = operatingSystemMXBean.getClass().
                    getDeclaredMethod("getTotalPhysicalMemorySize");

            m.setAccessible(true);

            Object value = m.invoke(operatingSystemMXBean);

            if (value != null)
            {
                long totalRam = (long) value;
                double totalRamGB = totalRam / (1024.0 * 1024 * 1024);

                long roundedTotal = Math.round(totalRamGB);

                for (int i = 1; i <= roundedTotal; i++) {
                    dedicatedMemory.addItem(i + "GB");
                }

                System.out.println("Total RAM: " + roundedTotal + " GB");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        dedicatedMemory.setMaximumRowCount(14);
        dedicatedMemory.setUI(new CustomComboBoxUI());
    }

    private void setupJrePath() {
        LegacyPathLbl = new JLabel("Jre Legacy Path or Java 8");
        LegacyPathLbl.setBounds(20, 135, 300, 25);
        LegacyPathLbl.setForeground(Color.WHITE);

        LegacyPathField = new JTextField(Utils.LegacyPath);
        LegacyPathField.setBounds(20, 160, 315, 25);
        LegacyPathField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SaveBtn.setEnabled(true);
                DefaultsBtn.setEnabled(true);
            }
        });

        jreFileChooser = new JButton("...");
        jreFileChooser.setBounds(335, 160, 30, 25);
        jreFileChooser.setBackground(Color.LIGHT_GRAY);
        jreFileChooser.setForeground(Color.WHITE);
        jreFileChooser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                OpenFileViaExplorer(LegacyPathField);
            }
        });

        GammaPathLbl = new JLabel("Java 17 or above runtime Path");
        GammaPathLbl.setBounds(20, 190, 300, 25);
        GammaPathLbl.setForeground(Color.WHITE);

        runtimeGammaField = new JTextField(Utils.GammaPath);
        runtimeGammaField.setBounds(20, 215, 315, 25);
        runtimeGammaField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SaveBtn.setEnabled(true);
                DefaultsBtn.setEnabled(true);
            }
        });

        jdkFileChooser = new JButton("...");
        jdkFileChooser.setBounds(335, 215, 30, 25);
        jdkFileChooser.setBackground(Color.LIGHT_GRAY);
        jdkFileChooser.setForeground(Color.WHITE);
        jdkFileChooser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                OpenFileViaExplorer(runtimeGammaField);
            }
        });

    }

    public boolean OpenFileViaExplorer(JTextField textField) {
        try {
            JFileChooser fileChooser = new JFileChooser();

            FileNameExtensionFilter filter = new FileNameExtensionFilter("Exe files", "exe");

            fileChooser.setFileFilter(filter);

            fileChooser.setCurrentDirectory(new File("."));

            int result = fileChooser.showOpenDialog(null);

            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = new File(fileChooser.getSelectedFile().getAbsolutePath());
                System.out.println("File Path: " + selectedFile);
                textField.setText(selectedFile.toString());
                SaveBtn.setEnabled(true);
                DefaultsBtn.setEnabled(true);

                return true;
            } else if (result == JFileChooser.CANCEL_OPTION) {
                System.out.println("Cancelled");
                return false;
            }
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
        return false;
    }

    private void setUpButtons() {
        SaveBtn = new JButton("Save");
        SaveBtn.setBounds(210, 345, 80, 25);
        SaveBtn.setBackground(buttonsColor);
        SaveBtn.setForeground(Color.WHITE);
        SaveBtn.setEnabled(false);
        SaveBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SaveBtn.setEnabled(false);
                Utils.LegacyPath = LegacyPathField.getText();
                Utils.GammaPath = runtimeGammaField.getText();
                Utils.saveUserPrefs();
            }
        });

        CancelBtn = new JButton("Cancel");
        CancelBtn.setBounds(300, 345, 80, 25);
        CancelBtn.setBackground(buttonsColor);
        CancelBtn.setForeground(Color.WHITE);
        CancelBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        DefaultsBtn = new JButton("Default");
        DefaultsBtn.setBounds(10, 345, 80, 25);
        DefaultsBtn.setBackground(buttonsColor);
        DefaultsBtn.setForeground(Color.WHITE);
        DefaultsBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultsBtn.setEnabled(false);
                SaveBtn.setEnabled(true);
                Utils.LegacyPath = Utils.getWorkingDirectory() + "\\.minecraft\\runtime\\jre-legacy\\bin\\javaw.exe";
                Utils.GammaPath = Utils.getWorkingDirectory() + "\\.minecraft\\runtime\\jre-legacy\\bin\\javaw.exe";
                Utils.saveUserPrefs();
                runtimeGammaField.setText(Utils.GammaPath);
                LegacyPathField.setText(Utils.LegacyPath);
            }
        });


    }
}
