package game;

public class Role {
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
}