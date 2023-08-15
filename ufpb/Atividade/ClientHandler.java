import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

class ClientHandler extends Thread {
    private DataInputStream in;
    private DataOutputStream out;
    private Socket clientSocket;
    private static final ConcurrentHashMap<Socket, String> clientMap = new ConcurrentHashMap<>();
    private ExecutorService threadPool;

    public ClientHandler(Socket socket, ExecutorService threadPool) throws IOException {
        clientSocket = socket;
        this.threadPool = threadPool;
        try {
            in = new DataInputStream(clientSocket.getInputStream());
            out = new DataOutputStream(clientSocket.getOutputStream());
        } catch (IOException e) {
            System.out.println("Erro ao criar canais de comunicação: " + e.getMessage());
        }
    }

    public void sendMessage(String message) {
        broadcast("[Servidor]: " + message);
    }

    public void run() {
        try {
            out.writeUTF("Informe seu nome para entrar no chat:");
            String clientName = in.readUTF();
            clientMap.put(clientSocket, clientName);

            broadcast("{ Seja Bem vindo sr(a)" + clientName + " ao Bate-Papo! }");
            broadcast("Digite uma msg: ");

            while (true) {
                String message = in.readUTF();
                System.out
                        .println("Mensagem recebida de " + clientSocket.getRemoteSocketAddress() + ": " + message);
                if (message.equalsIgnoreCase("/quit")) {
                    break;
                }
                broadcast(clientName + ": " + message);
            }

            clientSocket.close();
            clientMap.remove(clientSocket);

        } catch (IOException e) {
            System.out.println("Erro na comunicação com o cliente: " + e.getMessage());
        }
    }

    private void broadcast(String message) {
        for (Socket socket : clientMap.keySet()) {
            threadPool.submit(() -> {
                try {
                    DataOutputStream clientOut = new DataOutputStream(socket.getOutputStream());
                    clientOut.writeUTF(message);
                } catch (IOException e) {
                    System.out.println("Erro ao enviar mensagem para cliente: " + e.getMessage());
                }
            });
        }
    }
}