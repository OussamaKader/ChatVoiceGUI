package server;

import java.io.*;
import java.net.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChatServer {

    private static final int PORT = 6000;
    private static CopyOnWriteArrayList<ClientHandler> clients = new CopyOnWriteArrayList<>();

    public static void main(String[] args) {
        System.out.println("Server running on port " + PORT);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {

            while (true) {
                Socket socket = serverSocket.accept();
                ClientHandler handler = new ClientHandler(socket);
                clients.add(handler);
                handler.start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    static class ClientHandler extends Thread {
        private Socket socket;
        private BufferedReader reader;
        private PrintWriter writer;

        public ClientHandler(Socket socket) {
            this.socket = socket;
            try {
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                writer = new PrintWriter(socket.getOutputStream(), true);
            } catch (IOException e) {}
        }

        @Override
        public void run() {
            try {
                String line;
                while ((line = reader.readLine()) != null) {
                    broadcast(line);
                }
            } catch (Exception ignored) {
            } finally {
                close();
            }
        }

        private void broadcast(String msg) {
            for (ClientHandler c : clients) {
                if (c != this) {
                    c.writer.println(msg);
                }
            }
        }

        private void close() {
            try { socket.close(); } catch (Exception ignored) {}
            clients.remove(this);
        }
    }
}
