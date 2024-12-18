package game;

import game.role.*;
import helper.RandomUtils;
import io.Writer;
import message.tag.MessageTag;
import serialization.Serializable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class RoomManager  {
     private static RoomManager instance = new RoomManager();
     private ArrayList<Slot> slots;
     private ArrayList<Slot> wereWolfTeam;
     private ArrayList<Slot> deathPlayers;
     private int playerCount;
     private boolean isGameStarted;
     private int maxSlot = 12;
     private int minimumVote = 7;
     private int werewolfAlive = 0;
     private int playerAlive = 0;
     private RoomManager() {
          slots = new ArrayList<>();
          deathPlayers = new ArrayList<>();
          for(int i=0;i<maxSlot;i++) {
               slots.add(new Slot(i));
          }
     }

     public void notification(String message) {
          notification(message,slots);
     }
     public void notification(String message, List<Slot> slots) {
          Writer writer = new Writer();
          writer.writeString(message);
          sendAll(MessageTag.NOTIFICATION,writer,slots);
     }
     public void playerChat(Player player, String message) {
          System.out.println(player.getId() + " " + player.getName() + ": "+message);
          ArrayList<Slot> slots;
          if(!player.isAlive()) {
              slots = new ArrayList<>(deathPlayers);
          }
          else if(GameTime.getInstance().getState() == GameState.NIGHT) {
                slots = new ArrayList<>(wereWolfTeam);
          }
          else {
               slots = new ArrayList<>(this.slots);
          }
          slots.remove(player);
          Writer writer = new Writer();
          writer.writeByte((byte)player.getId());
          writer.writeString(message);
          sendAll(MessageTag.CHAT,writer,slots);
     }

     public static RoomManager getInstance() {
          return instance;
     }
     public int getIdEmptySlot() {
          if(isGameStarted || playerCount == maxSlot) {
               return -1;
          }
          for(int i=0;i<slots.size();i++) {
               if(slots.get(i).isEmpty()) {
                    return i;
               }
          }
          return -1;
     }
     public void addDeathPlayer(Player player) {
          deathPlayers.add(player.getMySlot());
          playerAlive--;
          if(player.getRole().getInfo().getTeam() == Team.WOLF) {
               werewolfAlive--;
          }
          checkGameEnded();
     }
     private void checkGameEnded() {
          if(playerAlive <= 2 * werewolfAlive) {
               GameTime.getInstance().setState(GameState.GAME_ENDED);
               notification("Ma sói chiến thăng.");
          }
          else if(werewolfAlive == 0) {
               GameTime.getInstance().setState(GameState.GAME_ENDED);
               notification("Dân làng chiến thăng.");
          }
     }
     public Slot getSlot(int index) {
          return slots.get(index);
     }
     public void onPlayerJoin(Player player) {
          notification(player.getId() + " " + player.getName() + " đã tham gia.");
          getSlot(player.getId()).setPlayer(player);
          updatePlayer();
          playerCount++;
          if(playerCount == maxSlot) {
               CompletableFuture.runAsync(this::tryStartGame);
          }
     }
     private void tryStartGame() {
          try {
               for(int i=5;i>0;i--) {
                    if(playerCount < maxSlot) {
                         return;
                    }
                    notification("Trò chơi bắt đầu sau: " + i + ".");
                    Thread.sleep(1000);
               }
          }
          catch (InterruptedException e) {
               throw new RuntimeException(e);
          }
          startGame();
     }
     public void onChangeGameState(GameState state) {
          switch (state) {
               case NIGHT -> {
                    notification("Màn đêm buông xuống dân làng đi ngủ.");
                    updateCanVote(state);
                    updateCanChat(state);
               }
               case DAY -> {
                    notification("Trời sáng mời cả làng thức dậy.");
                    updateCanVote(state);
                    updateCanChat(state);
                    if(minimumVote > 1) {
                         minimumVote--;
                    }
               }
               case DISCUSSION -> {

               }
               case VOTE -> {
                    notification("Đến giờ bỏ phiếu, tối thiểu " + minimumVote + " phiếu bầu.");
                    updateCanVote(state);
               }
               case VOTE_COMPLETE -> {
                    closeVoting(this.minimumVote);
                    clearVoting();
               }
               case WOLF_VOTE_COMPLETE -> {
                    closeVoting(0);
                    clearVoting();
               }
          }
          Writer writer = new Writer(5);
          writer.writeByte((byte)state.ordinal());
          sendAll(MessageTag.CHANGE_GAME_STATE,writer,slots);
          for(Slot slot : slots) {
               if(slot.isEmpty()) {
                    continue;
               }
               slot.getPlayer().onGameStateChanged(state);
          }
     }
     private void closeVoting(int minimumVote) {
          int maxNumVotes = minimumVote;
          Player playerVoted = null;
          for(Slot slot : slots) {
               Player player = slot.getPlayer();
               if(player == null) {
                    continue;
               }
               if(player.getNumVotes() > maxNumVotes) {
                    maxNumVotes = player.getNumVotes();
                    playerVoted = player;
               }
          }
          if(playerVoted == null) {
               if(minimumVote == 0) {
                    notification("Không ai bị giết.");
               }
               else {
                    notification("Không ai bị treo cổ.");
               }
          }
          else {
               playerVoted.setAlive(false);
               if(minimumVote == 0) {
                    notification("Ma sói đã cắn chết " +  playerVoted.getId() + " " + playerVoted.getName() + ".");
               }
               else {
                    notification("Dân làng đã treo cổ " +  playerVoted.getId() + " " + playerVoted.getName() + ".");
               }
          }
     }
     private void clearVoting() {
          for(Slot slot : slots) {
               if(slot.isEmpty()) {
                    continue;
               }
               slot.getPlayer().unvote();
          }
     }
     private void updateCanVote(GameState state) {
          switch(state) {
               case NIGHT -> {
                    for(Slot slot : slots) {
                         Player player = slot.getPlayer();
                         if(player == null) {
                              continue;
                         }
                         if(!player.isAlive()) {
                             continue;
                         }
                         if(player.getRole().getInfo().getTeam() == Team.WOLF) {
                              player.setCanVote(true);
                         }
                         else {
                              player.setCanVote(false);
                         }
                    }
               }
               case DAY -> {
                    for(Slot slot : slots) {
                         Player player = slot.getPlayer();
                         if(player == null) {
                              continue;
                         }
                         if(player.isAlive()) {
                              player.setCanVote(false);
                         }
                    }
               }
               case VOTE -> {
                    for(Slot slot : slots) {
                         Player player = slot.getPlayer();
                         if(player == null) {
                              continue;
                         }
                         if(player.isAlive()) {
                              player.setCanVote(true);
                         }
                    }
               }
          }
     }
     private void updateCanChat(GameState state) {
          switch(state) {
               case NIGHT -> {
                    for(Slot slot : slots) {
                         Player player = slot.getPlayer();
                         if(player == null) {
                              continue;
                         }
                         if(player.getRole().getInfo().getTeam() == Team.WOLF) {
                              player.setCanChat(true);
                         }
                         else {
                              player.setCanChat(false);
                         }
                    }
               }
               case DAY -> {
                    for(Slot slot : slots) {
                         Player player = slot.getPlayer();
                         if(player == null) {
                              continue;
                         }
                         player.setCanChat(true);
                    }
               }
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
               player.getClient().send(MessageTag.MY_ROLE,roleWriter);
               playerAlive++;
          }
          wereWolfTeamInit();
          GameTime.getInstance().startGame();
     }
     private void wereWolfTeamInit() {
          wereWolfTeam = new ArrayList<>();
          for(Slot slot : slots) {
               if(!slot.isEmpty()) {
                    if(slot.getPlayer().getRole().getInfo().getTeam() == Team.WOLF) {
                         werewolfAlive++;
                         wereWolfTeam.add(slot);
                    }
               }
          }
          for(Slot slot : wereWolfTeam) {
               roleDisclosure(slot.getPlayer(),wereWolfTeam);
          }
     }
     public void roleDisclosure(Player disclosure,List<Slot> slots) {
          Writer writer = new Writer(5);
          writer.writeByte((byte) disclosure.getId());
          writer.writeByte((byte) disclosure.getRole().getInfo().ordinal());
          sendAll(MessageTag.ROLE_DISCLOSURE,writer,slots);
     }
     private ArrayList<Role> getRolePools() {
          ArrayList<Role> roles = new ArrayList<>();
          roles.add(new Seer());
          roles.add(new Mayor());
          roles.add(new Medium());
          roles.add(new Witch());
          roles.add(new Doctor());
          roles.add(new BodyGuard());
          roles.add(new LoudMounth());
          roles.add(new WereWolf());
          roles.add(new AlphaWereWolf());
          roles.add(new WolfSeer());
          roles.add(new Fool());
          roles.add(new Corruptor());
          return roles;
     }
     public void onPlayerLeave(Player player) {
          System.out.println(player.getId() + " left the game");
          notification(player.getId() + " " + player.getName() + " đã rời game.");
          Role role = player.getRole();
          if(role != null) {
               if(role.getInfo().getTeam() == Team.WOLF) {
                    werewolfAlive--;
               }
               playerAlive--;
               checkGameEnded();
          }
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
     public void sendWolfTeam(byte tag,Writer writer) {
          sendAll(tag,writer,wereWolfTeam);
     }
     private void sendAll(byte[] data,List<Slot> slots) {
          for (Slot slot : slots) {
               if (slot.isEmpty()) {
                    continue;
               }
               try {
                    slot.getPlayer().getClient().send(data);
               }
               catch (Exception ignored) {

               }
          }
     }
     public void sendAll(byte tag,Writer writer) {
          sendAll(tag,writer,slots);
     }
     private void sendAll(byte tag,Writer writer,List<Slot> slots) {
          writer.writeTag(tag);
          sendAll(writer.getBuffer(),slots);
     }
     private void sendAll(byte tag, Serializable message,List<Slot> slots) {
          sendAll(tag,message.serialize(),slots);
     }
}
