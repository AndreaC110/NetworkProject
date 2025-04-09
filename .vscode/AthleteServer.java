import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class AthleteServer {
private static final int PORT = 12345;
private static final String FILE_NAME = "Workoutlogs.txt";
private static final List<PrintWriter> clientWriters = new ArrayList<>();

public static void main(String[] args) {
System.out.println("Starting Athlete's Server on port " + PORT);

try (ServerSocket serverSocket = new ServerSocket(PORT)) {
while (true) {
try {
Socket clientSocket = serverSocket.accept();
System.out.println("New connection from " + clientSocket.getInetAddress());
ClientHandler handler = new ClientHandler(clientSocket);handler.start();} 
catch (IOException e) {System.err.println("Failed to accept connection: " + e.getMessage());}
    }} 
catch (IOException startupError) {System.out.println("Error starting server: " + startupError.getMessage());}
    }


private static class ClientHandler extends Thread {
private Socket socket;
private PrintWriter clientOut;

public ClientHandler(Socket socket) {
this.socket = socket;
}
public void run() {
BufferedReader in = null;
PrintWriter out = null;

try {
in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
out = new PrintWriter(socket.getOutputStream(), true);
clientOut = out;

synchronized (clientWriters) {
clientWriters.add(clientOut);
}

String line;
while ((line = in.readLine()) != null) {
System.out.println("Received: " + line);
appendToFile(line);

synchronized (clientWriters) {
for (PrintWriter pw : clientWriters) {
pw.println(line);
}}
}} 
catch (IOException readWriteErr) {
System.out.println("Client connection dropped: " + readWriteErr.getMessage());} 
finally {
try {
if (socket != null) socket.close();} 
catch (IOException e) {
}

synchronized (clientWriters) {
clientWriters.remove(clientOut);
                }}
        }
private void appendToFile(String message) {
BufferedWriter writer = null;
try {
writer = new BufferedWriter(new FileWriter(FILE_NAME, true));
writer.write(message);
writer.newLine();
writer.flush();
} 
catch (IOException fileErr) {
System.out.println("Not able to log: " + fileErr.getMessage());} 
finally {
if (writer != null) {
try {
writer.close();} 
catch (IOException closeErr) {}
}}
}}
}
