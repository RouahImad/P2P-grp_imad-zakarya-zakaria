import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * The main Peer class.
 * It acts as both a server (listening for connections) and a client (initiating connections).
 * It maintains a list of all connected peers (PeerConnection threads).
 */
public class Peer {

    // A thread-safe list to store all active connections to other peers.
    // CopyOnWriteArrayList is good for lists that are read often but modified infrequently.
    private final CopyOnWriteArrayList<PeerConnection> connections = new CopyOnWriteArrayList<>();
    private final String username;
    private final int port;

    public Peer(String username, int port) {
        this.username = username;
        this.port = port;
    }

    /**
     * Starts the peer's server component in a new thread.
     * This thread will listen for incoming connections from other peers.
     */
    public void startServer() {
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(port)) {
                System.out.println("[*] Server started on port " + port + ". Waiting for peers...");
                while (true) {
                    // Accept a new connection
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("[+] New peer connected from " + clientSocket.getRemoteSocketAddress());
                    
                    // Create a new thread to handle this peer's communication
                    PeerConnection connection = new PeerConnection(clientSocket, this);
                    connections.add(connection);
                    connection.start();
                }
            } catch (IOException e) {
                System.err.println("[-] Server error: " + e.getMessage());
            }
        }).start();
    }

    /**
     * Checks if a connection to the given host and port already exists.
     * @param host The host to check.
     * @param port The port to check.
     * @return true if connection exists, false otherwise.
     */
    private boolean isAlreadyConnected(String host, int port) {
        // Normalize localhost variations
        String normalizedHost = host;
        if (host.equals("localhost") || host.equals("0:0:0:0:0:0:0:1")) {
            normalizedHost = "127.0.0.1";
        }
        
        for (PeerConnection connection : connections) {
            String remoteAddr = connection.getRemoteSocketAddress().toString();
            // remoteAddr format is typically "/127.0.0.1:12345"
            if (remoteAddr.contains(normalizedHost + ":" + port) || 
                remoteAddr.contains("/" + normalizedHost + ":" + port)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Connects to another peer given its host and port.
     * This is the "client" part of the peer.
     * @param host The host of the peer to connect to.
     * @param port The port of the peer to connect to.
     */
    public void connectToPeer(String host, int port) {
        // Check if already connected
        if (isAlreadyConnected(host, port)) {
            System.out.println("[!] Already connected to " + host + ":" + port);
            return;
        }
        
        try {
            Socket socket = new Socket(host, port);
            System.out.println("[+] Successfully connected to " + host + ":" + port);
            
            // Create a new thread to handle this new connection
            PeerConnection connection = new PeerConnection(socket, this);
            connections.add(connection);
            connection.start();
        } catch (UnknownHostException e) {
            System.err.println("[-] Unknown host: " + host);
        } catch (IOException e) {
            System.err.println("[-] Could not connect to " + host + ":" + port + " (" + e.getMessage() + ")");
        }
    }

    /**
     * Sends a message to all connected peers.
     * @param message The message to broadcast.
     */
    public void broadcast(String message) {
        // We also want to see our own messages
        System.out.println("[" + username + " (Me)]: " + message);
        for (PeerConnection connection : connections) {
            connection.sendMessage("[" + username + "]: " + message);
        }
    }

    /**
     * Removes a disconnected peer from the active connections list.
     * This is called by the PeerConnection thread when it detects a disconnection.
     * @param connection The connection to remove.
     */
    public void removeConnection(PeerConnection connection) {
        connections.remove(connection);
        System.out.println("[-] Peer disconnected: " + connection.getRemoteSocketAddress());
    }

    /**
     * Main method to run the Peer application.
     */
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java Peer <username> <port>");
            return;
        }

        String username = args[0];
        int port = Integer.parseInt(args[1]);

        Peer peer = new Peer(username, port);
        // Start the server component to listen for other peers
        peer.startServer();

        System.out.println("[*] Welcome, " + username + "! Type 'connect <host>:<port>' to connect to a peer, or type a message to chat.");

        // Read user input from the console
        try (BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in))) {
            String input;
            while ((input = consoleReader.readLine()) != null) {
                if (input.startsWith("connect ")) {
                    // User wants to connect to another peer
                    try {
                        String[] parts = input.substring(8).split(":");
                        String host = parts[0];
                        int peerPort = Integer.parseInt(parts[1]);

                        if(peerPort == port && (host.equals("localhost") || host.equals("127.0.0.1"))){
                            System.err.println("[!] Invalid port, you can't use your own port");
                        }else
                            peer.connectToPeer(host, peerPort);
                    } catch (Exception e) {
                        System.err.println("[!] Invalid command. Use: connect <host>:<port>");
                    }
                } else {
                    // User wants to send a chat message
                    peer.broadcast(input);
                }
            }
        } catch (IOException e) {
            System.err.println("[-] Error reading from console: " + e.getMessage());
        }
    }
}