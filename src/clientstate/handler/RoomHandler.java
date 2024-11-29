package clientstate.handler;

import game.Player;
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
               case MessageTag.SELECT_PLAYER -> {
                    Player player = RoomManager.getInstance().getSlot(reader.nextByte()).getPlayer();
                    client.getPlayer().selectPlayer(player);
               }
               case MessageTag.PING -> {
                    String message = reader.nextString();
                    System.err.println(message);
               }
          }
     }
     @Override
     public void onExit(Client client) {
          RoomManager.getInstance().onPlayerLeave(client.getPlayer());
     }
}
