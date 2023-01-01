import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.nio.Buffer;
import java.util.Scanner;

public class Client {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 6666;
    private Socket socket;
    private BufferedWriter bufferedWriter;
    private BufferedReader bufferedReader;
    private String userName;

    public Client(Socket socket, String userName) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.userName = userName;
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        try {
            if (socket != null) {
                socket.close();
            }
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage() {
        try {
            bufferedWriter.write(userName);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            Scanner scanner = new Scanner(System.in);
            while (socket.isConnected()) {
                String message = scanner.nextLine();
                bufferedWriter.write(userName + ": " + message);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    public void listenForMessage() {
        new Thread(() -> {
            String messageFromGroupChat;
            while (socket.isConnected()) {
                try {
                    messageFromGroupChat = bufferedReader.readLine();
                    System.out.println(messageFromGroupChat);
                }
                catch (IOException e) {
                    e.printStackTrace();
                    closeEverything(socket, bufferedReader, bufferedWriter);
                }
            }
        }).start();
    }

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your username for the group chat: ");
        String userName = scanner.nextLine();

        Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
        Client client = new Client(socket, userName);
        client.listenForMessage();
        client.sendMessage();
    }
}
