package ca.hackercat.magma.net;

import ca.hackercat.logging.Logger;
import ca.hackercat.magma.MagmaEngine;
import ca.hackercat.magma.object.Drawable;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Client implements Runnable {

    private static final Logger LOGGER = Logger.get(Client.class);

    public static final int DEFAULT_PORT = Server.DEFAULT_PORT;

    private MagmaEngine engine;

    private Socket socket;

    private OutputStream toServer;
    private InputStream fromServer;

    public Client(MagmaEngine engine, String ip) {
        this(engine, ip, DEFAULT_PORT);
    }

    public Client(MagmaEngine engine, String ip, int port) {
        this.engine = engine;
        connect(ip, port);
        new Thread(this).start();
    }

    private void connect(String ip, int port) {
        if (socket != null && socket.isConnected()) {
            LOGGER.warn("Attempted to connect client when socket is already connected!");
            return;
        }
        try {
            socket = new Socket();
            LOGGER.log("Attempting connection.");
            socket.connect(new InetSocketAddress(ip, port));
            toServer = socket.getOutputStream();
            fromServer = socket.getInputStream();
        } catch (IOException e) {
            LOGGER.log(e);
        }
    }

    public void sendData() {
        if (socket == null || toServer == null || fromServer == null) {
            return;
        }

        List<Drawable> objects = engine.getObjects();

        List<FieldContainer> data = new ArrayList<>();

        for (int i = 0; i < objects.size(); i++) {

            Drawable obj = objects.get(i);

            Field[] fields = obj.getClass().getFields();

            for (Field field : fields) {

                ServerSync ann = field.getAnnotation(ServerSync.class);

                if (ann == null) {
                    continue;
                }

                String action = ann.value();

                if (!action.equals("r")) {
                    continue;
                }

                try {

                    String name = field.getName();
                    Object value = field.get(obj);
                    data.add(new FieldContainer(i, name, value));

                } catch (IllegalAccessException e) {
                    LOGGER.error(e);
                }

            }


        }

        try {

            ObjectOutputStream oos = new ObjectOutputStream(toServer);

            for (FieldContainer fc : data) {
                oos.writeObject(fc);
            }

            oos.close();

        } catch (IOException e) {
            LOGGER.error(e);
        }

    }

    public void getData() {

        List<FieldContainer> data = new ArrayList<>();

        try {

            ObjectInputStream ois = new ObjectInputStream(fromServer);

            Object o = ois.readObject();

            LOGGER.log("Got data " + o);

            ois.close();

        } catch (IOException | ClassNotFoundException e) {
            LOGGER.error(e + " heeeeeeeeeeeeeeeeeee");
        }

    }

    @Override
    public void run() {

        while (socket.isConnected()) {

            sendData();
            getData();

            try {
                Thread.sleep(16);
            }
            catch (InterruptedException e) {
                LOGGER.log(e);
            }

        }

    }
}
