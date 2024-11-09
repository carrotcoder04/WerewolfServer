package clientstate.state;

import clientstate.handler.ClientInfoHandler;
import clientstate.handler.ClientMessageHandler;
import clientstate.handler.RoomHandler;

public enum ClientState {
    CLIENT_INFO_HANDLER(new ClientInfoHandler()),
    ROOM_HANDLER(new RoomHandler());
    private final ClientMessageHandler clientMessageHandler;
    ClientState(ClientMessageHandler clientMessageHandler) {
        this.clientMessageHandler = clientMessageHandler;
    }
    public ClientMessageHandler getClientMessageHandler() {
        return clientMessageHandler;
    }
}
