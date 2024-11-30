package game;


public class Role  {
     private RoleInfo info;
     public Role(RoleInfo info){
          this.setInfo(info);
     }
     private void setInfo(RoleInfo info){
          this.info = info;
     }
     public RoleInfo getInfo() {
          return this.info;
     }
     public void onGameStateChanged(Player player,GameState state){

     }
     public void onSelectPlayer(Player main,Player target,GameState state) {
          if(main == target) {
               return;
          }
          if(!main.canVote()) {
               System.out.println(main.getId() + " can't vote");
               return;
          }
          if(!main.isAlive()) {
               System.out.println(main.getId() + " is dead");
               return;
          }
          if(!target.isAlive()) {
               return;
          }
          if(state == GameState.NIGHT) {
               if(main.isSameTeam(target)) {
                    return;
               }
          }
          if(main.isSelecteOldPlayer(target)) {
               main.unvote();
          }
          else if(!main.isVoting()) {
               main.vote(target);
          }
          else {
               main.unvote();
               main.vote(target);
          }
     }
}