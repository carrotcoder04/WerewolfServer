package network;

import event.Event;
import event.EventListener;
import message.io.MessageReader;
import message.io.MessageWriter;
import message.structure.MessageStructure;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;


public class Client {
    private Socket socket;
    private int id;
    private InputStream in;
    private OutputStream out;
    private EventListener<Client> onDisconnected;

    public Client(Socket socket, int id) {
        try {
            this.socket = socket;
            this.id = id;
            in = socket.getInputStream();
            out = socket.getOutputStream();
            onDisconnected = new EventListener<>();
            CompletableFuture.runAsync(this::readLoop);
        }
        catch (IOException e) {
            e.printStackTrace();
            disconnect();
        }
    }

    private void readLoop() {
        byte[] buffer = new byte[1024];
        while (true) {
            try {
                int len = in.read(buffer);
                byte[] data = Arrays.copyOfRange(buffer, 0, len);
                receiveMessage(data);
            }
            catch (IOException e) {
               // e.printStackTrace();
                disconnect();
                break;
            }
        }
    }
    private void receiveMessage(byte[] data) {
        MessageReader reader = new MessageReader(data);
        receiveMessage(reader);
    }
    private void receiveMessage(MessageReader reader) {

    }
    private void disconnect() {
        try {
            in.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        try {
            out.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        try {
            socket.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        onDisconnected.invoke(this);
    }
    public void addEventOnDisconnected(Event<Client> event) {
        onDisconnected.addEvent(event);
    }
    private void send(byte[] data) {
        try {
            out.write(data);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void send(MessageWriter message) {
        send(message.getBuffer());
    }
    public void send(MessageStructure message) {
        send(message.getWriter());
    }
    public int getId() {
        return id;
    }
}
