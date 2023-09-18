package com.example;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.swing.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DecimalFormat;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Downloader extends Thread{
    private JSONObject jsonObject, version;

    private JProgressBar progressBar;

    private long totalSize, totalLibrariesSize;
    private double totalSizeMB;

    private long totalBytesRead;

    private double downloadSpeedMbps = 0;

    public Downloader (JSONObject version, JSONObject assetsList, JProgressBar progressBar) {
        this.jsonObject = assetsList;
        this.version = version;
        this.progressBar = progressBar;
    }

    private double bytesConverter(long bytes) {
        return (double) bytes / (1024 * 1024);
    }

    private void downloadVersionJar() {
        JSONObject versionDownloads = (JSONObject) version.get("downloads");
        JSONObject client = (JSONObject) versionDownloads.get("client");
        JSONObject clientMappings = (JSONObject) versionDownloads.get("client_mappings");
        String clientURL = (String) client.get("url");
        long Size = (long) client.get("size");

        String clientPath = Utils.getWorkingDirectory() + "\\.minecraft\\versions\\" + Utils.downloadedVersion +
                "\\" + Utils.downloadedVersion + ".jar";

        Utils.primary_jar = clientPath;
        Utils.natives_directory = Utils.getWorkingDirectory() + "\\.minecraft\\bin\\" + client.get("sha1");

        if (Utils.isForgeVersion) {
            JSONObject forgeVersion = Utils.forgeVersion;
            Utils.mainClass = (String) forgeVersion.get("mainClass");
        } else if (Utils.isOptifineVersion){
            JSONObject optifineVersion = Utils.optifineVersion;
            Utils.mainClass = (String) optifineVersion.get("mainClass");
        } else if (Utils.isFabricVersion) {
            JSONObject fabricVersion = Utils.fabricVersion;
            Utils.mainClass = (String) fabricVersion.get("mainClass");
        } else {
            Utils.mainClass = (String) version.get("mainClass");
        }
        String clientMappingsPath = Utils.getWorkingDirectory() + "/.minecraft/versions/" + Utils.downloadedVersion +
                "/client.txt";

        File file = new File(clientPath);

        if(Files.notExists(Paths.get(clientPath)) || file.length() == 0) {
            System.out.println("Downloading client: " + Utils.downloadedVersion);
            int bufferSize = 1024;
            try (InputStream inputStream = new URL(clientURL).openStream();
                 FileOutputStream outputStream = new FileOutputStream(clientPath)) {

                byte[] buffer = new byte[bufferSize];
                int bytesRead;
                long totalBytesRead = 0;


                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    long startTime = System.currentTimeMillis();
                    outputStream.write(buffer, 0, bytesRead);
                    totalBytesRead += bytesRead;

                    double totalBytesReadMB = bytesConverter(totalBytesRead);
                    double sizeMB = bytesConverter(Size);
                    DecimalFormat decimalFormat = new DecimalFormat("#.##"); // Two decimal places
                    String formattedReadBytes = decimalFormat.format(totalBytesReadMB);
                    String formattedSizeMB = decimalFormat.format(sizeMB);

                    int progress = (int) ((totalBytesRead * 100) / Size);
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setString("Downloading Client " + Utils.downloadedVersion + ".jar " +
                                    formattedReadBytes + "mb/" + formattedSizeMB + "mb " + decimalFormat.format(downloadSpeedMbps) + " Mbps");
                            progressBar.setValue(progress);
                        }
                    });
                    long endTime = System.currentTimeMillis();

                    double elapsedTimeInSeconds = (endTime - startTime) / 1000.0; // Seconds

                    downloadSpeedMbps = (bufferSize / 1024.0 / 1024.0) / elapsedTimeInSeconds;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    progressBar.setValue(0);
                }
            });

            try (InputStream inputStream = new URL((String) clientMappings.get("url")).openStream();
                 FileOutputStream outputStream = new FileOutputStream(clientMappingsPath)) {

                byte[] buffer = new byte[bufferSize];
                int bytesRead;
                long totalBytesRead = 0;


                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    long startTime = System.currentTimeMillis();
                    outputStream.write(buffer, 0, bytesRead);
                    totalBytesRead += bytesRead;

                    double totalBytesReadMB = bytesConverter(totalBytesRead);
                    double mappingsSizeMB = bytesConverter((Long) clientMappings.get("size"));
                    DecimalFormat decimalFormat = new DecimalFormat("#.##"); // Two decimal places
                    String formattedReadBytes = decimalFormat.format(totalBytesReadMB);
                    String formattedMSizeMB = decimalFormat.format(mappingsSizeMB);

                    int progress = (int) ((totalBytesRead * 100) / Size);
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setString("Downloading client_mappings " + "client.txt " +
                                    formattedReadBytes + "mb/" + formattedMSizeMB + "mb " + decimalFormat.format(downloadSpeedMbps) + " Mbps");
                            progressBar.setValue(progress);
                        }
                    });
                    long endTime = System.currentTimeMillis();

                    double elapsedTimeInSeconds = (endTime - startTime) / 1000.0; // Seconds

                    downloadSpeedMbps = (bufferSize / 1024.0 / 1024.0) / elapsedTimeInSeconds;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                progressBar.setString("Reading Assets...");
                progressBar.setValue(0);
            }
        });
    }

    @Override
    public void run() {

        downloadVersionJar();

        for(Object key : jsonObject.keySet()) {
            JSONObject asset = (JSONObject) jsonObject.get(key);

            String Hash = (String) asset.get("hash");
            String twoLetters = Hash.substring(0, 2);

            String finalPath = Utils.getWorkingDirectory() + "/.minecraft/assets/objects/" + twoLetters + "/" + Hash;

            Path directoryPath = Paths.get(finalPath).getParent();
            if (directoryPath != null) {
                try {
                    Files.createDirectories(directoryPath);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            if(Files.notExists(Paths.get(finalPath))) {
                long bytesSize = (long) asset.get("size");
                totalSize += bytesSize;
            }

        }

        totalSizeMB = bytesConverter(totalSize);
        DecimalFormat decimalFormat = new DecimalFormat("#.##"); // Two decimal places
        String totalSizeMBFormatted = decimalFormat.format(totalSizeMB);

        for (Object key : jsonObject.keySet()) {
            JSONObject asset = (JSONObject) jsonObject.get(key);

            String Hash = (String) asset.get("hash");
            String twoLetters = Hash.substring(0, 2);

            long bytesSize = (long) asset.get("size");
            double mbSize = bytesConverter(bytesSize);

            String Furl = "https://resources.download.minecraft.net/" + twoLetters + "/" + Hash;

            String finalPath = Utils.getWorkingDirectory() + "/.minecraft/assets/objects/" + twoLetters + "/" + Hash;
            Path directoryPath = Paths.get(finalPath).getParent();
            if (directoryPath != null) {
                try {
                    Files.createDirectories(directoryPath);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            if(Files.notExists(Paths.get(finalPath))) {

                String keyName = (String) key;
                System.out.println("Downloading: " + keyName + " " + mbSize + "mb");

                totalBytesRead += bytesSize;

                int progress = (int) ((totalBytesRead * 100) / totalSize);

                double totalBytesReadMB = bytesConverter(totalBytesRead);
                String formattedSize = decimalFormat.format(mbSize);
                String totalBytesReadMBFormatted = decimalFormat.format(totalBytesReadMB);

                SwingUtilities.invokeLater(() -> {
                    progressBar.setString("Downloading: " + keyName + " " + formattedSize + "mb " +
                            totalBytesReadMBFormatted + "mb" + "/" + totalSizeMBFormatted + "mb " + decimalFormat.format(downloadSpeedMbps) + " Mbps"); // Update text
                });

                try (InputStream inputStream = new URL(Furl).openStream();
                     FileOutputStream outputStream = new FileOutputStream(finalPath)) {
                    long startTime = System.currentTimeMillis();
                    IOUtils.copy(inputStream, outputStream);
                    long endTime = System.currentTimeMillis();

                    double elapsedTimeInSeconds = (endTime - startTime) / 1000.0; // Seconds
                    downloadSpeedMbps = (bytesSize / 1024.0 / 1024.0) / elapsedTimeInSeconds;

                    System.out.println(keyName + " downloaded successfully");
                    SwingUtilities.invokeLater(() -> {
                        progressBar.setValue(progress);
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }

        SwingUtilities.invokeLater(() -> {
            progressBar.setString("Getting Libraries...");
            progressBar.setValue(0);
        });
        totalBytesRead = 0;
        System.out.println("Getting Libraries...");

        downloadLibraries();
    }

    private void downloadLibraries() {
        JSONArray libraries = (JSONArray) version.get("libraries");

        for (Object o : libraries) {
            JSONObject library = (JSONObject) o;

            if (library.containsKey("rules")) {
                JSONArray rules = (JSONArray) library.get("rules");

                for (Object obj : rules) {
                    JSONObject object = (JSONObject) obj;

                    if (object.containsKey("action")) {
                        String action = (String) object.get("action");

                        if (action.equals("allow")) {
                            if (object.containsKey("os")) {
                                JSONObject os = (JSONObject) object.get("os");

                                String name = (String) os.get("name");

                                if(name.equals("windows")) {
                                    JSONObject downloads = (JSONObject) library.get("downloads");
                                    if (downloads.containsKey("artifact")) {
                                        JSONObject artifact = (JSONObject) downloads.get("artifact");
                                        if (artifact.containsKey("size")) {
                                            long size = (long) artifact.get("size");
                                            totalLibrariesSize += size;
                                        }
                                    }
                                }
                            } else {
                                JSONObject downloads = (JSONObject) library.get("downloads");
                                if (downloads.containsKey("artifact")) {
                                    JSONObject artifact = (JSONObject) downloads.get("artifact");
                                    if (artifact.containsKey("size")) {
                                        long size = (long) artifact.get("size");
                                        totalLibrariesSize += size;
                                    }
                                }
                            }
                        }
                    }
                }

            } else {
                JSONObject downloads = (JSONObject) library.get("downloads");
                if (downloads.containsKey("artifact")) {
                    JSONObject artifact = (JSONObject) downloads.get("artifact");
                    if (artifact.containsKey("size")) {
                        long size = (long) artifact.get("size");
                        totalLibrariesSize += size;
                    }
                }
            }

            if(library.containsKey("natives")) {
                JSONObject natives = (JSONObject) library.get("natives");
                if (natives.containsKey("windows")) {
                    String windows = (String) natives.get("windows");
                    String windowsArch = windows.replace("${arch}", Utils.getSystemArch());
                    JSONObject downloads = (JSONObject) library.get("downloads");
                    JSONObject classifiers = (JSONObject) downloads.get("classifiers");
                    if (classifiers.containsKey(windowsArch)) {
                        JSONObject nativesWindows = (JSONObject) classifiers.get(windowsArch);
                        if (nativesWindows.containsKey("size")) {
                            long size = (long) nativesWindows.get("size");
                            totalLibrariesSize += size;
                        }
                    }
                }
            }
        }

        for (Object o : libraries) {
            JSONObject library = (JSONObject) o;

            if (library.containsKey("rules")) {
                JSONArray rules = (JSONArray) library.get("rules");

                for (Object obj: rules) {
                    JSONObject object = (JSONObject) obj;

                    if (object.containsKey("action")) {
                        String action = (String) object.get("action");

                        if (action.equals("allow")) {
                            if(object.containsKey("os")) {
                                JSONObject os = (JSONObject) object.get("os");
                                String name = (String) os.get("name");

                                if(name.equals("windows")) {
                                    downloadLibrary(library);
                                }
                            } else {
                                downloadLibrary(library);
                            }
                        }
                    }
                }
            } else {
                downloadLibrary(library);
            }

            if (library.containsKey("natives")) {
                downloadNative(library);
            }

        }

        SwingUtilities.invokeLater(() -> {
            progressBar.setString("Successfully downloaded version: " + Utils.downloadedVersion);
            progressBar.setValue(0);
        });
        totalBytesRead = 0;
        System.out.println("Successfully downloaded version: " + Utils.downloadedVersion);

        downloadLog4j();

        GameThread gameThread = new GameThread();
        gameThread.setupCommand(version);
    }

    private void downloadNative(JSONObject library) {
        JSONObject natives = (JSONObject) library.get("natives");

        if (natives.containsKey("windows")) {
            String windows = (String) natives.get("windows");
            String windowsArch = windows.replace("${arch}", Utils.getSystemArch());
            JSONObject downloads = (JSONObject) library.get("downloads");
            JSONObject classifiers = (JSONObject) downloads.get("classifiers");

            if (classifiers.containsKey(windowsArch)) {
                JSONObject nativesWindows = (JSONObject) classifiers.get(windowsArch);
                String name = (String) library.get("name");
                long size = (long) nativesWindows.get("size");

                String url = (String) nativesWindows.get("url");

                if (nativesWindows.containsKey("path")) {
                    String path = (String) nativesWindows.get("path");

                    String formPath = path.replace("/", "\\");
                    String finalPath = Utils.getWorkingDirectory() + "\\.minecraft\\libraries\\" + formPath;

                    downloadNativeObject(finalPath, url, name, size);
                } else {
                    String extractedPath = url.replace("https://libraries.minecraft.net/", "");
                    String finalPath = Utils.getWorkingDirectory() + "\\.minecraft\\libraries\\"
                            + extractedPath.replace("/", "\\");

                    downloadNativeObject(finalPath, url, name, size);
                }
            }
        }
    }

    private void downloadNativeObject(String finalPath, String url, String name, long size) {
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

            System.out.println("Downloading: " + name + "-natives-windows " + sizeMBFormat + "mb");

            totalBytesRead += size;

            int progress = (int) ((totalBytesRead * 100) / totalLibrariesSize);

            double totalBytesReadMB = bytesConverter(totalBytesRead);
            String totalBytesReadMBFormatted = decimalFormat.format(totalBytesReadMB);
            String totalLibrariesSizeF = decimalFormat.format(bytesConverter(totalLibrariesSize));

            SwingUtilities.invokeLater(() -> {
                progressBar.setString("Downloading: " + name + "-natives-windows " + sizeMBFormat + "mb " +
                        totalBytesReadMBFormatted + "mb" + "/" + totalLibrariesSizeF + "mb " + decimalFormat.format(downloadSpeedMbps) + " Mbps"); // Update text
            });

            try (InputStream inputStream = new URL(url).openStream();
                 FileOutputStream outputStream = new FileOutputStream(finalPath)) {
                long startTime = System.currentTimeMillis();
                IOUtils.copy(inputStream, outputStream);
                long endTime = System.currentTimeMillis();

                double elapsedTimeInSeconds = (endTime - startTime) / 1000.0; // Seconds
                downloadSpeedMbps = (size / 1024.0 / 1024.0) / elapsedTimeInSeconds;

                System.out.println(name + "-natives-windows" + " downloaded successfully");
                SwingUtilities.invokeLater(() -> {
                    progressBar.setValue(progress);
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //extracting natives to natives path
        try (JarFile jarFile = new JarFile(finalPath)) {
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry jarEntry = entries.nextElement();
                Path entryPath = Paths.get(Utils.natives_directory, jarEntry.getName());

                if (jarEntry.isDirectory()) {
                    Files.createDirectories(entryPath);
                } else {
                    try (InputStream inputStream = jarFile.getInputStream(jarEntry)) {
                        if (Files.notExists(entryPath)) {
                            Files.copy(inputStream, entryPath, StandardCopyOption.REPLACE_EXISTING);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void downloadLibrary(JSONObject library) {
        try {
            JSONObject downloads = (JSONObject) library.get("downloads");
            String name = (String) library.get("name");

            if (downloads.containsKey("artifact")) {
                JSONObject artifact = (JSONObject) downloads.get("artifact");

                String url = (String) artifact.get("url");
                long size = (long) artifact.get("size");

                if (artifact.containsKey("path")) {
                    String path = (String) artifact.get("path");

                    String formPath = path.replace("/", "\\");
                    String finalPath = Utils.getWorkingDirectory() + "\\.minecraft\\libraries\\" + formPath;

                    downloadObject(finalPath, url, name, size);

                } else {
                    String extractedPath = url.replace("https://libraries.minecraft.net/", "");

                    String finalPath = Utils.getWorkingDirectory() + "\\.minecraft\\libraries\\" +
                            extractedPath.replace("/", "\\");
                    downloadObject(finalPath, url, name, size);
                }
            }

        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void downloadObject(String finalPath, String url, String name, long size) {

        if (!Utils.classPaths.contains(finalPath)) {
            Utils.classPaths.add(finalPath);
        }

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

            int progress = (int) ((totalBytesRead * 100) / totalLibrariesSize);

            double totalBytesReadMB = bytesConverter(totalBytesRead);
            String totalBytesReadMBFormatted = decimalFormat.format(totalBytesReadMB);
            String totalLibrariesSizeF = decimalFormat.format(bytesConverter(totalLibrariesSize));

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

    private void downloadLog4j() {
        try {
            if (version.containsKey("logging")) {
                JSONObject logging = (JSONObject) version.get("logging");
                JSONObject client = (JSONObject) logging.get("client");
                JSONObject file = (JSONObject) client.get("file");

                String url = (String) file.get("url");
                String id = (String) file.get("id");
                String argument = (String) client.get("argument");

                JSONObject javaVersion = (JSONObject) version.get("javaVersion");
                long majorVersion = (long) javaVersion.get("majorVersion");

                //if (majorVersion <= 8) {
                    //id = "log4j2-1.12.xml";
                //}

                String finalPath = Utils.getWorkingDirectory() + "\\.minecraft\\assets\\log_configs\\" + id;

                Utils.Log4jArgument = argument.replace("${path}", ArgumentsGen.surroundWithQuotes(finalPath));

                Path directoryPath = Paths.get(finalPath).getParent();
                if (directoryPath != null) {
                    try {
                        Files.createDirectories(directoryPath);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }

                try (InputStream inputStream = new URL(url).openStream();
                     FileOutputStream outputStream = new FileOutputStream(finalPath)) {
                    System.out.println("Downloading: " + id);
                    IOUtils.copy(inputStream, outputStream);
                    System.out.println("Successfully downloaded: " + id);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
