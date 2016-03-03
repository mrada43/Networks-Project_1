
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {

    private String hostname;
    private int port;
    Socket socketClient;
    BufferedReader stdIn;
    BufferedWriter wr;

    public Client(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    public void start() {
        int command = 0;
        try {
            //trying to establish connection to the server
            do {
                connect();
                printMenu();
                command = getCommand();
                if (command != 7) {
                    sendSelection(command);
                    //if successful, read response from server
                    readResponse();
                }
            } while (command != 7);

        } catch (UnknownHostException e) {
            System.err.println("Host unknown. Cannot establish connection");
        } catch (IOException e) {
            System.err.println("Cannot establish connection. Server may not be up." + e.getMessage());
        }
    }

    public void connect() throws UnknownHostException, IOException {
        System.out.println("Attempting to connect to " + hostname + ":" + port);
        socketClient = new Socket(hostname, port);
        System.out.println("Connection Established");
    }

    public void readResponse() throws IOException {
        String userInput;
        stdIn = new BufferedReader(new InputStreamReader(socketClient.getInputStream()));
        System.out.println("Response from server:");
        while ((userInput = stdIn.readLine()) != null) {
            System.out.println(userInput);
        }
    }

    private void printMenu() {
        System.out.println("Hey! Choose an option below and we'll fetch from the server!");
        System.out.println("\n\n ============= MENU ============= \n");
        System.out.println("1. Host current Date and Time ");
        System.out.println("2. Host uptime ");
        System.out.println("3. Host memory use ");
        System.out.println("4. Host Netstat ");
        System.out.println("5. Host current users ");
        System.out.println("6. Host running processes ");
        System.out.println("7. Quit \n");
    }

    private int getCommand() {
        int command = 0;
        boolean correctInput = false;
        String commandEntered = "";
        Scanner scan = new Scanner(System.in);
        do {
            System.out.print("\n>>>>>  ");
            commandEntered = scan.next();
            try {
                command = Integer.parseInt(commandEntered);
                correctInput = true;
            } catch (NumberFormatException e) {
                System.out.println("Only input a number between 1 and 7.");
            }
        } while (!correctInput);
        return command;
    }

    private void sendSelection(int command) {
        try {
            wr = new BufferedWriter(new OutputStreamWriter(socketClient.getOutputStream()));
            wr.write(Integer.toString(command));
            wr.newLine();
            wr.flush();
        } catch (IOException e) {
            System.out.println("IO Exception thrown." + e.getMessage());
        }
    }

    public static void main(String arg[]) {
        //Creating a SocketClient object
        new Client("192.168.100.104", 1234).start();
    }
}
