package clientstate.handler;

import game.RoomManager;
import io.Reader;
import network.client.Client;

public class RoomHandler extends ClientMessageHandler {

     @Override
     public void onEnter(Client client) {
          RoomManager.getInstance().updatePlayer();
     }

     @Override
     public void onMessage(Client client, byte tag, Reader reader) {

     }
     @Override
     public void onExit(Client client) {

     }
}
