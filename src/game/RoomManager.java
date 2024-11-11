package game;

import io.Reader;
import io.Writer;
import message.tag.MessageTag;
import serialization.Serializable;

import java.util.ArrayList;

public class RoomManager  {
     private static RoomManager instance = new RoomManager();
     private ArrayList<Slot> slots;
     private RoomManager() {
          slots = new ArrayList<>();
          for(int i=0;i<12;i++) {
               slots.add(new Slot(i));
          }
     }
     public static RoomManager getInstance() {
          return instance;
     }
     public int getIdEmptySlot() {
          for(int i=0;i<slots.size();i++) {
               if(slots.get(i).isEmpty()) {
                    return i;
               }
          }
          return -1;
     }
     public Slot getSlot(int index) {
          return slots.get(index);
     }
     public void onPlayerJoin(Player player) {
          getSlot(player.getId()).setPlayer(player);
          updatePlayer();
     }
     public void onPlayerLeave(Player player) {
          System.out.println(player.getId() + " left the game");
          getSlot(player.getId()).removePlayer();
          updatePlayer();
     }
     public void updatePlayer() {
          for(Slot slot:slots) {
               if(slot.isEmpty()) {
                    sendAll(MessageTag.UPDATE_SLOT_EMPTY, new Serializable() {
                         @Override
                         public void deserialize(Reader reader) {
                         }
                         @Override
                         public Writer serialize() {
                              Writer writer = new Writer(5);
                              writer.writeInt(slot.getId());
                              return writer;
                         }
                    },this.slots);
                    continue;
               }
               PlayerInfo player = slot.getPlayer().getPlayerInfo();
               sendAll(MessageTag.UPDATE_ROOM, player,this.slots);
          }
     }
     private void sendAll(byte[] data,ArrayList<Slot> slots) {
          for (Slot slot : slots) {
               if (slot.isEmpty()) {
                    continue;
               }
               slot.getPlayer().getClient().send(data);
          }
     }
     private void sendAll(byte tag, Serializable message,ArrayList<Slot> slots) {
          byte[] data;
          if(message != null) {
               Writer writer = message.serialize();
               writer.writeTag(tag);
               data = writer.getBuffer();
          }
          else {
               data = new byte[1];
               data[0] = tag;
          }
          sendAll(data,slots);
     }
}
