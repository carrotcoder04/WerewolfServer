package network;

import helper.Helper;

import javax.xml.transform.Source;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    private ServerSocket serverSocket;
    private ConcurrentHashMap<Integer, Client> clients;

    public Server(int port) {
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
                int id = Helper.randomInt();
                while (clients.containsKey(id)) {
                    id = Helper.randomInt();
                }
                System.out.println("Client " + id + " connected");
                Client client = new Client(clientSocket,id);
                clients.put(id, client);
                client.addEventOnDisconnected(this::clientDisconnected);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void clientDisconnected(Client client) {
        System.out.println("Client " + client.getId() + " disconnected");
        clients.remove(client.getId());
    }
}
