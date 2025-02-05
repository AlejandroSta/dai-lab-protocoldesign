package ch.heig.dai.lab.protocoldesign;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Objects;

import static java.nio.charset.StandardCharsets.*;

public class Server {
    final int SERVER_PORT = 4242;
    final String[] OPERATIONS = {"ADD", "MUL", "SUB", "DIV"};

    public static void main(String[] args) {
        // Create a new server and run it
        Server server = new Server();
        server.run();
    }

    private void run() {
        //Copy of given example on side:
        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            int requests = 0;
            while (true) {
                try (Socket socket = serverSocket.accept();
                     BufferedReader in = new BufferedReader(
                             new InputStreamReader(socket.getInputStream(), UTF_8));
                     BufferedWriter out = new BufferedWriter(
                             new OutputStreamWriter(socket.getOutputStream(), UTF_8))) {
                    //TODO : lecture du paquet et traitement
                    String line;
                    while ((line = in.readLine()) != null) {
                        ++requests;
                        if (requests > 10) {
                            out.write("INVALID 5" + '\n');
                            out.flush();
                            socket.close();
                        }
                        String[] args = line.split(" ");
                        double value1, value2, result = 0;
                        if (args.length != 3) {
                            out.write("INVALID 2" + '\n');
                            out.flush();
                            socket.close();
                        }
                        if (!Arrays.asList(OPERATIONS).contains(args[0])) {
                            out.write("INVALID 1" + '\n');
                            out.flush();
                            socket.close();
                        }
                        if (!(args[1].startsWith("+") || args[1].startsWith("-")) && !(args[2].startsWith("+") || args[2].startsWith("-"))) {
                            out.write("INVALID 3" + '\n');
                            out.flush();
                            socket.close();
                        }
                        try {
                            value1 = Double.parseDouble(args[1]);
                            value2 = Double.parseDouble(args[2]);
                            if (value2 == 0 && Objects.equals(args[0], "DIV")) {
                                out.write("INVALID 4" + '\n');
                                out.flush();
                                socket.close();
                                throw new IOException();
                            }

                            boolean notAnOp = false;
                            if (args[0].equals("ADD") || args[0].equals("MUL") || args[0].equals("SUB") || args[0].equals("DIV")) {
                                result = switch (args[0]) {
                                    case "ADD" -> value1 + value2;
                                    case "MUL" -> value1 * value2;
                                    case "SUB" -> value1 - value2;
                                    case "DIV" -> value1 / value2;
                                    default ->
                                            throw new IllegalStateException("Unexpected value: " + args[0]); //never thrown
                                };
                            } else {
                                notAnOp = true;
                            }

                            if (notAnOp) {
                                out.write("INVALID 1" + '\n');
                            } else {
                                out.write(Double.toString(result) + '\n');
                            }
                            out.flush();

                        } catch (NumberFormatException e) {
                            out.write("INVALID 3" + '\n');
                            out.flush();
                            socket.close();
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Server: socket ex. : " + e);
                }
            }
        } catch (IOException e) {
            System.out.println("Server: server socket ex. : " + e);
        }
    }
}