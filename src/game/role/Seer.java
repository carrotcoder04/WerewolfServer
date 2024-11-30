package game.role;

import game.GameState;
import game.Player;
import game.Role;
import game.RoleInfo;
import io.Writer;
import message.tag.MessageTag;

public class Seer extends Role {
    private boolean checkable;
    public Seer() {
        super(RoleInfo.SEER);
    }

    @Override
    public void onGameStateChanged(Player player,GameState state){
        if(state == GameState.NIGHT) {
            checkable = true;
        }
        else if(state == GameState.DAY) {
            checkable = false;
        }
    }
    @Override
    public void onSelectPlayer(Player main, Player target, GameState state) {
        if(main == target) {
            return;
        }
        if(!main.isAlive()) {
            return;
        }
        if(state == GameState.NIGHT) {
            if(checkable) {
                checkable = false;
                String info = "Bạn đã soi " + target.getId() + " " + target.getName() + " " + target.getRole().getInfo().getName();
                Writer noti = new Writer();
                noti.writeString(info);
                main.getClient().send(MessageTag.NOTIFICATION, noti);
                Writer roleDisclosure = new Writer(5);
                roleDisclosure.writeByte((byte)target.getId());
                roleDisclosure.writeByte((byte)target.getRole().getInfo().ordinal());
                main.getClient().send(MessageTag.ROLE_DISCLOSURE, roleDisclosure);
            }
            return;
        }
        super.onSelectPlayer(main, target, state);
    }
}
