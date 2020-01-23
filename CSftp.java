import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.IllegalBlockingModeException;
import java.util.StringTokenizer;

//
// This is an implementation of a simplified version of a command 
// line ftp client. The program always takes two arguments
//


public class CSftp {
    static final int MAX_LEN = 255;
    static final int ARG_CNT = 2;

    // Socket, reader, and writer for the control connection
    static Socket ClientSocket;
    static BufferedReader in;
    static PrintWriter out;

    /**
     * Sends the user's username to the FTP server.
     * @param username
     */
    public static void user(String username){
        try {
            // Send/echo command to FTP server and print response
            System.out.println("--> USER " + username);
            out.print("USER " + username + "\r\n");
            out.flush();
            System.out.println("<-- " + in.readLine());
        } catch (IOException e) {
            // Error while attempting to read from/write to server, close connection
            System.out.println("0xFFFD: Control connection I/O error, closing control connection.");
            closeConnection();
        } catch (Exception e) {
            // Any other error occurs
            System.out.println("0xFFFF: Processing error. " + e.getMessage());
        }
    }

    /**
     * Sends the user's password to the FTP server.
     * @param password
     */
    public static void pw(String password){
        try {
            // Send/echo command to FTP server and print response
            System.out.println("--> PASS " + password);
            out.print("PASS " + password + "\r\n");
            out.flush();
            System.out.println("<-- " + in.readLine());
        } catch (IOException e) {
            // Error while attempting to read from/write to server, close connection
            System.out.println("0xFFFD: Control connection I/O error, closing control connection.");
            closeConnection();
        } catch (Error e) {
            // Any other error occurs
            System.out.println("0xFFFF: Processing error. " + e.getMessage());
        }
    }

    public static void get(String remote){
        System.out.println("Retrieving file " + remote + "...");
        // TODO: implement
    }

    private static void makeFile(String fileName) {

    }

    /**
     * Changes the current working directory on the server to directory.
     * @param directory
     */
    public static void cd(String directory){
        try {
            // Send/echo command to FTP server and print response
            System.out.println("--> CWD " + directory);
            out.print("CWD " + directory + "\r\n");
            out.flush();
            System.out.println("<-- " + in.readLine());
        } catch (IOException e) {
            // Error while attempting to read from/write to server, close connection
            System.out.println("0xFFFD: Control connection I/O error, closing control connection.");
            closeConnection();
        } catch (Exception e) {
            // Any other error occurs
            System.out.println("0xFFFF: Processing error. " + e.getMessage());
        }
    }

    /**
     * If connected to the server, closes any established connection and exits the program.
     */
    public static void quit(){
        try {
            // Send/echo command to FTP server and print response.
            System.out.println("--> QUIT");
            out.print("QUIT\r\n");
            out.flush();
            System.out.println("<-- " + in.readLine());
            // Exit command prompt mode
            closeConnection();
        } catch (IOException e) {
            // Error while attempting to read from/write to server, close connection
            System.out.println("0xFFFD: Control connection I/O error, closing control connection.");
            closeConnection();
        } catch (Exception e) {
            // Any other error occurs
            System.out.println("0xFFFF: Processing error. " + e.getMessage());
        }
    }

    /**
     * Displays the set of features/extensions the server supports.
     */
    public static void features(){
        try {
            // Send/echo command to FTP server and print response.
            System.out.println("--> FEAT");
            out.print("FEAT\r\n");
            out.flush();
            String line = in.readLine();
            System.out.println("<-- " + line);
            while ((line = in.readLine()) != null){
                // break if server says there are no more features
                if (line.equals("211 End FEAT.")) {
                    System.out.println("    " + line);
                    break;
                }
                System.out.println("   " + line);
            }
        } catch (IOException e) {
            // Error while attempting to read from/write to server, close connection
            System.out.println("0xFFFD: Control connection I/O error, closing control connection.");
            closeConnection();
        } catch (Exception e) {
            // Any other error occurs
            System.out.println("0xFFFF: Processing error. " + e.getMessage());
        }
    }

