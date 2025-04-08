import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.*;

public class AthleteClient {
    private BufferedReader reader;
    private PrintWriter writer;
    private JTextArea logArea;
    private JTextField nameInput;
    private JTextField sportInput;
    private JTextField hoursInput;

    public AthleteClient(String serverAddress) {
        JFrame frame = new JFrame("Athlete Logger");
        logArea = new JTextArea(20, 50);
        logArea.setEditable(false);

        nameInput = new JTextField(10);
        sportInput = new JTextField(10);
        hoursInput = new JTextField(5);

        JButton sendBtn = new JButton("Submit");

        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("Athlete Name:"));
        inputPanel.add(nameInput);
        inputPanel.add(new JLabel("Sport:"));
        inputPanel.add(sportInput);
        inputPanel.add(new JLabel("Number of Hours:"));
        inputPanel.add(hoursInput);
        inputPanel.add(sendBtn);

        frame.getContentPane().add(new JScrollPane(logArea), BorderLayout.CENTER);
        frame.getContentPane().add(inputPanel, BorderLayout.SOUTH);

        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        sendBtn.addActionListener(e -> {
            try {
                submitLog();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });

        hoursInput.addActionListener(e -> {
            try {
                submitLog();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });

        try {
            int port = 12345;
            Socket connection = new Socket(serverAddress, port);
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            writer = new PrintWriter(connection.getOutputStream(), true);

            new Thread(this::extracted).start();

        } catch (IOException connEx) {
            JOptionPane.showMessageDialog(frame, "Could not connect: " + connEx.getMessage());
        }
    }

    /**
     * 
     */
    private void extracted() {
        try {
            String incoming;
            while ((incoming = reader.readLine()) != null) {
                logArea.append(incoming + "\n");
            }
        } catch (IOException e) {
            logArea.append("Lost connection to server\n");
        }}
    private void submitLog() throws IOException {
        String name = nameInput.getText().trim();
        String sport = sportInput.getText().trim();
        String hoursStr = hoursInput.getText().trim();

        if (name.isEmpty() || sport.isEmpty() || hoursStr.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please fill out all the fields.");
            return;
        }

        try {
            double hours = Double.parseDouble(hoursStr);
            String currentDate = new SimpleDateFormat("MM/dd/yyyy").format(new Date());
            String logMessage = String.format("[%s] %s – %s – %.2f hrs", currentDate, name, sport, hours);

            writer.println(logMessage); // Send to server

            FileWriter fw = new FileWriter("Workoutlogs.txt", true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(logMessage + "\n"); // Save to file
            bw.close();

            // Clear input fields
            nameInput.setText("");
            sportInput.setText("");
            hoursInput.setText("");

        } catch (NumberFormatException numEx) {
            JOptionPane.showMessageDialog(null, "Please enter a valid number for hours.");
        }
    }

    public static void main(String[] args) {
        String ipAddr = JOptionPane.showInputDialog("Enter IP address:");
        if (ipAddr != null && !ipAddr.trim().isEmpty()) {
            new AthleteClient(ipAddr);
        }
    }
}
