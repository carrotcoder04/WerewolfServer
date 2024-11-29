package clientstate.handler;

import game.RoomManager;
import io.Reader;
import message.tag.MessageTag;
import network.client.Client;

import java.text.MessageFormat;

public class RoomHandler extends ClientMessageHandler {

     @Override
     public void onEnter(Client client) {
          RoomManager.getInstance().onPlayerJoin(client.getPlayer());
     }

     @Override
     public void onMessage(Client client, byte tag, Reader reader) {
          switch (tag) {
               case MessageTag.CHAT -> {
                    RoomManager.getInstance().playerChat(client.getPlayer(),reader.nextString());
               }
          }
     }
     @Override
     public void onExit(Client client) {
          RoomManager.getInstance().onPlayerLeave(client.getPlayer());
     }
}
