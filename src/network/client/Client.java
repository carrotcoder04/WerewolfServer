package network.client;

import event.interfaces.Event;
import event.listener.EventListener;
import game.Player;
import io.Reader;
import io.Writer;
import clientstate.state.ClientState;
import clientstate.handler.ClientMessageHandler;
import serialization.Serializable;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;


public class Client {
    private Socket socket;
    private int id;
    private DataInputStream in;
    private DataOutputStream out;
    private EventListener<Client> onDisconnectedEvents;
    private ClientState clientState;
    private ClientMessageHandler stateHandler;
    private Player player;
    public Client(Socket socket, int id) {
        try {
            this.socket = socket;
            this.id = id;
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
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
                int size = in.readInt();
                byte[] data = new byte[size];
                int len = 0;
                int byteRead = 0;
                while (byteRead < size) {
                    len = in.read(data, byteRead, size - byteRead);
                    if (len > 0) {
                        byteRead += len;
                    }
                    else {
                        throw new IOException();
                    }
                }
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
            out.writeInt(data.length);
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
