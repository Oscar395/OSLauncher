package com.example;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.DecimalFormat;
import java.util.*;

public class JsonWriterAndReader {

    private JSONObject versionsList;
    private JSONArray fabricVersions;
    private JSONArray forgeVersions;
    private JSONArray optifineVersions;
    public List<String> jvmArguments = new ArrayList<>();

    public void readVersionsList(JComboBox versionsBox) {
        try {
            URL url = new URL("https://launchermeta.mojang.com/mc/game/version_manifest.json");
            String json = IOUtils.toString(url, Charset.forName("UTF-8"));

            Object obj = JSONValue.parse(json);
            versionsList = (JSONObject) obj;

            setAllVersions(versionsList, versionsBox);

            JSONObject latest = (JSONObject) versionsList.get("latest");

            String latestRelease = (String) latest.get("release");

            System.out.println("LatestRelease: " + latestRelease);

            //FileWriter versionsList = new FileWriter("versions_list.json");
            //versionsList.write(json);
            //versionsList.flush();

        } catch (IOException e) {
            e.printStackTrace();
            listOfflineVersions(versionsBox);
        }
    }

    private void listOfflineVersions(JComboBox versionsBox) {
        String directoryPath = Utils.getWorkingDirectory() + "\\.minecraft\\versions\\";
        try {
            List<String> primaryFolders = listPrimaryFolders(directoryPath);

            //System.out.println("Primary Folders:");
            for (String folder : primaryFolders) {
                System.out.println(folder);
                versionsBox.addItem(folder);
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public List<String> listPrimaryFolders(String directoryPath) throws IOException {
        List<String> primaryFolders = new ArrayList<>();
        Path path = Paths.get(directoryPath);

        if (Files.exists(path) && Files.isDirectory(path)) {
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                    if (dir.equals(path)) {
                        // Skip the root directory
                        return FileVisitResult.CONTINUE;
                    }
                    primaryFolders.add(path.relativize(dir).toString());
                    return FileVisitResult.SKIP_SUBTREE; // Skip contents of this directory
                }
            });
        } else {
            System.out.println("Directory does not exist or is not a directory.");
        }

        return primaryFolders;
    }

