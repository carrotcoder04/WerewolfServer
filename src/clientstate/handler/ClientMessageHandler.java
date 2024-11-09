package clientstate.handler;

import io.Reader;
import network.client.Client;



public abstract class ClientMessageHandler {
    public abstract void onEnter(Client client) ;
    public abstract void onMessage(Client client, byte tag, Reader reader);
    public abstract void onExit(Client client);

}
