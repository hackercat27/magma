package ca.hackercat.magma.net;

import ca.hackercat.logging.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class Server implements Runnable {

    private static class ClientConnection {
        ObjectOutputStream toClient;
        ObjectInputStream fromClient;

        Socket socket;

        public ClientConnection(Socket socket) throws IOException {
            this.socket = socket;

            toClient = new ObjectOutputStream(socket.getOutputStream());
            fromClient = new ObjectInputStream(socket.getInputStream());
        }

        int id;

        public void sendObject(Object o) {
            try {
                toClient.writeObject(o);
            } catch (IOException e) {
                LOGGER.error(e);
            }
        }
        public Object getObject() {
            try {
                return fromClient.readObject();
            } catch (IOException | ClassNotFoundException e) {
                LOGGER.error(e);
            }
            return null;
        }
        public boolean hasData() {
            return true;
        }

        public void disconnect() {
            try {
                toClient.close();
                fromClient.close();
                socket.close();
            } catch (IOException e) {
                LOGGER.error(e);
            }
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof ClientConnection c) {
                return c.id == this.id;
            }
            return false;
        }
    }

    private static final Logger LOGGER = Logger.get(Server.class);

    private List<ClientConnection> connections = new ArrayList<>();

    public static final int DEFAULT_PORT = 42069;
    private int port;

    ServerSocket serverSocket;
    Socket socket;

    public Server() {
        this(DEFAULT_PORT);
    }
    public Server(int port) {
        this.port = port;
        new Thread(this, "magma-server").start();
    }

    public void sendData() {

    }

    public byte[] getData() {
        return null;
    }


    @Override
    public void run() {
        LOGGER.log("Starting server.");
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            LOGGER.log(e);
        }

        boolean connected = true;

        while (connected) {

            waitForConnection();

            for (ClientConnection connection : connections) {

                for (int i = 0; connection.hasData() && i < 16; i++) {
                    Object newObject = connection.getObject();

                    if (newObject == null) {
                        LOGGER.warn("newObject == null!");
                    }
                    else {
                        LOGGER.log(newObject);
                    }

                    for (ClientConnection otherConnection : connections) {
                        if (otherConnection.equals(connection)) {
                            continue;
                        }
                        otherConnection.sendObject(newObject);
                    }

                }

            }
        }
    }

    private void waitForConnection() {
        try {
            serverSocket.setSoTimeout(10);
            Socket s = serverSocket.accept();

            LOGGER.log("Accepting connection from " + s.getLocalAddress() + ":" + s.getLocalPort());

            connections.add(new ClientConnection(s));

        } catch (SocketTimeoutException ignored) {
        } catch (IOException e) {
            LOGGER.error(e);
        }

    }
}
