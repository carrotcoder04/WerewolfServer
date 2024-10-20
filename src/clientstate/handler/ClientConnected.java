package clientstate.handler;

import message.io.MessageReader;
import network.client.Client;


public class ClientConnected extends ClientMessageHandler {
    @Override
    public void onEnter(Client client) {
        System.out.println("Client connected: " + client.getId());
    }

    @Override
    public void onMessage(Client client,byte tag,MessageReader reader) {

    }

    @Override
    public void onExit(Client client) {

    }
}
