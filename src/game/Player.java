package game;

import io.Writer;
import message.tag.MessageTag;
import network.client.Client;

public class Player {
     private int id;
     private String name;
     private final Avatar avatar;
     private Role role;
     //GamePlay
     private boolean isAlive;
     private boolean isProtected;
     private boolean canVote;
     private boolean canChat;
     private int numVotes;
     private Player selectPlayer;
     private Client client;

     public Player(int id,String name,Avatar avatar,Client client) {
          this.id = id;
          this.name = name;
          this.avatar = avatar;
          this.client = client;
          this.isAlive = true;
          this.isProtected = false;
          canVote = false;
          canChat = false;
          numVotes = 0;
     }
     public Player(PlayerInfo playerInfo,Client client) {
          this(playerInfo.getId(),playerInfo.getName(),playerInfo.getAvatar(),client);
     }
     public Client getClient() {
          return client;
     }
     public int getId() {
          return id;
     }
     public String getName() {
          return name;
     }
     public void setSlot(int id) {
          try {
               Slot oldSlot = RoomManager.getInstance().getSlot(this.id);
               Slot newSlot = RoomManager.getInstance().getSlot(id);
               if(!newSlot.isEmpty() || oldSlot == newSlot) {
                    return;
               }
               oldSlot.removePlayer();
               this.id = id;
               newSlot.setPlayer(this);
          }
          catch (IndexOutOfBoundsException ignore) {

          }
     }
     public PlayerInfo getPlayerInfo() {
          return new PlayerInfo(id,name,avatar);
     }
     public void setRole(Role role) {
          this.role = role;
     }
     public Role getRole() {
          return role;
     }
     public Avatar getAvatar() {
          return avatar;
     }
     public Slot getMySlot() {
          return RoomManager.getInstance().getSlot(this.id);
     }
     public void setProtected(boolean isProtected) {
          this.isProtected = isProtected;
     }

     public boolean isAlive() {
          return isAlive;
     }
     public void setAlive(boolean alive) {
          isAlive = alive;
          RoomManager.getInstance().addDeathPlayer(this);
          Writer writer = new Writer(5);
          writer.writeByte((byte) id);
          if(isAlive) {
               writer.writeByte((byte) 1);
               setCanVote(false);
               setCanChat(true);
          }
          else {
               writer.writeByte((byte) 0);
          }
          RoomManager.getInstance().sendAll(MessageTag.SET_ALIVE, writer);
     }
     public void takeDamage() {
          if(isProtected) {
               isProtected = false;
          }
          else {
               isAlive = false;
          }
     }

     public void setCanVote(boolean canVote) {
          this.canVote = canVote;
          Writer writer = new Writer(5);
          if(canVote) {
               writer.writeByte((byte) 1);
          }
          else {
               writer.writeByte((byte) 0);
          }
          client.send(MessageTag.SET_CAN_VOTE, writer);
     }
     public boolean canVote() {
          return canVote;
     }
     public void setNumVotes(int numVotes) {
          this.numVotes = numVotes;
     }
     public int getNumVotes() {
          return numVotes;
     }
     public void setCanChat(boolean canChat) {
          this.canChat = canChat;
          Writer writer = new Writer(5);
          if(canChat) {
               writer.writeByte((byte) 1);
          }
          else {
               writer.writeByte((byte) 0);
          }
          client.send(MessageTag.SET_CAN_CHAT, writer);
     }
     public boolean canChat() {
          return canChat;
     }
     public void vote(Player player) {
          player.setNumVotes(player.getNumVotes() + 1);
          Writer writer = new Writer(6);
          writer.writeByte((byte)id);
          writer.writeByte((byte)player.getId());
          writer.writeByte((byte)player.getNumVotes());
          selectPlayer = player;
          if(GameTime.getInstance().getState() == GameState.NIGHT) {
               RoomManager.getInstance().sendWolfTeam(MessageTag.VOTE, writer);
          }
          else {
               RoomManager.getInstance().sendAll(MessageTag.VOTE, writer);
          }
     }
     public void unvote() {
          if(isVoting()) {
               Writer writer = new Writer(6);
               selectPlayer.setNumVotes(selectPlayer.getNumVotes() - 1);
               writer.writeByte((byte)id);
               writer.writeByte((byte)selectPlayer.getId());
               writer.writeByte((byte)selectPlayer.getNumVotes());
               selectPlayer = null;
               GameState gameState = GameTime.getInstance().getState();
               if(gameState == GameState.NIGHT  || gameState == GameState.WOLF_VOTE_COMPLETE) {
                    RoomManager.getInstance().sendWolfTeam(MessageTag.UN_VOTE, writer);
               }
               else {
                    RoomManager.getInstance().sendAll(MessageTag.UN_VOTE, writer);
               }
          }
     }
     public boolean isVoting() {
          return selectPlayer != null;
     }
     public boolean isSameTeam(Player player) {
          return player.getRole().getInfo().getTeam() == role.getInfo().getTeam();
     }
     public void selectPlayer(Player player) {
          if(!canVote) {
               return;
          }
          if(!player.isAlive()) {
              return;
          }
          if(GameTime.getInstance().getState() == GameState.NIGHT) {
               if(isSameTeam(player)) {
                    return;
               }
          }
          if(selectPlayer == player) {
               unvote();
          }
          else if(selectPlayer == null) {
               vote(player);
          }
          else if(selectPlayer != player) {
               unvote();
               vote(player);
          }
     }
}