package com.example;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        copyJavaAgent();
        makeLauncherProfiles();
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    WindowManager windowManager = new WindowManager();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void makeLauncherProfiles() {
        String minecraftPath = Utils.getWorkingDirectory() + "\\.minecraft\\launcher_profiles.json";
        if (Files.notExists(Paths.get(minecraftPath))) {

            String json = "{\n" +
                    "  \"profiles\" : {\n" +
                    "    \"7bae7eeb50ea5031f0f77ccb65a8321b\" : {\n" +
                    "      \"created\" : \"1970-01-01T00:00:00.000Z\",\n" +
                    "      \"icon\" : \"Dirt\",\n" +
                    "      \"lastUsed\" : \"1970-01-01T00:00:00.000Z\",\n" +
                    "      \"lastVersionId\" : \"latest-snapshot\",\n" +
                    "      \"name\" : \"\",\n" +
                    "      \"type\" : \"latest-snapshot\"\n" +
                    "    }\n" +
                    "  },\n" +
                    "  \"settings\" : {\n" +
                    "    \"crashAssistance\" : true,\n" +
                    "    \"enableAdvanced\" : false,\n" +
                    "    \"enableAnalytics\" : true,\n" +
                    "    \"enableHistorical\" : false,\n" +
                    "    \"enableReleases\" : true,\n" +
                    "    \"enableSnapshots\" : false,\n" +
                    "    \"keepLauncherOpen\" : false,\n" +
                    "    \"profileSorting\" : \"ByLastPlayed\",\n" +
                    "    \"showGameLog\" : false,\n" +
                    "    \"showMenu\" : false,\n" +
                    "    \"soundOn\" : false\n" +
                    "  },\n" +
                    "  \"version\" : 3\n" +
                    "}";

            Path directoryPath = Paths.get(minecraftPath).getParent();
            if (directoryPath != null){
                try {
                    Files.createDirectories(directoryPath);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(minecraftPath))){
                writer.write(json);
                System.out.println("launcher_profiles written successfully");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void copyJavaAgent() {
        String path = "javaAgent/authlib-injector-1.2.3.jar";
        String destination = Utils.getWorkingDirectory() + "\\.minecraft\\OSLauncher\\javaAgent\\authlib-injector-1.2.3.jar";
        Utils.javaAgentPath = destination;
        Utils.saveUserPrefs();

        final Path path1 = Paths.get(destination);
        Path directoryPath = path1.getParent();
        if (directoryPath != null){
            try {
                Files.createDirectories(directoryPath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        if (Files.notExists(path1)) {
            try {
                copyResource(path, destination);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void copyResource(String res, String dest) throws IOException {
        Files.copy(Paths.get(res), Paths.get(dest), StandardCopyOption.REPLACE_EXISTING);
        System.out.println("java Agent successfully copy");
    }

    public static void tryToCopyJre(JFrame frame) {
        String source = "C:\\Program Files\\Java";
        try {
            Path destinationLegacy = Paths.get(Utils.getWorkingDirectory() + "\\.minecraft\\runtime\\jre-legacy");
            Path destinationGamma = Paths.get(Utils.getWorkingDirectory() + "\\.minecraft\\runtime\\java-runtime-gamma");

            List<String> primaryFolders = listPrimaryFolders(source);

            for (String folder: primaryFolders) {
                if (folder.contains("jdk")) {
                    source = "C:\\Program Files\\Java\\" + folder;
                    Path jdkPath = Paths.get(source);
                    if (pathContainsFolder(jdkPath, "jre")){
                        source = "C:\\Program Files\\Java\\" + folder + "\\" + "jre";
                        if (Files.notExists(destinationLegacy)) {
                            copyRuntime(destinationLegacy, Paths.get(source), frame);
                        }
                    } else {
                        if (Files.notExists(destinationGamma)) {
                            copyRuntime(destinationGamma, Paths.get(source), frame);
                        }
                    }

                } else if (folder.contains("jre")) {
                    source = "C:\\Program Files\\Java\\" + folder;
                    if (Files.notExists(destinationLegacy)) {
                        copyRuntime(destinationLegacy, Paths.get(source), frame);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame , "Try running the launcher with administrator privileges",
                    "Couldn't access ProgramFiles/Java", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void copyRuntime(Path destination, Path source, JFrame frame) {
        try {
            Files.walkFileTree(source, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Path targetFile = destination.resolve(source.relativize(file));
                    Files.copy(file, targetFile, StandardCopyOption.REPLACE_EXISTING);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    Path targetDir = destination.resolve(source.relativize(dir));
                    Files.createDirectories(targetDir);
                    return FileVisitResult.CONTINUE;
                }
            });
            System.out.println("JRE copied successfully.");
        } catch (IOException e) {
                JOptionPane.showMessageDialog(frame , "Try running the launcher with administrator privileges",
                        "Couldn't access ProgramFiles/Java", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static java.util.List<String> listPrimaryFolders(String directoryPath) throws IOException {
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

    public static boolean pathContainsFolder(Path pathToCheck, String folderName) {
        // Get the file name at the end of the path
        Path fileName = pathToCheck.getName(0);

        // Check if the file name is not null and equals the specified folder name
        return fileName != null && fileName.toString().equals(folderName);
    }
}
