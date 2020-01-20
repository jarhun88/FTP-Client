
import java.lang.System;
import java.io.*;

//
// This is an implementation of a simplified version of a command 
// line ftp client. The program always takes two arguments
//


public class CSftp {
    static final int MAX_LEN = 255;
    static final int ARG_CNT = 2;

    public static void main(String[] args) {
        byte cmdString[] = new byte[MAX_LEN];

        // Get command line arguments and connected to FTP
        // If the arguments are invalid or there aren't enough of them
        // then exit.

        if (args.length != ARG_CNT) {
            System.out.print("Usage: cmd ServerAddress ServerPort\n");
            return;
        }

        try {
            for (int len = 1; len > 0; ) {
                System.out.print("csftp> ");
                len = System.in.read(cmdString);
                if (len <= 0)
                    break;

                if (len > 1 && (char)cmdString[0] != '#'){
                    String[] commands = (new String(cmdString, 0, len)).trim().split("\\s+");

                    if (commands[0].equals("user")){
                        if (commands.length != 2) System.out.println("0x002 Incorrect number of arguments");
                        else {
                            System.out.println("Client username: " + commands[1]);
                            // TODO: Send username to FTP server
                        }
                    } else if (commands[0].equals("pw")) {
                        if (commands.length != 2) System.out.println("0x002 Incorrect number of arguments");
                        else {
                            System.out.println("Client password: " + commands[1]);
                            // TODO: Send password to FTP server
                        }
                    } else if (commands[0].equals("quit")) {
                        if (commands.length != 1) System.out.println("0x002 Incorrect number of arguments");
                        else {
                            System.out.println("Good bye.");
                            // TODO: close established connection if connected
                            break;
                        }
                    } else if (commands[0].equals("get")) {
                        if (commands.length != 2) System.out.println("0x002 Incorrect number of arguments");
                        else {
                            System.out.println("Getting file: " + commands[1] + "...");
                            // TODO: Establish a data connection, retrieve the file indicated by REMOTE, save it in a
                            // file of the same name on the local machine.
                        }
                    } else if (commands[0].equals("features")) {
                        if (commands.length != 1) System.out.println("0x002 Incorrect number of arguments");
                        else {
                            System.out.println("Retrieving features... ");
                            // TODO: Request set of features/extensions supported by server, print it out
                        }
                    } else if (commands[0].equals("cd")) {
                        if (commands.length != 2) System.out.println("0x002 Incorrect number of arguments");
                        else {
                            System.out.println("Changing cwd to: " + commands[1] + "...");
                            // TODO: Change cwd on server to indicated directory
                        }
                    } else if (commands[0].equals("dir")) {
                        if (commands.length != 1) System.out.println("0x002 Incorrect number of arguments");
                        else {
                            System.out.println("Retrieving files on cwd...");
                            // TODO: Establish data connection, list files in cwd
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
