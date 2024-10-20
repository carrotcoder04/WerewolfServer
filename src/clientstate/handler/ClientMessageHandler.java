package clientstate.handler;

import message.io.MessageReader;
import clientstate.state.ClientState;
import network.client.Client;

import java.util.HashMap;
import java.util.Map;


public abstract class ClientMessageHandler {
    private static final Map<ClientState, ClientMessageHandler> stateHandlers;
    static {
        stateHandlers = new HashMap<>();
        stateHandlers.put(ClientState.CLIENT_CONNECTED,new ClientConnected());
    }
    public static ClientMessageHandler getStateHandler(ClientState state) {
        return stateHandlers.get(state);
    }
    public abstract void onEnter(Client client);
    public abstract void onMessage(Client client, byte tag, MessageReader reader);
    public abstract void onExit(Client client);

}
