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

public class ForgeDownloader {

    private long librariesSize;

    private double downloadSpeedMbps = 0;

    private long totalBytesRead;

    private double bytesConverter(long bytes) {
        return (double) bytes / (1024 * 1024);
    }

    private void setLibrariesSize(JSONArray libraries) {
        for (Object obj : libraries) {
            JSONObject object = (JSONObject) obj;

            JSONObject downloads = (JSONObject) object.get("downloads");
            JSONObject artifact = (JSONObject) downloads.get("artifact");
            long size = (long) artifact.get("size");

            librariesSize += size;
        }
    }

    public void downloadLibraries(JSONObject forgeVersion, JProgressBar progressBar) {
        JSONArray libraries = (JSONArray) forgeVersion.get("libraries");

        setLibrariesSize(libraries);

        for (Object o: libraries) {
            JSONObject object = (JSONObject) o;

            String name = (String) object.get("name");
            JSONObject downloads = (JSONObject) object.get("downloads");
            JSONObject artifact = (JSONObject) downloads.get("artifact");
            String path = (String) artifact.get("path");
            String url = (String) artifact.get("url");
            long size = (long) artifact.get("size");

            String finalPath = Utils.getWorkingDirectory() + "\\.minecraft\\libraries\\" + path;

            Utils.classPaths.add(finalPath);

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
                double sizeMB = bytesConverter(size);
                String sizeMBFormat = decimalFormat.format(sizeMB);

                System.out.println("Downloading: " + name + " " + sizeMBFormat + "mb");

                totalBytesRead += size;

                int progress = (int) ((totalBytesRead * 100) / librariesSize);

                double totalBytesReadMB = bytesConverter(totalBytesRead);
                String totalBytesReadMBFormatted = decimalFormat.format(totalBytesReadMB);
                String totalLibrariesSizeF = decimalFormat.format(bytesConverter(librariesSize));

                //System.out.println("Downloading: " + name + " " + sizeMBFormat + "mb");

                SwingUtilities.invokeLater(() -> {
                    progressBar.setString("Downloading: " + name + " " + sizeMBFormat + "mb " +
                            totalBytesReadMBFormatted + "mb" + "/" + totalLibrariesSizeF + "mb " + decimalFormat.format(downloadSpeedMbps) + " Mbps"); // Update text
                });

                try (InputStream inputStream = new URL(url).openStream();
                     FileOutputStream outputStream = new FileOutputStream(finalPath)) {
                    long startTime = System.currentTimeMillis();
                    IOUtils.copy(inputStream, outputStream);
                    long endTime = System.currentTimeMillis();

                    double elapsedTimeInSeconds = (endTime - startTime) / 1000.0; // Seconds
                    downloadSpeedMbps = (size / 1024.0 / 1024.0) / elapsedTimeInSeconds;

                    System.out.println(name + " downloaded successfully");
                    SwingUtilities.invokeLater(() -> {
                        progressBar.setValue(progress);
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        }
    }
}