    public void updateVersionsList(JComboBox versionsBox) {
        try {
            setAllVersions(versionsList, versionsBox);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void downloadSelectedVersion(JComboBox versionsBox, JProgressBar progressBar) {
        progressBar.setSize(890, 20);
        progressBar.setLocation(0, 445);
        String selectedVersion = versionsBox.getSelectedItem().toString();
        try {
            JSONArray versionsArray = (JSONArray) versionsList.get("versions");
            for (Object o : versionsArray) {
                JSONObject version = (JSONObject) o;
                if (version.get("id").equals(selectedVersion)) {
                    try {
                        String versionsPath = Utils.getWorkingDirectory() + "\\.minecraft\\versions\\" +
                                version.get("id");
                        final Path dir = Paths.get(versionsPath);
                        Files.createDirectories(dir);

                        String path = versionsPath + "\\" + version.get("id") + ".json";
                        if(Files.notExists(Paths.get(path))) {
                            InputStream in = new URL((String) version.get("url")).openStream();
                            Files.copy(in , Paths.get(path), StandardCopyOption.REPLACE_EXISTING);
                        }

                        JSONParser parser = new JSONParser();

                        FileReader reader = new FileReader(path);
                        Object obj = parser.parse(reader);
                        JSONObject Vjson = (JSONObject) obj;

                        Utils.downloadedVersion = (String) version.get("id");
                        Utils.version_name = (String) version.get("id");
                        Utils.auth_player_name = WindowManager.Instance.playerNameField.getText();
                        Utils.saveUserPrefs();

                        downloadAssets(Vjson, progressBar);

                        return;

                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                }
            }

            trySelectedVersion(selectedVersion, progressBar);

        } catch (Exception e) {
            e.printStackTrace();
            trySelectedVersion(selectedVersion, progressBar);
        }
    }

    private void trySelectedVersion(String selectedVersion, JProgressBar progressBar) {
        Runnable runnable = () -> {
            String path = Utils.getWorkingDirectory() + "\\.minecraft\\versions\\" + selectedVersion + "\\" + selectedVersion + ".json";
            JSONParser parser = new JSONParser();

            try {

                JSONObject Versionjson = null;
                if (Files.exists(Paths.get(path))) {
                    FileReader reader = new FileReader(path);
                    Object obj = parser.parse(reader);
                    Versionjson = (JSONObject) obj;
                } else {
                    if (selectedVersion.contains("Fabric")) {
                        String version = selectedVersion.replace("Fabric-", "");
                        String fabricJsonUrl = "https://meta.fabricmc.net/v2/versions/loader/" + version + "/0.14.22/profile/json";

                        try {
                            String versionPath = Utils.getWorkingDirectory() + "\\.minecraft\\versions\\" + selectedVersion;
                            Files.createDirectories(Paths.get(versionPath));

                            String path1 = versionPath + "\\" + selectedVersion + ".json";
                            InputStream in = new URL(fabricJsonUrl).openStream();
                            Files.copy(in , Paths.get(path1), StandardCopyOption.REPLACE_EXISTING);

                            FileReader reader2 = new FileReader(path1);
                            Object object = parser.parse(reader2);
                            Versionjson = (JSONObject) object;

                            System.out.println("fabric version json downloaded successfully");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (selectedVersion.contains("forge")) {
                        String forgeInstallerUrl = "https://oscar395.github.io/oslauncher-repository/forge-installers/" + selectedVersion + ".jar";

                        try {
                            String installerPath = Utils.getWorkingDirectory() + "\\.minecraft\\forge-installers\\" + selectedVersion + ".jar";
                            final Path path2 = Paths.get(installerPath);
                            Files.createDirectories(path2.getParent());

                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setSize(890, 20);
                                    progressBar.setLocation(0, 445);
                                    progressBar.setString("Downloading forge installer...");
                                }
                            });

                            InputStream installerIn = new URL(forgeInstallerUrl).openStream();
                            Files.copy(installerIn, path2, StandardCopyOption.REPLACE_EXISTING);

                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setString("Installing forge..");
                                }
                            });

                            ProcessBuilder processBuilder = new ProcessBuilder("java" ,"-jar", installerPath);

                            Process process = processBuilder.start();
                            System.out.println("Installing forge...");

                            StringBuilder output = new StringBuilder();

                            BufferedReader reader = new BufferedReader(
                                    new InputStreamReader(process.getInputStream()));
                            String line;
                            while ((line = reader.readLine()) != null) {
                                output.append(line + "\n");
                                System.out.println(line);
                            }

                            int exitVal = process.waitFor();
                            if(exitVal == 0) {
                                System.out.println("| forge installed successfully |");

                                String versionPath = Utils.getWorkingDirectory() + "\\.minecraft\\versions\\" + selectedVersion;
                                Files.createDirectories(Paths.get(versionPath));

                                String jsonPath = versionPath + "\\" + selectedVersion + ".json";
                                //InputStream in = new URL(forgeJsonUrl).openStream();
                                //Files.copy(in, Paths.get(jsonPath), StandardCopyOption.REPLACE_EXISTING);

                                FileReader jsonReader = new FileReader(jsonPath);
                                Object objeto = parser.parse(jsonReader);
                                Versionjson = (JSONObject) objeto;
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (selectedVersion.contains("OptiFine")) {
                        String optifineInstallerUrl = "https://oscar395.github.io/oslauncher-repository/optifine/" + selectedVersion + ".jar";

                        try {
                            String installerPath = Utils.getWorkingDirectory() + "\\.minecraft\\optifine\\" + selectedVersion + ".jar";
                            final Path path2 = Paths.get(installerPath);
                            Files.createDirectories(path2.getParent());

                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setSize(890, 20);
                                    progressBar.setLocation(0, 445);
                                    progressBar.setString("Downloading OptiFine...");
                                }
                            });

                            int index1 = selectedVersion.indexOf("_") + 1;
                            String split1 = selectedVersion.substring(index1);
                            int split2 = split1.indexOf("_");
                            String version = split1.substring(0, split2);
                            String finalString = split1.substring(split2);
                            String finalString2 = version + "-OptiFine" + finalString;

                            String versionPathcheck = Utils.getWorkingDirectory() + "\\.minecraft\\versions\\" +
                                    finalString2 + "\\" + finalString2 + ".json";

                            String vanillaVersion = Utils.getWorkingDirectory() + "\\.minecraft\\versions\\" +
                                    version + "\\" + version + ".jar";

                            if (Files.notExists(Paths.get(vanillaVersion))) {
                                JSONArray versionsArray = (JSONArray) versionsList.get("versions");
                                for (Object o : versionsArray) {
                                    JSONObject version2 = (JSONObject) o;
                                    if (version2.get("id").equals(version)) {
                                        try {
                                            String versionsPath = Utils.getWorkingDirectory() + "\\.minecraft\\versions\\" +
                                                    version2.get("id");
                                            final Path dir = Paths.get(versionsPath);
                                            Files.createDirectories(dir);

                                            String path3 = versionsPath + "\\" + version2.get("id") + ".json";
                                            if(Files.notExists(Paths.get(path3))) {
                                                InputStream in = new URL((String) version2.get("url")).openStream();
                                                Files.copy(in , Paths.get(path3), StandardCopyOption.REPLACE_EXISTING);
                                            }

                                            JSONParser parser2 = new JSONParser();

                                            FileReader reader = new FileReader(path3);
                                            Object obj = parser2.parse(reader);
                                            JSONObject Vjson = (JSONObject) obj;

                                            downloadJarVersion(progressBar, Vjson, version);

                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        } catch (ParseException e) {
                                            throw new RuntimeException(e);
                                        }
                                        break;
                                    }
                                }
                            }

                            if (Files.notExists(Paths.get(installerPath))) {
                                InputStream installerIn = new URL(optifineInstallerUrl).openStream();
                                Files.copy(installerIn, path2, StandardCopyOption.REPLACE_EXISTING);

                                SwingUtilities.invokeLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressBar.setString("Installing OptFine..");
                                    }
                                });

                                ProcessBuilder processBuilder = new ProcessBuilder("java" ,"-jar", installerPath);

                                Process process = processBuilder.start();
                                System.out.println("Installing OptiFine...");

                                StringBuilder output = new StringBuilder();

                                BufferedReader reader = new BufferedReader(
                                        new InputStreamReader(process.getInputStream()));
                                String line;
                                while ((line = reader.readLine()) != null) {
                                    output.append(line + "\n");
                                    System.out.println(line);
                                }

                                int exitVal = process.waitFor();
                                if(exitVal == 0) {
                                    System.out.println("| OptiFine installed successfully |");

                                    String versionPath = Utils.getWorkingDirectory() + "\\.minecraft\\versions\\" + finalString2;
                                    Files.createDirectories(Paths.get(versionPath));

                                    String jsonPath = versionPath + "\\" + finalString2 + ".json";
                                    //InputStream in = new URL(forgeJsonUrl).openStream();
                                    //Files.copy(in, Paths.get(jsonPath), StandardCopyOption.REPLACE_EXISTING);

                                    FileReader jsonReader = new FileReader(jsonPath);
                                    Object objeto = parser.parse(jsonReader);
                                    Versionjson = (JSONObject) objeto;
                                }
                            } else if (Files.notExists(Paths.get(versionPathcheck))) {
                                ProcessBuilder processBuilder = new ProcessBuilder("java" ,"-jar", installerPath);

                                Process process = processBuilder.start();
                                System.out.println("Installing OptiFine...");

                                StringBuilder output = new StringBuilder();

                                BufferedReader reader = new BufferedReader(
                                        new InputStreamReader(process.getInputStream()));
                                String line;
                                while ((line = reader.readLine()) != null) {
                                    output.append(line + "\n");
                                    System.out.println(line);
                                }

                                int exitVal = process.waitFor();
                                if(exitVal == 0) {
                                    System.out.println("| OptiFine installed successfully |");

                                    String versionPath = Utils.getWorkingDirectory() + "\\.minecraft\\versions\\" + finalString2;
                                    Files.createDirectories(Paths.get(versionPath));

                                    String jsonPath = versionPath + "\\" + finalString2 + ".json";
                                    //InputStream in = new URL(forgeJsonUrl).openStream();
                                    //Files.copy(in, Paths.get(jsonPath), StandardCopyOption.REPLACE_EXISTING);

                                    FileReader jsonReader = new FileReader(jsonPath);
                                    Object objeto = parser.parse(jsonReader);
                                    Versionjson = (JSONObject) objeto;
                                }
                            } else {
                                String versionPath = Utils.getWorkingDirectory() + "\\.minecraft\\versions\\" + finalString2;
                                Files.createDirectories(Paths.get(versionPath));

                                String jsonPath = versionPath + "\\" + finalString2 + ".json";
                                //InputStream in = new URL(forgeJsonUrl).openStream();
                                //Files.copy(in, Paths.get(jsonPath), StandardCopyOption.REPLACE_EXISTING);

                                FileReader jsonReader = new FileReader(jsonPath);
                                Object objeto = parser.parse(jsonReader);
                                Versionjson = (JSONObject) objeto;
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                if (selectedVersion.contains("OptiFine")) {
                    String inheritsFrom = (String) Versionjson.get("inheritsFrom");
                    String inheritsPath = Utils.getWorkingDirectory() + "\\.minecraft\\versions\\" + inheritsFrom +
                            "\\" + inheritsFrom + ".json";

                    JSONArray libraries = (JSONArray) Versionjson.get("libraries");

                    for (Object object: libraries) {
                        JSONObject library = (JSONObject) object;
                        String name = (String) library.get("name");

                        String optifinePath = name.replace(":", "\\");
                        String jarFile = name.replace("optifine:", "").replace(":", "-");
                        String fnPath = Utils.getWorkingDirectory() + "\\.minecraft\\libraries\\" + optifinePath + "\\"
                                + jarFile + ".jar";
                        Utils.classPaths.add(fnPath);
                    }

                    Utils.isOptifineVersion = true;
                    Utils.optifineVersion = Versionjson;
                    Utils.downloadedVersion = (String) Versionjson.get("id");
                    Utils.version_name = (String) Versionjson.get("id");
                    Utils.auth_player_name = WindowManager.Instance.playerNameField.getText();
                    Utils.saveUserPrefs();

                    JSONObject inheritsVersion = null;
                    if (Files.exists(Paths.get(inheritsPath))) {
                        FileReader reader2 = new FileReader(inheritsPath);
                        Object object = parser.parse(reader2);
                        inheritsVersion = (JSONObject) object;
                    } else {
                        inheritsVersion = downloadInheritsVersion(inheritsFrom);
                    }

                    //Downloads Vanilla assets
                    downloadAssets(inheritsVersion, progressBar);

                    return;
                }

                if (selectedVersion.contains("fabric") || selectedVersion.contains("Fabric")) {
                    String inheritsFrom = (String) Versionjson.get("inheritsFrom");
                    String inheritsPath = Utils.getWorkingDirectory() + "\\.minecraft\\versions\\" + inheritsFrom +
                            "\\" + inheritsFrom + ".json";

                    FabricDownloader fabricDownloader = new FabricDownloader();
                    fabricDownloader.downloadLibraries(Versionjson, progressBar);

                    Utils.isFabricVersion = true;
                    Utils.fabricVersion = Versionjson;
                    Utils.downloadedVersion = selectedVersion;
                    Utils.version_name = selectedVersion;
                    Utils.auth_player_name = WindowManager.Instance.playerNameField.getText();
                    Utils.saveUserPrefs();

                    JSONObject inheritsVersion = null;
                    if (Files.exists(Paths.get(inheritsPath))) {
                        FileReader reader2 = new FileReader(inheritsPath);
                        Object object = parser.parse(reader2);
                        inheritsVersion = (JSONObject) object;
                    } else {
                        inheritsVersion = downloadInheritsVersion(inheritsFrom);
                    }

                    //Downloads Vanilla assets
                    downloadAssets(inheritsVersion, progressBar);

                    return;
                }

                //forge
                if (Versionjson.containsKey("inheritsFrom")) {
                    String inheritsFrom = (String) Versionjson.get("inheritsFrom");
                    String inheritsPath = Utils.getWorkingDirectory() + "\\.minecraft\\versions\\" + inheritsFrom +
                            "\\" + inheritsFrom + ".json";

                    ForgeDownloader forgeDownloader = new ForgeDownloader();
                    //Downloads the forge libraries
                    forgeDownloader.downloadLibraries(Versionjson, progressBar);

                    //Adds the forge version JSONObject to a JSON to be used later on
                    Utils.isForgeVersion = true;
                    Utils.forgeVersion = Versionjson;
                    Utils.downloadedVersion = selectedVersion;
                    Utils.version_name = selectedVersion;
                    Utils.auth_player_name = WindowManager.Instance.playerNameField.getText();
                    Utils.saveUserPrefs();

                    JSONObject inheritsVersion = null;
                    if(Files.exists(Paths.get(inheritsPath))) {
                        FileReader reader2 = new FileReader(inheritsPath);
                        Object object = parser.parse(reader2);
                        inheritsVersion = (JSONObject) object;
                    } else {
                        inheritsVersion = downloadInheritsVersion(inheritsFrom);
                    }
                    //Downloads Vanilla assets
                    if (inheritsVersion != null) {
                        downloadAssets(inheritsVersion, progressBar);
                    }
                } else {
                    Utils.downloadedVersion = (String) Versionjson.get("id");
                    Utils.version_name = (String) Versionjson.get("id");
                    Utils.auth_player_name = WindowManager.Instance.playerNameField.getText();
                    Utils.saveUserPrefs();

                    downloadAssets(Versionjson, progressBar);
                }
            } catch (IOException excep) {
                excep.printStackTrace();
            } catch (ParseException ex) {
                throw new RuntimeException(ex);
            }
        };
        new Thread(runnable).start();
    }

