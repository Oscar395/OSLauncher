package com.example;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.StringJoiner;
import java.util.UUID;

public class ArgumentsGen {

    JSONObject version;
    public ArgumentsGen (JSONObject versionJson) {
        this.version = versionJson;
    }

    public String genWholeCommand() {
        if (Utils.isForgeVersion) {
            return genJreArguments() + genJVMArguments() + " -cp " + getclassPaths() + " " + genForgeJVMArguments()
                    + genExtraArguments() + genGameArguments() + " " + genForgeGameArguments();
        } else if (Utils.isOptifineVersion){
            return genJreArguments() + genJVMArguments() + " -cp " + getclassPaths() + genExtraArguments() + genGameArguments() + genOptifineGameArgs();
        } else {
            return genJreArguments() + genJVMArguments() + " -cp " + getclassPaths() + genExtraArguments() + genGameArguments();
        }
    }

    private String genOptifineGameArgs() {
        JSONObject gameArgs = Utils.optifineVersion;
        JSONObject arguments = (JSONObject) gameArgs.get("arguments");

        StringJoiner optiGameArgs = new StringJoiner(" ");

        JSONArray game = (JSONArray) arguments.get("game");
        for (Object o: game) {
            String element = (String) o;

            optiGameArgs.add(element);
        }

        return " " + optiGameArgs.toString();
    }

    private String genForgeJVMArguments() {
        JSONObject forgeVersion = Utils.forgeVersion;

        StringJoiner forgeJVMArguments = new StringJoiner(" ");

        if (forgeVersion.containsKey("arguments")) {
            JSONObject arguments = (JSONObject) forgeVersion.get("arguments");
            JSONArray jvm = (JSONArray) arguments.get("jvm");

            for (Object o: jvm) {
                String value = (String) o;

                String fixedValue = value.replace("${version_name}", Utils.downloadedVersion)
                        .replace("${library_directory}", Utils.library_directory)
                        .replace("${classpath_separator}", ";");
                forgeJVMArguments.add(fixedValue);
            }
        }

        return forgeJVMArguments.toString() + " ";
    }

    private String genForgeGameArguments() {
        JSONObject forgeVersion = Utils.forgeVersion;

        StringJoiner gameArgs = new StringJoiner(" ");

        if (forgeVersion.containsKey("arguments")) {
            JSONObject arguments = (JSONObject) forgeVersion.get("arguments");
            JSONArray game = (JSONArray) arguments.get("game");

            for (Object o: game) {
                String args = (String) o;
                gameArgs.add(args);
            }
        }

        return gameArgs.toString();
    }

    private String genJreArguments() {
        if (version.containsKey("javaVersion")) {
            JSONObject javaVersion = (JSONObject) version.get("javaVersion");
            long majorVersion = (long) javaVersion.get("majorVersion");

            if (majorVersion <= 8) {
                return Utils.LegacyPath + " ";
            } else {
                return Utils.GammaPath + " ";
            }
        } else {
            return Utils.LegacyPath + " ";
        }
    }

