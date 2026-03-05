package client;

import java.io.*;
import java.net.*;

public class ChatClient {

    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 6000);
             BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {

            System.out.println("Connected to server.");

            // Listener de messages
            new Thread(() -> {
                try {
                    String msg;
                    while ((msg = reader.readLine()) != null) {
                        System.out.println("SERVER: " + msg);
                    }
                } catch (Exception e) {}
            }).start();

            // Envoi utilisateur
            String line;
            while ((line = keyboard.readLine()) != null) {
                writer.println(line);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
