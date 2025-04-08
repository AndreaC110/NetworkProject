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

private static List<PrintWriter> clientList = new ArrayList<>();

public static void main(String[] args) throws IOException {
System.out.println("Starting Athlete Logging Server on port " + PORT);

ServerSocket server = new ServerSocket(PORT);

while (true) {
try {
Socket incoming = server.accept();
System.out.println("Client joined: " + incoming.getInetAddress());

new ClientHandler(incoming).start();
} catch (IOException e) {
System.err.println("Problem accepting client connection.");
}
}
}
private static class ClientHandler extends Thread {

private Socket clientSocket;
private PrintWriter clientOut;

public ClientHandler(Socket socket) {
this.clientSocket = socket;}

public void run() {
BufferedReader clientIn = null;

try {
clientIn = new BufferedReader(
new InputStreamReader(clientSocket.getInputStream()));
clientOut = new PrintWriter(clientSocket.getOutputStream(), true);

synchronized (clientList) {
clientList.add(clientOut); }

String incomingMessage;
while ((incomingMessage = clientIn.readLine()) != null) {
System.out.println("Received log: " + incomingMessage);

synchronized (clientList) {for (PrintWriter pw : clientList) {
pw.println(incomingMessage);
}}
}

} catch (IOException ex) {
System.out.println("Lost client: " + ex.getMessage());

} finally {
try {
if (clientSocket != null) clientSocket.close();
} catch (IOException ignored) {
}
synchronized (clientList) {
clientList.remove(clientOut); 
}}
}}
}