    public String genJVMArguments() {
        StringJoiner jvmArguments = new StringJoiner(" ");

        if (version.containsKey("arguments")){
            JSONObject arguments = (JSONObject) version.get("arguments");
            JSONArray jvm = (JSONArray) arguments.get("jvm");

            for (Object o: jvm) {

                if (!(o instanceof String)) {
                    JSONObject obj = (JSONObject) o;

                    if(obj.containsKey("rules")) {
                        JSONArray rules = (JSONArray) obj.get("rules");
                        //JSONObject obj0 = (JSONObject) rules.get(0);

                        for (Object obj2: rules) {

                            JSONObject obj0 = (JSONObject) obj2;

                            if(obj0.containsKey("os")) {
                                JSONObject os = (JSONObject) obj0.get("os");

                                if (os.containsKey("name")) {
                                    String name = (String) os.get("name");

                                    if (name.equals("windows")) {
                                        Object value =  obj.get("value");
                                        if (value instanceof JSONArray) {
                                            JSONArray valueArray = (JSONArray) value;
                                            for (Object object: valueArray) {
                                                String argument = (String) object;
                                                if (argument.equals("-Dos.name=Windows 10")) {
                                                    String formattedArg = "\"-Dos.name=Windows 10\"";
                                                    jvmArguments.add(formattedArg);
                                                } else {
                                                    jvmArguments.add(argument);
                                                }
                                            }
                                        } else {
                                            String valueStr = (String) value;
                                            jvmArguments.add(valueStr);
                                        }
                                    }
                                } else if (os.containsKey("arch")) {
                                    String value = (String) obj.get("value");
                                    jvmArguments.add(value);
                                }
                            }
                        }
                    }
                } else {
                    String obj = (String) o;

                    if (obj.equals("-Djava.library.path=${natives_directory}")) {
                        String libraryPath = "-Djava.library.path=" + Utils.natives_directory;
                        //String libraryPathF = libraryPath.replace("${natives_directory}", Utils.natives_directory);
                        jvmArguments.add(libraryPath);
                    }
                    if (obj.equals("-Djna.tmpdir=${natives_directory}")){
                        String tmpdir = "-Djna.tmpdir=" + Utils.natives_directory;
                        //String tmpdirF = tmpdir.replace("${natives_directory}", Utils.natives_directory);
                        jvmArguments.add(tmpdir);
                    }

                    if (obj.equals("-Dorg.lwjgl.system.SharedLibraryExtractPath=${natives_directory}")) {
                        String lwjgl = "-Dorg.lwjgl.system.SharedLibraryExtractPath=" + Utils.natives_directory;
                        //String lwjglF = lwjgl.replace("${natives_directory}", Utils.natives_directory);
                        jvmArguments.add(lwjgl);
                    }

                    if (obj.equals("-Dio.netty.native.workdir=${natives_directory}")) {
                        String nettyNative = "-Dio.netty.native.workdir=" + Utils.natives_directory;
                        //String nettyNativeF = nettyNative.replace("${natives_directory}", Utils.natives_directory);
                        jvmArguments.add(nettyNative);
                    }

                    if (obj.equals("-Dminecraft.launcher.brand=${launcher_name}")) {
                        String launcher_name = "-Dminecraft.launcher.brand=" + Utils.launcher_name;
                        jvmArguments.add(launcher_name);
                    }
                    if (obj.equals("-Dminecraft.launcher.version=${launcher_version}")){
                        String launcher_version = "-Dminecraft.launcher.version=" + Utils.launcher_version;
                        jvmArguments.add(launcher_version);
                    }
                }

            }

            return jvmArguments.toString();
        } else {

            String libraryPath = "-Djava.library.path=" + Utils.natives_directory;
            String launcher_name = "-Dminecraft.launcher.brand=" + Utils.launcher_name;
            String launcher_version = "-Dminecraft.launcher.version=2.8.2";
            String mcClient = "-Dminecraft.client.jar=" + Utils.primary_jar;

            jvmArguments.add("\"-Dos.name=Windows 10\"").add("-Dos.version=10.0")
                    .add("-XX:HeapDumpPath=MojangTricksIntelDriversForPerformance_javaw.exe_minecraft.exe.heapdump")
                    .add(libraryPath).add(launcher_name).add(launcher_version).add(mcClient);

            return jvmArguments.toString();
        }
    }

    public String getclassPaths() {
        StringJoiner classPaths = new StringJoiner(";");
        for (String s: Utils.classPaths) {
            classPaths.add(s);
        }
        return classPaths.toString() + ";" + Utils.primary_jar;
    }

    public String genExtraArguments() {
        StringJoiner extraArguments = new StringJoiner(" ");
        //String GBs = " -Xmx2G";
        //String experimentalOptions = "-XX:+UnlockExperimentalVMOptions";
        //String useG1GC = "-XX:+UseG1GC";
        //String sizePercent = "-XX:G1NewSizePercent=20";
        //String reservePercent = "-XX:G1ReservePercent=20";
        //String maxGCPauseMillis = "-XX:MaxGCPauseMillis=50";
        //String heapRegionSize = "-XX:G1HeapRegionSize=32M";
        String javaAgentArgs = Utils.javaAgentArgs;
        String jvmArgs = Utils.jvmArguments;
        String log4jArgument = Utils.Log4jArgument;
        String mainClass = Utils.mainClass;

        extraArguments.add(javaAgentArgs)
                .add(jvmArgs)
                .add(log4jArgument)
                .add(mainClass);

        return extraArguments.toString();
    }

