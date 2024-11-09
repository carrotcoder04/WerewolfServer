package game;

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
     public int getEmptySlot() {
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
     public void updatePlayer() {
          for(Slot slot:slots) {
               if(slot.isEmpty()) {
                    continue;
               }
               PlayerInfo player = slot.getPlayer().getPlayerInfo();
               sendAll(MessageTag.UPDATE_PLAYER, player,false);
          }
     }
     private void sendAll(byte[] data,boolean isAsync) {
          for(Slot slot : slots) {
               if(slot.isEmpty()) {
                    continue;
               }
               if(isAsync) {
                    slot.getPlayer().getClient().sendAsync(data);
               }
               else {
                    slot.getPlayer().getClient().send(data);
               }
          }
     }
     private void sendAll(byte tag, Serializable message,boolean isAsync) {
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
          sendAll(data,isAsync);
     }
}
