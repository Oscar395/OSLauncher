package com.example;

import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

public class Utils {

    public static final int WIDTH  = 912;
    public static final int HEIGHT = 612;

    static Preferences userPrefs = Preferences.userNodeForPackage(Utils.class);

    public static int resolutionX = userPrefs.getInt("resolutionX", 854);
    public static int resolutionY = userPrefs.getInt("resolutionY", 480);

    public static String jvmArguments = userPrefs.get("jvmArguments", " -Xmx2G -XX:+UnlockExperimentalVMOptions -XX:+UseG1GC -XX:G1NewSizePercent=20" +
            " -XX:G1ReservePercent=20 -XX:MaxGCPauseMillis=50 -XX:G1HeapRegionSize=32M");

    public static String selectedRam = userPrefs.get("selectedRam", "2GB");

    public static String playerUUID = userPrefs.get("playerUUID", "9cb6a52c55bc456b9513f4cf19cdf9e3");

    public static String accountType = userPrefs.get("accountType", "Local account");

    public static String accountLocalPath = userPrefs.get("accountLocalPath", "");

    public static String localSkinPath = userPrefs.get("localSkinPath", "steve.png");

    public static String javaAgentPath = userPrefs.get("javaAgentPath", "");

    public static String accessToken = userPrefs.get("accessToken", "");

    public static String clientToken = userPrefs.get("clientToken", "");

    public static void saveUserPrefs() {
        // Save user preferences
        userPrefs.put("username", auth_player_name);
        userPrefs.put("downloadedVersion", downloadedVersion);
        userPrefs.put("GammaPath", GammaPath);
        userPrefs.put("LegacyPath", LegacyPath);
        userPrefs.put("jvmArguments", jvmArguments);
        userPrefs.put("selectedRam", selectedRam);
        userPrefs.put("playerUUID", playerUUID);
        userPrefs.put("accountType", accountType);
        userPrefs.put("accountLocalPath", accountLocalPath);
        userPrefs.put("localSkinPath", localSkinPath);
        userPrefs.put("javaAgentPath", javaAgentPath);
        userPrefs.put("accessToken", accessToken);
        userPrefs.put("clientToken", clientToken);
        userPrefs.putInt("resolutionX", resolutionX);
        userPrefs.putInt("resolutionY", resolutionY);
    }

    public static String GammaPath = userPrefs.get("GammaPath", Utils.getWorkingDirectory() + "\\.minecraft\\runtime\\java-runtime-gamma\\bin\\javaw.exe");
    public static String LegacyPath = userPrefs.get("LegacyPath", Utils.getWorkingDirectory() + "\\.minecraft\\runtime\\jre-legacy\\bin\\javaw.exe");

    public static String VersionType = "release";

    public static String javaAgentArgs = "";

    public static String downloadedVersion = userPrefs.get("downloadedVersion", "");

    public static boolean isForgeVersion = false;
    public static boolean isOptifineVersion = false;
    public static boolean isFabricVersion = false;
    public static JSONObject optifineVersion;
    public static JSONObject forgeVersion;
    public static JSONObject fabricVersion;

    public static String getWorkingDirectory() {
        return System.getenv("APPDATA");
    }

    public static String getSystemArch() {
        String architecture = System.getProperty("os.arch");

        String arch = architecture.replaceAll("[^0-9]+", "");

        if (arch.contains("64")) {
            return "64";
        } else if (arch.contains("86")) {
            return "32";
        } else {
            return "64";
        }
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
        isOptifineVersion = false;
        isFabricVersion = false;
        fabricVersion = null;
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
