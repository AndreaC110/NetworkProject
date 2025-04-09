import java.awt.BorderLayout;
import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.*;

public class AthleteClient {
private BufferedReader inputReader;
private PrintWriter socketWriter;
private JTextArea logOutput;
private JTextField nameField;
private JTextField sportField;
private JTextField hoursField;

public AthleteClient(String serverIP) {
JFrame mainWindow = new JFrame("Athlete Workout Logger");

logOutput = new JTextArea(20, 50);
logOutput.setEditable(false);

nameField = new JTextField(10);
sportField = new JTextField(10);
hoursField = new JTextField(5);

JButton submitBtn = new JButton("Submit");

JPanel inputs = new JPanel();
inputs.add(new JLabel("Athlete Name:"));
inputs.add(nameField);
inputs.add(new JLabel("Sport:"));
inputs.add(sportField);
inputs.add(new JLabel("Number of Hours:"));
inputs.add(hoursField);
inputs.add(submitBtn); 

mainWindow.getContentPane().add(new JScrollPane(logOutput), BorderLayout.CENTER);
mainWindow.getContentPane().add(inputs, BorderLayout.SOUTH);

mainWindow.pack();
mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
mainWindow.setVisible(true);

submitBtn.addActionListener(evt -> {
try {
handleSubmission();} 
catch (IOException err) {err.printStackTrace(); }
        });
hoursField.addActionListener(evt -> {
try {handleSubmission();} 
catch (IOException err) {err.printStackTrace();}
 });

try {
int defaultPort = 12345;
Socket socket = new Socket(serverIP, defaultPort);
inputReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
socketWriter = new PrintWriter(socket.getOutputStream(), true); 

Thread backgroundListener = new Thread(() -> listenForMessages());
backgroundListener.start();}

catch (IOException connectErr) {
JOptionPane.showMessageDialog(mainWindow, "Unable to connect to server:\n" + connectErr.getMessage());}
}

private void listenForMessages() {
try {
String line;
while ((line = inputReader.readLine()) != null) {
logOutput.append(line + "\n");}
} 
catch (IOException e) {
logOutput.append("lost connection to the server");
}
}

private void handleSubmission() throws IOException {
String athleteName = nameField.getText().trim();
String sportName = sportField.getText().trim();
String hoursText = hoursField.getText().trim();

if (athleteName.isEmpty() || sportName.isEmpty() || hoursText.isEmpty()) {
JOptionPane.showMessageDialog(null, "All fields are required!");
return;}

try {
double hoursTrained = Double.parseDouble(hoursText);

String dateNow = new SimpleDateFormat("MM/dd/yyyy").format(new Date());
String fullLogEntry = String.format("[%s] %s – %s – %.2f hrs", dateNow, athleteName, sportName, hoursTrained);

socketWriter.println(fullLogEntry);

FileWriter fileWriter = new FileWriter("Workoutlogs.txt", true);
BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
bufferedWriter.write(fullLogEntry + "\n");
bufferedWriter.close();

nameField.setText("");
sportField.setText("");
hoursField.setText("");} 
catch (NumberFormatException badNumber) {
JOptionPane.showMessageDialog(null, "Hours must be a number");
        
}
    }

public static void main(String[] args) {
String inputIP = JOptionPane.showInputDialog("Enter server IP address:");
if (inputIP != null && !inputIP.trim().isEmpty()) {
new AthleteClient(inputIP.trim());} 
else {
JOptionPane.showMessageDialog(null, "Exiting.");
        }
    }
}
