package game;

import game.role.*;
import helper.RandomUtils;
import io.Reader;
import io.Writer;
import message.tag.MessageTag;
import serialization.Serializable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RoomManager  {
     private static RoomManager instance = new RoomManager();
     private ArrayList<Slot> slots;
     private int playerCount;
     private boolean isGameStarted;

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
          if(isGameStarted) {
               return -1;
          }
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
          playerCount++;
          if(playerCount == 12) {
               try {
                    Thread.sleep(1000);
               }
               catch (InterruptedException e) {
                    throw new RuntimeException(e);
               }
               startGame();
          }
     }
     private void startGame() {
          isGameStarted = true;
          ArrayList<Role> pools = getRolePools();
          Writer writer = new Writer();
          for(int i=0;i<pools.size();i++) {
               writer.writeByte((byte)pools.get(i).getInfo().ordinal());
          }
          sendAll(MessageTag.ALL_ROLES,writer,slots);
          for(int i=0;i<playerCount;i++) {
               int random = RandomUtils.randomRange(0,pools.size());
               Role role = pools.get(random);
               Player player = getSlot(i).getPlayer();
               player.setRole(role);
               pools.remove(random);
               Writer roleWriter = new Writer();
               roleWriter.writeByte((byte)role.getInfo().ordinal());
               player.getClient().send(MessageTag.MY_ROLES,roleWriter);
          }
     }
     private ArrayList<Role> getRolePools() {
          ArrayList<Role> roles = new ArrayList<>();
          roles.add(new Seer());
          roles.add(new Mayor());
          roles.add(new Medium());
          roles.add(new Priest());
          roles.add(new Witch());
          roles.add(new Doctor());
          roles.add(new LoudMounth());
          roles.add(new WereWolf());
          roles.add(new JuniorWereWolf());
          roles.add(new WolfSeer());
          roles.add(new Fool());
          roles.add(new Corruptor());
          return roles;
     }
     public void onPlayerLeave(Player player) {
          System.out.println(player.getId() + " left the game");
          getSlot(player.getId()).removePlayer();
          updatePlayer();
          playerCount--;
     }
     public void updatePlayer() {
          for(Slot slot:slots) {
               if(slot.isEmpty()) {
                    Writer writer = new Writer(5);
                    writer.writeInt(slot.getId());
                    sendAll(MessageTag.UPDATE_SLOT_EMPTY, writer,this.slots);
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
     private void sendAll(byte tag,Writer writer,ArrayList<Slot> slots) {
          writer.writeTag(tag);
          sendAll(writer.getBuffer(),slots);
     }
     private void sendAll(byte tag, Serializable message,ArrayList<Slot> slots) {
          sendAll(tag,message.serialize(),slots);
     }
}
