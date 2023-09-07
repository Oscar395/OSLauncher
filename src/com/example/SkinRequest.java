package com.example;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Base64;

public class SkinRequest {

    public String getUsernameUUID(String userName) throws IOException, ParseException {
        String UUIDString = "";

            URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + userName);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");

            // Read the response
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder response = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            System.out.println("Response: " + response.toString());

            connection.disconnect();

            JSONParser parser = new JSONParser();

            Object parseStr = parser.parse(response.toString());

            JSONObject responseJson = (JSONObject) parseStr;

            if (responseJson.containsKey("id")) {
                UUIDString = (String) responseJson.get("id");
            }

        return UUIDString;
    }

    public Image getSkin(String name) {
            try {
                String uuid = getUsernameUUID(name);

                URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid);

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String line;
                StringBuilder response = new StringBuilder();

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                System.out.println("Response: " + response.toString());

                urlConnection.disconnect();

                JSONParser parser = new JSONParser();

                Object parseStr = parser.parse(response.toString());

                JSONObject responseJson = (JSONObject) parseStr;

                if (responseJson.containsKey("properties")) {
                    JSONArray propertiesArray = (JSONArray) responseJson.get("properties");

                    JSONObject properties = (JSONObject) propertiesArray.get(0);
                    String value = (String) properties.get("value");

                    // Decode the Base64 string
                    byte[] decodedBytes = Base64.getDecoder().decode(value);
                    String decodedString = new String(decodedBytes);

                    Object decodedParse = parser.parse(decodedString);

                    JSONObject texturesInfo = (JSONObject) decodedParse;

                    JSONObject textures = (JSONObject) texturesInfo.get("textures");
                    JSONObject skin = (JSONObject) textures.get("SKIN");
                    String urlSkin = (String) skin.get("url");

                    System.out.println(urlSkin);

                    URL skinUrl = new URL(urlSkin);

                    // Read the image from the URL
                    BufferedImage bufferedImage = ImageIO.read(skinUrl);

                    // Now you have the image as an Image object
                    System.out.println("Image loaded successfully.");

                    return bufferedImage;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
    }

    private void saveSkinPng(String imageUrl, String outputPath) {
        try {
            // Create a URL object
            URL url = new URL(imageUrl);

            // Open a connection to the URL
            try (InputStream in = new BufferedInputStream(url.openStream())) {
                // Save the image to a file using java.nio.file
                Path outputPathPath = Paths.get(outputPath);
                Files.copy(in, outputPathPath, StandardCopyOption.REPLACE_EXISTING);

                System.out.println("Image saved to: " + outputPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
