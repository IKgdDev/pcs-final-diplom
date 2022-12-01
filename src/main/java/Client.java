import java.io.*;
import java.net.Socket;

public class Client {
    private static final String HOST = "localhost";
    private static final int PORT = 8989;

    public static void main(String[] args) throws IOException {
        try (
                Socket clientSocket = new Socket(HOST, PORT);
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter outFile = new PrintWriter("Reply.json");
        ) {

            out.println("бизнес");

            outFile.println(in.readLine());
        }
    }
}