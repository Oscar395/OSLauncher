package com.example;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.swing.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;

public class FabricDownloader extends ForgeDownloader{

    @Override
    protected void setLibrariesSize(JSONArray libraries) {
    }

    @Override
    public void downloadLibraries(JSONObject forgeVersion, JProgressBar progressBar) {
        JSONArray libraries = (JSONArray) forgeVersion.get("libraries");

        for (Object o: libraries) {
            JSONObject object = (JSONObject) o;

            String name = (String) object.get("name");
            String url = (String) object.get("url");

            String firstPath = name.replace("net.fabricmc", "net/fabricmc")
                    .replace("org.ow2.asm", "org/ow2/asm");

            String secondPath = firstPath.replace(":", "/");

            int jarIndex = name.indexOf(":") + 1;

            String jarname = name.substring(jarIndex);
            jarname = jarname.replace(":", "-") + ".jar";

            String finalPath = Utils.getWorkingDirectory() + "\\.minecraft\\libraries\\" + secondPath.replace("/", "\\") + "\\" + jarname;

            Utils.classPaths.add(finalPath);

            url = url + secondPath + "/" + jarname;

            Path directoryPath = Paths.get(finalPath).getParent();
            if (directoryPath != null) {
                try {
                    Files.createDirectories(directoryPath);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            DecimalFormat decimalFormat = new DecimalFormat("#.##"); // Two decimal places

            if(Files.notExists(Paths.get(finalPath))) {

                System.out.println("Downloading: " + name);

                //System.out.println("Downloading: " + name + " " + sizeMBFormat + "mb");

                SwingUtilities.invokeLater(() -> {
                    progressBar.setString("Downloading: " + name); // Update text
                });

                try (InputStream inputStream = new URL(url).openStream();
                     FileOutputStream outputStream = new FileOutputStream(finalPath)) {
                    IOUtils.copy(inputStream, outputStream);

                    System.out.println(name + " downloaded successfully");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }
}
