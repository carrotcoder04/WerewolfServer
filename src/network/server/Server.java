package network.server;

import helper.RandomUtils;
import clientstate.state.ClientState;
import message.data.Message;
import network.client.Client;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    private final int port;
    private ServerSocket serverSocket;
    private ConcurrentHashMap<Integer, Client> clients;
    private static Server instance;
    public static Server getInstance() {
        return instance;
    }
    public Server(int port) {
        this.port = port;
        instance = this;
    }
    public void start() {
        try {
            serverSocket = new ServerSocket(port);
            clients = new ConcurrentHashMap<>();
            CompletableFuture.runAsync(this::acceptClients);
            System.out.println("Server started on port " + port);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void acceptClients() {
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                int id = generateUniqueId();
                Client client = new Client(clientSocket, id);
                onClientConnected(client);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private int generateUniqueId() {
        int id = RandomUtils.randomInt();
        while (clients.containsKey(id)) {
            id = RandomUtils.randomInt();
        }
        return id;
    }
    public void broadcastAll(Message message) {
        for (Client client : clients.values()) {
            client.sendAsync(message);
        }
    }
    private void onClientConnected(Client client) {
        clients.put(client.getId(), client);
        client.addEventOnDisconnected(this::onClientDisconnected);
        client.setClientState(ClientState.CLIENT_CONNECTED);
    }
    public void onClientDisconnected(Client client) {
        System.out.println(client);
        clients.remove(client.getId());
    }
}
