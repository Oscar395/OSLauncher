package com.example;

import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

public class Utils {

    static Preferences userPrefs = Preferences.userNodeForPackage(Utils.class);

    public static void saveUserPrefs() {
        // Save user preferences
        userPrefs.put("username", auth_player_name);
        userPrefs.put("downloadedVersion", downloadedVersion);
    }
    public static String VersionType = "release";

    public static String downloadedVersion = userPrefs.get("downloadedVersion", null);

    public static boolean isForgeVersion = false;
    public static boolean isOptifineVersion = false;
    public static JSONObject optifineVersion;
    public static JSONObject forgeVersion;

    public static String getWorkingDirectory() {
        return System.getenv("APPDATA");
    }

    //JVM arguments
    public static List<String> classPaths = new ArrayList<>();
    public static String natives_directory;
    public static String launcher_name = "minecraft-launcher";
    public static String launcher_version = "2.7.12";
    public static String primary_jar;
    public static String library_directory = getWorkingDirectory() + "\\.minecraft\\libraries";
    public static String game_directory = getWorkingDirectory() + "\\.minecraft";

    //Game arguments
    public static String auth_player_name = userPrefs.get("username", "Player");
    public static String version_name;
    public static String assets_root = getWorkingDirectory() + "\\.minecraft\\assets";
    public static String assets_index_name;
    public static String user_type = "mojang";
    public static String Log4jArgument = "";
    public static String mainClass;

    public static void resetUtils() {
        downloadedVersion = "";
        isForgeVersion = false;
        forgeVersion = null;
        classPaths.clear();
        natives_directory = null;
        primary_jar = null;
        version_name = null;
        assets_index_name = null;
        Log4jArgument = "";
        mainClass = null;
    }
}
