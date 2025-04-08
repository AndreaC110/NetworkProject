import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;



public class AthleteClient {
private BufferedReader in;
private PrintWriter out;
private JTextArea logArea;
private JTextField nameField, sportField, timeField;

public AthleteClient(String serverIP) {
JFrame frame = new JFrame("Athlete Time Logger");
logArea = new JTextArea(20, 50);
logArea.setEditable(false);

nameField = new JTextField(10);
sportField = new JTextField(10);
timeField = new JTextField(5);
JButton sendButton = new JButton("Log Time");

JPanel inputPanel = new JPanel();
inputPanel.add(new JLabel("Athlete Name:"));
inputPanel.add(nameField);
inputPanel.add(new JLabel("Sport:"));
inputPanel.add(sportField);
inputPanel.add(new JLabel("Number of Hours:"));
inputPanel.add(timeField);
inputPanel.add(sendButton);

frame.getContentPane().add(new JScrollPane(logArea), BorderLayout.CENTER);
frame.getContentPane().add(inputPanel, BorderLayout.SOUTH);
frame.pack();
frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
frame.setVisible(true);

sendButton.addActionListener(e -> sendLog());
timeField.addActionListener(e -> sendLog());

        try {
Socket socket = new Socket(serverIP, 12345);
in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
out = new PrintWriter(socket.getOutputStream(), true);
new Thread(() -> { try {
String line;
while ((line = in.readLine()) != null) {
logArea.append(line + "\n");
}
} catch (IOException e) {
logArea.append("Connection lost.\n");
}
}).start();

} catch (IOException ex) {
JOptionPane.showMessageDialog(frame, "Connection failed: " + ex.getMessage());
}}
private void sendLog() {
String name = nameField.getText().trim();
String sport = sportField.getText().trim();
String hours = timeField.getText().trim();
if (!name.isEmpty() && !sport.isEmpty() && !hours.isEmpty()) {try {
double timeValue = Double.parseDouble(hours);
String date = new SimpleDateFormat("MM/dd/yyyy").format(new Date());
String log = String.format("[%s] %s – %s – %.2f hrs", date, name, sport, timeValue);
out.println(log);
nameField.setText("");
sportField.setText("");
timeField.setText("");
} catch (NumberFormatException e) {
JOptionPane.showMessageDialog(null, "Enter a valid number for hours.");}} else {
JOptionPane.showMessageDialog(null, "Fill in all fields.");
}}

public static void main(String[] args) {
String ip = JOptionPane.showInputDialog("Enter Server IP Address:");
if (ip != null && !ip.trim().isEmpty()) {
new AthleteClient(ip);
}}
}
