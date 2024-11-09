package network.client;

import event.interfaces.Event;
import event.listener.EventListener;
import game.Player;
import io.Reader;
import io.Writer;
import clientstate.state.ClientState;
import clientstate.handler.ClientMessageHandler;
import serialization.Serializable;

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
    private Player player;
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
                disconnect();
                break;
            }
        }
    }
    private void receiveMessage(byte[] data) {
        Reader reader = new Reader(data);
        receiveMessage(reader);
    }
    private void receiveMessage(Reader reader) {
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
    public void send(byte[] data) {
        try {
            System.out.println("tag: " + data[0] + ",len: " + data.length);
            out.write(data);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void sendAsync(byte[] data) {
        CompletableFuture.runAsync(() -> {
           send(data);
        });
    }
    private void send(byte tag,Writer message) {
        message.writeTag(tag);
        send(message.getBuffer());
    }
    public void send(byte tag) {
        byte[] buffer = new byte[1];
        buffer[0] = tag;
        send(buffer);
    }
    public void send(byte tag,Serializable message) {
        send(tag,message.serialize());
    }
    public void sendAsync(byte tag,Serializable message) {
        CompletableFuture.runAsync(() -> {
            send(tag,message);
        });
    }
    public int getId() {
        return id;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setClientState(ClientState clientState) {
        this.clientState = clientState;
        if(stateHandler != null) {
            stateHandler.onExit(this);
        }
        stateHandler = clientState.getClientMessageHandler();
        stateHandler.onEnter(this);
    }
    @Override
    public String toString() {
        return String.format("Client id: %d, Client state: %s", id, clientState);
    }
}
