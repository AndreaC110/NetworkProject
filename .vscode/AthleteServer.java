import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class AthleteServer {
private static final int PORT = 12345;
private static List<PrintWriter> clientWriters = new ArrayList<>();

public static void main(String[] args) throws IOException {
System.out.println("Athlete Log Server started on port " + PORT);
ServerSocket serverSocket = new ServerSocket(PORT);

while (true) {
Socket client = serverSocket.accept();
System.out.println("New client connected: " + client.getInetAddress());
new ClientHandler(client).start();
}}

private static class ClientHandler extends Thread {
private Socket socket;
private PrintWriter out;

public ClientHandler(Socket socket) {
this.socket = socket;}

public void run() {try {
BufferedReader in = new BufferedReader(
new InputStreamReader(socket.getInputStream()));
out = new PrintWriter(socket.getOutputStream(), true);

synchronized (clientWriters) {clientWriters.add(out);}

String message;
while ((message = in.readLine()) != null) {
System.out.println("Log: " + message);
synchronized (clientWriters) {for (PrintWriter writer : clientWriters) {
writer.println(message);
}}
}} 
catch (IOException e) {
System.out.println("Client error: " + e.getMessage());
} finally {try {
socket.close();
} catch (IOException ignored) {}
synchronized (clientWriters) {
clientWriters.remove(out);
}}}
}}
