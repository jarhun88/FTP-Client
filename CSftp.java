import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

//
// This is an implementation of a simplified version of a command 
// line ftp client. The program always takes two arguments
//


public class CSftp {
    static final int MAX_LEN = 255;
    static final int ARG_CNT = 2;
    static Socket ClientSocket;
    static BufferedReader in;
    static PrintWriter out;

    public static void user(String username){
        try {
            System.out.println("--> USER " + username);
            out.print("USER " + username + "\r\n");
            out.flush();
            System.out.println("<-- " + in.readLine());
        } catch (IOException e) {
            System.out.println("0xFFFD: Control connection I/O error, closing control connection.");
            closeConnection();
        } catch (Exception e) {
            System.out.println("0xFFFF: Processing error. " + e.getMessage());
        }
    }

    public static void pw(String password){
        try {
            System.out.println("--> PASS " + password);
            out.print("PASS " + password + "\r\n");
            out.flush();
            System.out.println("<-- " + in.readLine());
        } catch (IOException e) {
            System.out.println("0xFFFD: Control connection I/O error, closing control connection.");
            closeConnection();
        } catch (Error e) {
            System.out.println("0xFFFF: Processing error. " + e.getMessage());
        }
    }

    public static void get(String remote){
        System.out.println("Retrieving file " + remote + "...");
        // TODO: implement
    }

    public static void cd(String directory){
        try {
            System.out.println("--> CWD " + directory);
            out.print("CWD " + directory + "\r\n");
            out.flush();
            String line = in.readLine();
            System.out.println("<-- " + line);
        } catch (IOException e) {
            System.out.println("0xFFFD: Control connection I/O error, closing control connection.");
            closeConnection();
        } catch (Exception e) {
            System.out.println("0xFFFF: Processing error. " + e.getMessage());
        }
    }

    public static void quit(){
        try {
            System.out.println("--> QUIT");
            out.print("QUIT\r\n");
            out.flush();
            System.out.println("<-- " + in.readLine());
            closeConnection();
        } catch (IOException e) {
            System.out.println("0xFFFD: Control connection I/O error, closing control connection.");
            closeConnection();
        } catch (Exception e) {
            System.out.println("0xFFFF: Processing error. " + e.getMessage());
        }
    }

    public static void features(){
        try {
            System.out.println("--> FEAT");
            out.print("FEAT\r\n");
            out.flush();
            String line = in.readLine();
            System.out.println("<-- " + line);
            while ((line = in.readLine()) != null){
                if (line.equals("211 End FEAT.")) {
                    System.out.println("    " + line);
                    break;
                }
                System.out.println("   " + line);
            }
        } catch (IOException e) {
            System.out.println("0xFFFD: Control connection I/O error, closing control connection.");
            closeConnection();
        } catch (Exception e) {
            System.out.println("0xFFFF: Processing error. " + e.getMessage());
        }
    }

    public static void dir(){
        System.out.println("Retrieving file list...");
        // TODO: implement
    }

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

            ClientSocket = new Socket();
            ClientSocket.connect(new InetSocketAddress(hostname, portNumber), 20000);
            in = new BufferedReader(new InputStreamReader(ClientSocket.getInputStream()));
            out = new PrintWriter(ClientSocket.getOutputStream(), true);
            System.out.println("<-- " + in.readLine());

        } catch (NumberFormatException e) {
            // Ensures port number is an int
            System.out.println("Usage: port number must be an integer");
            System.exit(-1);
        } catch (Exception e) {
            System.out.println("0xFFFC Control connection to " + hostname + " on port " + portNumber + " failed to open");
            System.exit(-1);
        }
    }

    private static void closeConnection() {
        try {
            if (ClientSocket != null) ClientSocket.close();
            if (in != null) in.close();
            if (out != null) out.close();
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        byte cmdString[] = new byte[MAX_LEN];

        // Get command line arguments and connected to FTP
        // If the arguments are invalid or there aren't enough of them
        // then exit.
        openConnection(args);

        try {
            for (int len = 1; len > 0; ) {
                System.out.print("csftp> ");
                len = System.in.read(cmdString);
                if (len <= 0)
                    break;

                if (len > 1 && (char)cmdString[0] != '#'){
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
            System.err.println("998 Input error while reading commands, terminating.");
        }
    }
}
