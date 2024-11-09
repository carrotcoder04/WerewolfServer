package game;

public class Slot {
     private int id;
     private Player player;
     public Slot(int id) {
          this.id = id;
     }

     public Player getPlayer() {
          return player;
     }

     public void setPlayer(Player player) {
          this.player = player;
          player.setSlot(id);
     }
     public void removePlayer() {
          this.player = null;
     }
     public boolean isEmpty() {
          return player == null;
     }
}
