import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * Handles the communication for a single peer connection.
 * Runs in its own thread to listen for incoming messages
 * and provides a method to send messages.
 */
public class PeerConnection extends Thread {
    
    private final Socket socket;
    private final Peer peer; // Reference to the main Peer object
    private PrintWriter out;
    private BufferedReader in;
    private final SocketAddress remoteSocketAddress;

    public PeerConnection(Socket socket, Peer peer) {
        this.socket = socket;
        this.peer = peer;
        this.remoteSocketAddress = socket.getRemoteSocketAddress();
    }

    @Override
    public void run() {
        try {
            // Initialize input and output streams for this connection
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            String message;
            // Listen for messages from this peer
            while ((message = in.readLine()) != null) {
                // Print the received message to our console
                System.out.println(message);
            }
        } catch (IOException e) {
            // An error (like a disconnect) occurred
            // System.err.println("Connection error: " + e.getMessage());
        } finally {
            // Clean up the connection
            closeConnection();
        }
    }

    /**
     * Sends a message to the peer this thread is handling.
     * @param message The message to send.
     */
    public void sendMessage(String message) {
        if (out != null) {
            out.println(message);
        }
    }

    /**
     * Closes all streams and the socket, and removes this connection
     * from the main peer's list.
     */
    private void closeConnection() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            // Ignore errors on close
        }
        // Notify the main Peer class that this connection is dead
        peer.removeConnection(this);
    }

    /**
     * Gets the remote address of the connected peer.
     * @return The remote socket address.
     */
    public SocketAddress getRemoteSocketAddress() {
        return remoteSocketAddress;
    }
}