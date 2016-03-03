import java.io.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private ServerSocket serverSocket;
    private int port;
    private String cmd;

    public Server(int port) {
        this.port = port;
    }

    /**
     * Executes server commands
     * @throws IOException no input or output stream found
     */
    public void start() throws IOException {
        System.out.println("Starting the socket server at port:" + Integer.toString(port));
        serverSocket = new ServerSocket(port);
        System.out.println("Waiting for clients...");
        while (true) {
            try (Socket client = serverSocket.accept();
                 BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));
                 BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));){
                
                System.out.println("Connection from " + client.getInetAddress());
                String line = br.readLine();
                System.out.println(line);
                String proc = takeCommand(line);
                runProcess(proc, client);
                System.out.println("Command Processed");
               // br.close();
                writer.flush();
               // writer.close();
                
            } catch (IOException e) {
                System.out.println("Client Disconnected.");
            } catch (Exception e) {
                System.out.println("\nException hit: " + e.getStackTrace());
            }
        }
    }

    /**
     * Takes in command the client sent and deciphers its meaning
     * @param command command sent by client
     * @return deciphered command
     */
    private String takeCommand(String command) {
        cmd = "";
        switch (command) {
            case "1":
                cmd = "date";
                break;
            case "2":
                cmd = "uptime";
                break;
            case "3":
                cmd = "free";
                break;
            case "4":
                cmd = "netstat";
                break;
            case "5":
                cmd = "who";
                break;
            case "6":
                cmd = "ps";
                break;
        }
        return cmd;
    }

    /**
     * Runs the deciphered command given by the client
     * @param cmd deciphered command
     * @param client socket connected to client
     * @throws IOException if input or output streams do not exist
     */
    private void runProcess(String cmd, Socket client) throws IOException {
        ProcessBuilder pb = new ProcessBuilder(cmd);
        Process p = pb.start();
        InputStream is = p.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line;
        
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
        boolean moreLines = true;

        while (moreLines) {
            line = br.readLine();
            if (line != null) {
                try {
                    writer.append(line + "\n");
                    writer.flush();
                } catch (Exception e) {
                    System.out.println("Exception:  " + e);
                } // end try catch
            } else {
                moreLines = false;
            }

        } // end while morelines

    }

    public static void main(String[] args) {
        int portNumber = 1234;
        if (args.length == 1) {
            portNumber = Integer.parseInt(args[0]);
        }
        try {
            new Server(portNumber).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