    private void downloadJarVersion(JProgressBar progressBar, JSONObject version, String versionStr) {
        JSONObject versionDownloads = (JSONObject) version.get("downloads");
        JSONObject client = (JSONObject) versionDownloads.get("client");
        JSONObject clientMappings = (JSONObject) versionDownloads.get("client_mappings");
        String clientURL = (String) client.get("url");
        long Size = (long) client.get("size");

        String clientPath = Utils.getWorkingDirectory() + "\\.minecraft\\versions\\" + versionStr +
                "\\" + versionStr + ".jar";

        String clientMappingsPath = Utils.getWorkingDirectory() + "/.minecraft/versions/" + versionStr +
                "/client.txt";

        File file = new File(clientPath);

        if(Files.notExists(Paths.get(clientPath)) || file.length() == 0) {
            System.out.println("Downloading client: " + versionStr);
            int bufferSize = 1024;
            try (InputStream inputStream = new URL(clientURL).openStream();
                 FileOutputStream outputStream = new FileOutputStream(clientPath)) {

                byte[] buffer = new byte[bufferSize];
                int bytesRead;
                long totalBytesRead = 0;


                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                    totalBytesRead += bytesRead;

                    int progress = (int) ((totalBytesRead * 100) / Size);
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setString("Downloading Client " + versionStr + ".jar");
                            progressBar.setValue(progress);
                        }
                    });
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

