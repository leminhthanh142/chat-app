import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static final int PORT = 6666;
    private final ServerSocket serverSocket;

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void startServer() {
        try {
            System.out.println("-------- [SERVER] is started!!! ---------");
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                System.out.println("A new Client has connected!");
                ClientHandler clientHandler = new ClientHandler(socket);

                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        } catch (IOException e) {
            System.err.println("Can not start server");
            closeServerSocket();
            e.printStackTrace();
        }
    }

    public void closeServerSocket() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Can not close server socket");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        Server server = new Server(serverSocket);
        server.startServer();
    }
}
