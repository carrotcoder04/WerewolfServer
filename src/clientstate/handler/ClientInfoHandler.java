package clientstate.handler;

import clientstate.state.ClientState;
import game.Player;
import game.RoomManager;
import game.PlayerInfo;
import io.Reader;
import message.tag.MessageTag;
import network.client.Client;


public class ClientInfoHandler extends ClientMessageHandler {
    @Override
    public void onEnter(Client client) {
        System.out.println("Client connected: " + client.getId());
    }

    @Override
    public void onMessage(Client client, byte tag, Reader reader) {
        switch (tag) {
            case MessageTag.PLAY:
                int id = RoomManager.getInstance().getIdEmptySlot();
                if(id == -1) {
                    client.send(MessageTag.ROOM_FULL);
                    return;
                }
                PlayerInfo playerInfo = new PlayerInfo(reader);
                playerInfo.setId(id);
                Player player = new Player(playerInfo,client);
                client.setPlayer(player);
                client.send(MessageTag.YOUR_INFO, playerInfo);
                break;
            case MessageTag.JOIN_ROOM:
                client.setClientState(ClientState.ROOM_HANDLER);
                break;
        }
    }

    @Override
    public void onExit(Client client) {

    }
}
