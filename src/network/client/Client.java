package network.client;

import event.interfaces.Event;
import event.listener.EventListener;
import message.io.MessageReader;
import message.io.MessageWriter;
import clientstate.state.ClientState;
import clientstate.handler.ClientMessageHandler;
import message.data.Message;

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
    private EventListener<Client> onDisconnectedEvents;
    private ClientState clientState;
    private ClientMessageHandler stateHandler;
    public Client(Socket socket, int id) {
        try {
            this.socket = socket;
            this.id = id;
            in = socket.getInputStream();
            out = socket.getOutputStream();
            onDisconnectedEvents = new EventListener<>();
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
        if(stateHandler != null) {
            byte tag = reader.readTag();
            stateHandler.onMessage(this,tag,reader);
        }
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
        onDisconnectedEvents.invoke(this);
    }
    public void addEventOnDisconnected(Event<Client> event) {
        onDisconnectedEvents.addEvent(event);
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
    public void send(Message message) {
        send(message.getWriter());
    }
    public void sendAsync(Message message) {
        CompletableFuture.runAsync(() -> {
           send(message);
        });
    }
    public int getId() {
        return id;
    }
//    public ClientState getClientState() {
//        return clientState;
//    }
    public void setClientState(ClientState clientState) {
        this.clientState = clientState;
        if(stateHandler != null) {
            stateHandler.onExit(this);
        }
        stateHandler = ClientMessageHandler.getStateHandler(clientState);
        stateHandler.onEnter(this);
    }
    @Override
    public String toString() {
        return String.format("Client id: %d, Client state: %s", id, clientState);
    }
}