                    DecimalFormat decimalFormat = new DecimalFormat("#.##"); // Two decimal places
                    int progress = (int) ((totalBytesRead * 100) / Size);
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setString("Downloading client_mappings client.txt");
                            progressBar.setValue(progress);
                        }
                    });
                    long endTime = System.currentTimeMillis();

                    double elapsedTimeInSeconds = (endTime - startTime) / 1000.0; // Seconds
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private JSONObject downloadInheritsVersion(String inheritsFrom) {
        JSONArray versionsArray = (JSONArray) versionsList.get("versions");
        JSONObject returnObject = null;

        for (Object o : versionsArray) {
            JSONObject version = (JSONObject) o;
            if (version.get("id").equals(inheritsFrom)) {
                try {
                    String versionsPath = Utils.getWorkingDirectory() + "\\.minecraft\\versions\\" +
                            version.get("id");
                    final Path dir = Paths.get(versionsPath);
                    Files.createDirectories(dir);

                    String path1 = versionsPath + "\\" + version.get("id") + ".json";
                    if(Files.notExists(Paths.get(path1))) {
                        InputStream in = new URL((String) version.get("url")).openStream();
                        Files.copy(in , Paths.get(path1), StandardCopyOption.REPLACE_EXISTING);
                    }

                    JSONParser parser = new JSONParser();

                    FileReader reader2 = new FileReader(path1);
                    Object object = parser.parse(reader2);
                    returnObject = (JSONObject) object;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        }
        return returnObject;
    }

    private void downloadAssets(JSONObject version, JProgressBar progressBar) throws IOException, ParseException {

        final JSONObject assetIndex = (JSONObject) version.get("assetIndex");
        System.out.println("Downloading: " + assetIndex.get("id") + " " + assetIndex.get("size") + " bytes");
        System.out.println("Total assets size: " + assetIndex.get("totalSize") + " bytes");

        String pathToDownload = Utils.getWorkingDirectory() + "/.minecraft/assets/indexes/"
                + assetIndex.get("id") + ".json";

        Utils.assets_index_name = (String) assetIndex.get("id");

        final Path dir = Paths.get(pathToDownload);
        if(Files.notExists(dir)) {
            Files.createDirectories(dir);
        }

        try (InputStream in = new URL((String) assetIndex.get("url")).openStream();) {
            Files.copy(in, dir, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Downloaded assets Index");
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONParser jsonParser = new JSONParser();
        FileReader reader = new FileReader(pathToDownload);
        Object obj = jsonParser.parse(reader);
        JSONObject assetsList = (JSONObject) obj;

        System.out.println("done");

        JSONObject objects = (JSONObject) assetsList.get("objects");

        Downloader downloader = new Downloader(version ,objects, progressBar);
        downloader.start();
    }

    public void lookForFabricVersions(JComboBox comboBox) {
        comboBox.removeAllItems();
        if (versionsList != null) {
            Runnable r = () -> {
                try {
                    if (fabricVersions == null) {
                        URL gameVersionsURL = new URL("https://meta.fabricmc.net/v2/versions/game");
                        String json = IOUtils.toString(gameVersionsURL, StandardCharsets.UTF_8);

                        Object obj = JSONValue.parse(json);
                        fabricVersions = (JSONArray) obj;
                    }

                    for (Object o: fabricVersions) {
                        JSONObject object = (JSONObject) o;

                        if ((boolean) object.get("stable")) {
                            String version = (String) object.get("version");

                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    comboBox.addItem("Fabric-" + version);
                                }
                            });
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            };
            new Thread(r).start();
        } else {
            updateVersionsList(comboBox);
        }
    }

    public void lookForOptiFineVersions(JComboBox comboBox) {
        comboBox.removeAllItems();
        if (versionsList != null) {
            Runnable r = () -> {
                try {
                    if (optifineVersions == null) {
                        URL optifineVersionsURL = new URL("https://oscar395.github.io/oslauncher-repository/optifine-versions.json");
                        String json = IOUtils.toString(optifineVersionsURL, StandardCharsets.UTF_8);

                        Object obj = JSONValue.parse(json);
                        JSONObject versions = (JSONObject) obj;
                        optifineVersions = (JSONArray) versions.get("versions");
                    }

                    for (Object o : optifineVersions) {
                        JSONObject object = (JSONObject) o;
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                comboBox.addItem(object.get("name"));
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            };
            new Thread(r).start();
        } else {
            updateVersionsList(comboBox);
        }
    }

    public void lookForForgeVersions(JComboBox comboBox) {
        comboBox.removeAllItems();
        if (versionsList != null) {
            Runnable r = () -> {
                try {
                    if (forgeVersions == null) {
                        URL forgeVersionsURL = new URL("https://oscar395.github.io/oslauncher-repository/forge-versions.json");
                        String json = IOUtils.toString(forgeVersionsURL, StandardCharsets.UTF_8);

                        Object obj = JSONValue.parse(json);
                        JSONObject versions = (JSONObject) obj;
                        forgeVersions = (JSONArray) versions.get("versions");
                    }

                    for (Object o: forgeVersions) {
                        JSONObject object = (JSONObject) o;

                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                comboBox.addItem(object.get("name"));
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            };
            new Thread(r).start();
        } else {
            updateVersionsList(comboBox);
        }
    }

    private void setAllVersions(JSONObject versionsList, JComboBox versionsBox) {
        versionsBox.removeAllItems();

        listOfflineVersions(versionsBox);

        JSONArray versions = (JSONArray) versionsList.get("versions");

        for (Object o : versions) {
            JSONObject version = (JSONObject) o;
            if(version.get("type").equals(Utils.VersionType)) {
                versionsBox.addItem(version.get("id"));
            }
        }

        // Find and combine duplicates
        List<String> uniqueItems = new ArrayList<>();
        for (int i = 0; i < versionsBox.getItemCount(); i++) {
            String item = (String) versionsBox.getItemAt(i);
            if (!uniqueItems.contains(item)) {
                uniqueItems.add(item);
            } else {
                versionsBox.removeItem(item); // Remove duplicate
            }
        }

        if (Utils.downloadedVersion != null && uniqueItems.contains(Utils.downloadedVersion)) {
            versionsBox.setSelectedItem(Utils.downloadedVersion);
        }
    }

}