    /**
     * Establishes a data connection and retrieves a list of files in the current working directory on the server
     */
    public static void dir(){
        String IPAddress = "";
        int portNumber = 0;

        try {
            // Create passive connection, Send/echo command to FTP server and print response.
            System.out.println("--> PASV");
            out.print("PASV\r\n");
            out.flush();
            String line = in.readLine();
            System.out.println("<-- " + line);

            // Check if user is logged in
            if (line.split(" ")[0].equals("530")) {
                return;
            }

            // Parse IP and port number of connection
            int startIndex = line.indexOf("(");
            int endIndex = line.indexOf(")");
            String IPAndPort = line.substring(startIndex+1, endIndex);
            StringTokenizer stringTokenizer = new StringTokenizer(IPAndPort, ",");
            IPAddress = stringTokenizer.nextToken() + "." + stringTokenizer.nextToken() + "." +
                    stringTokenizer.nextToken() + "." + stringTokenizer.nextToken();
            portNumber = Integer.parseInt(stringTokenizer.nextToken())*256 +
                    Integer.parseInt(stringTokenizer.nextToken());

            // Create socket and input stream for data connection
            Socket ClientSocketDataConnection = new Socket();
            ClientSocketDataConnection.connect(new InetSocketAddress(IPAddress, portNumber), 10000);
            BufferedReader inDataConnection = new BufferedReader(
                    new InputStreamReader(ClientSocketDataConnection.getInputStream()));

            try {
                // Send/echo command to FTP server and print response.
                System.out.println("--> LIST");
                out.print("LIST\r\n");
                out.flush();
                System.out.println(in.readLine());
                // Print server response
                System.out.println("<-- " + inDataConnection.readLine());
                // Print input from data connection
                while ((line = inDataConnection.readLine()) != null){
                    System.out.println("   " + line);
                }
                // Print server response
                System.out.println("<-- " + in.readLine());
            } catch (IOException e) {
                // Error while attempting to read from/write to data transfer connection, close connection
                System.out.println("0x3A7 Data transfer connection I/O error, closing data connection");
            }

            // Close data connection socket and reader
            ClientSocketDataConnection.close();
            inDataConnection.close();

        } catch (IllegalBlockingModeException | IllegalArgumentException | IOException e) {
            // Data connection cannot be established within 10 seconds or socket could not be created
            System.out.println("0x3A2 Data transfer connection to " + IPAddress + " on port " +
                    portNumber + " failed to open");
        } catch (Exception e) {
            // Any other error occurs
            System.out.println("0xFFFF: Processing error. " + e.getMessage());
        }

    }

    /**
     * parse host name and port number from user input and open a socket, reader, and writer to the host name and port.
     * @param args
     */
    public static void openConnection(String[] args) {
        String hostname = "";
        int portNumber = -1;

        try {
            // If there are not 2 arguments
            if (args.length != ARG_CNT) {
                if (args.length != 1) {
                    // If there is not 1 argument either, exit
                    System.out.print("Usage: cmd ServerAddress ServerPort\n");
                    System.exit(-1);
                } else {
                    // If there is 1 argument, that argument is host name and port number = 21
                    hostname = args[0];
                    portNumber = 21;
                }
            } else {
                // If there are 2 arguments, first argument is host name and second is port number.
                hostname = args[0];
                portNumber = Integer.parseInt(args[1]);
            }

            // Open socket connection, reader, and writer
            ClientSocket = new Socket();
            ClientSocket.connect(new InetSocketAddress(hostname, portNumber), 20000);
            in = new BufferedReader(new InputStreamReader(ClientSocket.getInputStream()));
            out = new PrintWriter(ClientSocket.getOutputStream(), true);
            System.out.println("<-- " + in.readLine());

        } catch (NumberFormatException e) {
            // Ensures port number is an int
            System.out.println("Usage: port number must be an integer");
            System.exit(-1);
        } catch (IllegalBlockingModeException | IllegalArgumentException | IOException e) {
            // Control connection cannot be established within 20 seconds or socket could not be created, exit program
            System.out.println("0xFFFC Control connection to " + hostname + " on port "
                    + portNumber + " failed to open");
            System.exit(1);
        } catch (Exception e) {
            // Any other error occurs
            System.out.println("0xFFFF: Processing error. " + e.getMessage());
        }
    }

    /**
     * Close the client socket, reader, and writer and exit.
     */
    private static void closeConnection() {
        try {
            // Close control connection socket, reader, and writer
            if (ClientSocket != null) ClientSocket.close();
            if (in != null) in.close();
            if (out != null) out.close();
            // Exit program
            System.exit(0);
        } catch (IOException e) {
            // Error occurs when trying to close connections
            System.out.println("0xFFFF: Processing error. " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        byte cmdString[] = new byte[MAX_LEN];

        // open control connection
        openConnection(args);

        try {
            for (int len = 1; len > 0; ) {
                System.out.print("csftp> ");
                len = System.in.read(cmdString);
                if (len <= 0)
                    break;

                // If line is empty or does not start with "#", silently ignore
                if (len > 1 && (char)cmdString[0] != '#'){
                    // Parse command and any parameters, call appropriate helper function or print errors
                    String[] commands = (new String(cmdString, 0, len)).trim().split("\\s+");
                    String command = commands[0];

                    if (command.equals("user") || command.equals("pw") || command.equals("get")
                            || command.equals("cd")){
                        if (commands.length != 2) System.out.println("0x002 Incorrect number of arguments");
                        else {
                            String param = commands[1];
                            switch (command) {
                                case "user": user(param);
                                break;
                                case "pw": pw(param);
                                break;
                                case "get": get(param);
                                break;
                                case "cd": cd(param);
                                break;
                            }
                        }
                    } else if (command.equals("quit") || command.equals("features") || command.equals("dir")){
                        if (commands.length != 1) System.out.println("0x002 Incorrect number of arguments");
                        else {
                            switch (command) {
                                case "quit": quit();
                                break;
                                case "features": features();
                                break;
                                case "dir": dir();
                                break;
                            }
                        }
                    } else {
                        System.out.println("0x001 Invalid command");
                    }
                }
            }
        } catch (IOException exception) {
            // Exception is thrown while the client is reading its commands
            System.err.println("0xFFFE Input error while reading commands, terminating.");
            System.exit(1);
        }
    }
}
