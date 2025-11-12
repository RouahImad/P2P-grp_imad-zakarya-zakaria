# P2P Chat Application

A simple peer-to-peer (P2P) chat application written in Java. This application allows multiple users to connect directly to each other and exchange messages without requiring a central server.

## Features

- **Decentralized Communication**: Direct peer-to-peer connections
- **Multi-peer Support**: Connect to multiple peers simultaneously
- **Simple Interface**: Command-line based interface
- **Real-time Messaging**: Instant message broadcasting to all connected peers

## Prerequisites

- **Java Development Kit (JDK)** version 8 or higher
  - To check if Java is installed: `java -version`
  - To check if Java compiler is installed: `javac -version`

## Installation and Compilation

1. Clone or download this repository:
   ```bash
   git clone https://github.com/RouahImad/P2P-grp_imad-zakarya-zakaria.git
   cd P2P-grp_imad-zakarya-zakaria
   ```

2. Compile the Java files:
   ```bash
   javac Peer.java PeerConnection.java
   ```

   This will generate `Peer.class` and `PeerConnection.class` files.

## How to Run

### Starting a Peer

Each peer requires two parameters:
- `<username>`: Your display name in the chat
- `<port>`: The port number this peer will listen on (e.g., 5000, 5001, 5002)

**Syntax:**
```bash
java Peer <username> <port>
```

### Example Usage

#### Scenario: Three users chatting together

**Step 1: Start the first peer (Alice)**

Open a terminal and run:
```bash
java Peer Alice 5000
```

You should see:
```
[*] Server started on port 5000. Waiting for peers...
[*] Welcome, Alice! Type 'connect <host>:<port>' to connect to a peer, or type a message to chat.
```

**Step 2: Start the second peer (Bob)**

Open a new terminal and run:
```bash
java Peer Bob 5001
```

You should see:
```
[*] Server started on port 5001. Waiting for peers...
[*] Welcome, Bob! Type 'connect <host>:<port>' to connect to a peer, or type a message to chat.
```

**Step 3: Connect Bob to Alice**

In Bob's terminal, type:
```
connect localhost:5000
```

You should see in Bob's terminal:
```
[+] Successfully connected to localhost:5000
```

And in Alice's terminal:
```
[+] New peer connected from /127.0.0.1:xxxxx
```

**Step 4: Start the third peer (Charlie)**

Open another terminal and run:
```bash
java Peer Charlie 5002
```

**Step 5: Connect Charlie to Alice and Bob**

In Charlie's terminal, type:
```
connect localhost:5000
connect localhost:5001
```

**Step 6: Start chatting!**

- In Alice's terminal, type: `Hello everyone!`
  - Bob and Charlie will see: `[Alice]: Hello everyone!`
  - Alice will see: `[Alice (Me)]: Hello everyone!`

- In Bob's terminal, type: `Hi Alice!`
  - Alice and Charlie will see: `[Bob]: Hi Alice!`
  - Bob will see: `[Bob (Me)]: Hi Alice!`

- In Charlie's terminal, type: `Hey guys!`
  - Alice and Bob will see: `[Charlie]: Hey guys!`
  - Charlie will see: `[Charlie (Me)]: Hey guys!`

## Commands

| Command | Description | Example |
|---------|-------------|---------|
| `connect <host>:<port>` | Connect to another peer | `connect localhost:5000` |
| `<message>` | Send a message to all connected peers | `Hello everyone!` |

## Network Configuration

### Local Network (Same Machine)
Use `localhost` or `127.0.0.1` as the host:
```
connect localhost:5000
```

### Local Network (Different Machines)
Use the IP address of the machine you want to connect to:
```
connect 192.168.1.100:5000
```

To find your IP address:
- **Linux/Mac**: `ifconfig` or `ip addr`
- **Windows**: `ipconfig`

### Firewall Considerations
Make sure the ports you're using are not blocked by your firewall. You may need to configure your firewall to allow incoming connections on the ports you choose.

## Troubleshooting

### "Address already in use"
The port you're trying to use is already occupied. Choose a different port number.

### "Connection refused"
- Make sure the peer you're trying to connect to is running
- Verify you're using the correct host and port
- Check if a firewall is blocking the connection

### "Already connected to <host>:<port>"
You're already connected to that peer. The application prevents duplicate connections.

### "Invalid port, you can't use your own port"
You cannot connect to yourself. Make sure you're using a different peer's port.

## Architecture

The application consists of two main classes:

- **Peer.java**: The main class that handles:
  - Starting a server to listen for incoming connections
  - Connecting to other peers as a client
  - Broadcasting messages to all connected peers
  - Managing the list of active connections

- **PeerConnection.java**: Handles individual peer connections:
  - Runs in a separate thread for each connection
  - Listens for incoming messages
  - Sends messages to the connected peer
  - Cleans up when the connection is closed

## Example Network Topology

```
    Alice (5000)
      /    \
     /      \
Bob (5001) - Charlie (5002)
```

In this topology:
- Alice is connected to Bob and Charlie
- Bob is connected to Alice and Charlie
- Charlie is connected to Alice and Bob

Each peer maintains direct connections and can send messages to all peers they're connected to.

## Contributing

Feel free to submit issues or pull requests to improve this P2P chat application.

## License

This project is available for educational purposes.
