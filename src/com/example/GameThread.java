package com.example;

import org.json.simple.JSONObject;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.InputStreamReader;

public class GameThread{

    public void setupCommand(JSONObject version) {

        ArgumentsGen argumentsGen = new ArgumentsGen(version);
        String command = argumentsGen.genWholeCommand();

        String id = (String) version.get("id");

        String idBat = id + ".bat";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(idBat))){
            writer.write(command);
            System.out.println("bat file successfully written");

        } catch (Exception e) {
            e.printStackTrace();
        }

        Initializegame(idBat);
    }

    public void Initializegame(String id) {

        Runnable r = () -> {
            ProcessBuilder processBuilder = new ProcessBuilder(id);
            System.out.println("Initializing Game...");
            //processBuilder.redirectErrorStream(true);
            //processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            try {
                Process process = processBuilder.start();
                System.out.println("Process Started...");

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
                    //System.exit(0);
                    System.out.println("Process stopped");
                    WindowManager.Instance.playButton.setEnabled(true);
                    Utils.resetUtils();
                    process.destroy();
                }
                if (exitVal != 0) {
                    System.out.println("something went wrong");
                    WindowManager.Instance.playButton.setEnabled(true);
                    Utils.resetUtils();
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        };
        new Thread(r, "asyncOut").start();
    }
}