    public String genGameArguments() {
        StringJoiner gameArguments = new StringJoiner(" ");

        if (version.containsKey("arguments")) {
            JSONObject arguments = (JSONObject) version.get("arguments");
            JSONArray game = (JSONArray) arguments.get("game");

            for (Object o: game) {

                if(o instanceof String) {
                    String jsonObject = (String) o;

                    if(jsonObject.equals("--username")) {
                        String username = " --username " + Utils.auth_player_name;
                        gameArguments.add(username);
                    }

                    if (jsonObject.equals("--version")) {
                        String version = "--version " + Utils.downloadedVersion;
                        gameArguments.add(version);
                    }

                    if (jsonObject.equals("--gameDir")) {
                        String gameDir = "--gameDir " + Utils.game_directory;
                        gameArguments.add(gameDir);
                    }

                    if (jsonObject.equals("--assetsDir")) {
                        String assetsDir = "--assetsDir " + Utils.assets_root;
                        gameArguments.add(assetsDir);
                    }

                    if (jsonObject.equals("--assetIndex")) {
                        String assetsIndex = "--assetIndex " + Utils.assets_index_name;
                        gameArguments.add(assetsIndex);
                    }

                    if (jsonObject.equals("--uuid")) {
                        String uuidF = "--uuid " + Utils.playerUUID;
                        gameArguments.add(uuidF);
                    }

                    if (jsonObject.equals("--accessToken")) {
                        String accessToken = "--accessToken " + Utils.clientToken;
                        gameArguments.add(accessToken);
                    }

                    if (jsonObject.equals("--clientId")) {
                        String clientId = "--clientId NzM3ZmFiNzYtNDY4Mi00OWVjLWI4OTctODZkNGY2NWJlYTcy";
                        gameArguments.add(clientId);
                    }

                    if(jsonObject.equals("--userType")) {
                        String userType = "--userType " + Utils.user_type;
                        gameArguments.add(userType);
                    }

                    if (jsonObject.equals("--versionType")) {
                        String versionType = "--versionType " + version.get("type");
                        gameArguments.add(versionType);
                    }

                } else {
                    JSONObject jsonObject = (JSONObject) o;
                    Object obj = jsonObject.get("value");
                    if (obj instanceof JSONArray) {
                        JSONArray valuesArray = (JSONArray) obj;
                        for (Object objs: valuesArray) {
                            String value = (String) objs;
                            if (value.equals("--width")) {
                                gameArguments.add("--width " + Utils.resolutionX);
                            }
                            if (value.equals("--height")) {
                                gameArguments.add("--height " + Utils.resolutionY);
                            }
                        }
                    }
                }
            }
            return gameArguments.toString();
        } else {
            UUID uuid = UUID.randomUUID();
            String uuidString = uuid.toString().replace("-", "");

            String minecraftArguments = (String) version.get("minecraftArguments");

            JSONObject assetIndex = (JSONObject) version.get("assetIndex");
            String id = (String) assetIndex.get("id");
            String assetsPath = "";

            if (id.equals("pre-1.6")) {
                assetsPath = Utils.getWorkingDirectory() + "\\.minecraft\\resources";
            } else {
                assetsPath = Utils.getWorkingDirectory() + "\\.minecraft\\assets\\virtual\\legacy";
            }

            if (Utils.isForgeVersion) {
                JSONObject forgeVersion = Utils.forgeVersion;
                minecraftArguments = (String) forgeVersion.get("minecraftArguments");
            }
            return " " + minecraftArguments.replace("${auth_player_name}", Utils.auth_player_name)
                    .replace("${version_name}", Utils.downloadedVersion).replace("${game_directory}", Utils.game_directory)
                    .replace("${assets_root}", Utils.assets_root).replace("${assets_index_name}", Utils.assets_index_name)
                    .replace("${auth_uuid}", Utils.playerUUID).replace("${auth_access_token}", "c01431e9c85e8e141730c2e2015ee64420243e9089591a0b6ab964b961aa1bbf")
                    .replace("${user_type}", "mojang").replace("${user_properties}", "{}")
                    .replace("${version_type}", Utils.VersionType).replace("${auth_session}", "416f2980fb2f6a605a56e71b5917986b55239a1647868d22fe15def670d3491e")
                    .replace("${game_assets}", assetsPath);
        }
    }
}
