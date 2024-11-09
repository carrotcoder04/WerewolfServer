package game;

import network.client.Client;

public class Player {
     private int id;
     private String name;
     private final Avatar avatar;
     private Role role;
     private boolean isAlive;
     private boolean isProtected;
     private Client client;
     public Player(int id,String name,Avatar avatar,Client client) {
          this.id = id;
          this.name = name;
          this.avatar = avatar;
          this.client = client;
          this.isAlive = true;
          this.isProtected = false;
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
     public void setProtected(boolean isProtected) {
          this.isProtected = isProtected;
     }

     public boolean isAlive() {
          return isAlive;
     }
     public void setAlive(boolean alive) {
          isAlive = alive;
     }
     public void takeDamage() {
          if(isProtected) {
               isProtected = false;
          }
          else {
               isAlive = false;
          }
     }
}