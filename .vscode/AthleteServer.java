import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class AthleteServer {
private static final int SERVER_PORT = 12345;
private static final String LOG_FILENAME = "logs.txt";

private static List<PrintWriter> activeClients = new ArrayList<>();
public static void main(String[] args) throws IOException {
System.out.println("🏃 Athlete Log Server is up and running on port " + SERVER_PORT);
ServerSocket listener = new ServerSocket(SERVER_PORT);

while (true) {
try {
Socket incomingClient = listener.accept();
System.out.println("➡ Client joined: " + incomingClient.getInetAddress());
new ClientHandler(incomingClient).start();} 
catch (IOException e) {
System.out.println("Something went wrong accepting a client: " + e.getMessage());
}
}}

private static class ClientHandler extends Thread {
private Socket clientSocket;
private PrintWriter clientOut;

public ClientHandler(Socket socket) {
this.clientSocket = socket;}

 public void run() {
try {
BufferedReader reader = new BufferedReader(
new InputStreamReader(clientSocket.getInputStream()));
clientOut = new PrintWriter(clientSocket.getOutputStream(), true);

synchronized (activeClients) {
activeClients.add(clientOut);}

String incomingMessage;
while ((incomingMessage = reader.readLine()) != null) {
System.out.println("[LOG] " + incomingMessage);
writeToFile(incomingMessage);

synchronized (activeClients) {
for (PrintWriter writer : activeClients) {
writer.println(incomingMessage); 
}}
}} 
catch (IOException ex) {
System.out.println("Oops, lost a client: " + ex.getMessage());} 
finally {
try {
clientSocket.close();} 
catch (IOException ignored) {
}
synchronized (activeClients) {
activeClients.remove(clientOut);
}
}}

private void writeToFile(String logEntry) {
try (
FileWriter fw = new FileWriter(LOG_FILENAME, true);
BufferedWriter bw = new BufferedWriter(fw);
PrintWriter logWriter = new PrintWriter(bw)
) {logWriter.println(logEntry);} 
catch (IOException fileErr) {
System.out.println(" Couldn't save to file: " + fileErr.getMessage());
}
}
}
}


