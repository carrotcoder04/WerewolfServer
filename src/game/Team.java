package game;

public enum Team {
     VILLAGER("Dân làng"),
     WOLF("Sói"),
     SOLO("Solo");
     private String detail;
     Team(String detail) {
          this.detail = detail;
     }
     public String getDetail() {
          return detail;
     }
     @Override
     public String toString() {
          return detail;
     }
}