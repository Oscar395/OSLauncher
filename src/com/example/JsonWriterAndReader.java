package com.example;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

public class JsonWriterAndReader {

    private JSONObject versionsList;
    private JSONArray fabricVersions;
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
                Utils.downloadedVersion = (String) Versionjson.get("id");
                Utils.version_name = (String) Versionjson.get("id");
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
                        comboBox.addItem("Fabric-" + version);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
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
