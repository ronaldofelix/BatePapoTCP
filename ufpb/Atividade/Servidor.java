import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class Servidor {
    public static void main(String args[]) {
        ExecutorService threadPool = Executors.newFixedThreadPool(10);
        int serverPort = 6666;

        try (ServerSocket serverSocket = new ServerSocket(serverPort)) {
            System.out.println(
                    "Servidor iniciado no endereço: " + InetAddress.getLocalHost() + ":" + serverPort);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println(
                        "Conexão estabelecida com: " + clientSocket.getInetAddress() + ":" + clientSocket.getPort());

                ClientHandler handler = new ClientHandler(clientSocket, threadPool);
                threadPool.submit(handler);
            }
        } catch (IOException e) {
            System.out.println("Erro no socket: " + e.getMessage());
        }
    }

}