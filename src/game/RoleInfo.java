package game;


import io.Reader;
import io.Writer;
import serialization.Serializable;

public enum RoleInfo {
     SEER("Tiên Tri", Team.VILLAGER, Aura.GOOD, Description.SEER), MAYOR("Thị Trưởng", Team.VILLAGER, Aura.GOOD, Description.MAYOR), MEDIUM("Thầy Đồng", Team.VILLAGER, Aura.UNKNOWN, Description.MEDIUM), PRIEST("Mục Sư", Team.VILLAGER, Aura.GOOD, Description.PRIEST), JAILER("Quản Ngục", Team.VILLAGER, Aura.GOOD, Description.JAILER), WITCH("Phù Thủy", Team.VILLAGER, Aura.UNKNOWN, Description.WITCH), DOCTOR("Bác Sĩ", Team.VILLAGER, Aura.GOOD, Description.DOCTOR), LOUD_MOUTH("Cậu Bé Miệng Bự", Team.VILLAGER, Aura.GOOD, Description.LOUD_MOUTH), WEREWOLF("Ma Sói", Team.WOLF, Aura.EVIL, Description.WEREWOLF), SHADOW_WOLF("Sói Hắc Ám", Team.WOLF, Aura.EVIL, Description.SHADOW_WOLF), JUNIOR_WEREWOLF("Sói Trẻ", Team.WOLF, Aura.EVIL, Description.JUNIOR_WEREWOLF), WOLF_SEER("Sói Tiên Tri", Team.WOLF, Aura.EVIL, Description.WOLF_SEER), FOOL("Thằng Ngố", Team.SOLO, Aura.UNKNOWN, Description.FOOL), CORRUPTOR("Tin Tặc", Team.SOLO, Aura.UNKNOWN, Description.CORRUPTOR), ARSONIST("Kẻ Phóng Hỏa", Team.SOLO, Aura.UNKNOWN, Description.ARSONIST);
     private final String name;
     private final Team team;
     private final Aura aura;
     private final String description;

     RoleInfo(String name, Team team, Aura aura, String description) {
          this.name = name;
          this.team = team;
          this.aura = aura;
          this.description = description;
     }

     public String getName() {
          return name;
     }

     public Team getTeam() {
          return team;
     }

     public Aura getAura() {
          return aura;
     }

     public String getDescription() {
          return description;
     }

     @Override
     public String toString() {
          return name + ", " + team + ", " + aura + ", " + description;
     }
}