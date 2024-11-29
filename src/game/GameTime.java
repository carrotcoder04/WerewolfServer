package game;

import event.interfaces.Action;
import io.Writer;
import message.tag.MessageTag;

import java.util.concurrent.CompletableFuture;

public class GameTime {
     private static GameTime instance;
     private GameState state;
     private GameTime() {
     }
     public GameState getState() {
          return state;
     }
     public void countDown(String title, int time, Action complete) {
          for (int i = time; i >= 0; i--) {
               Writer writer = new Writer();
               writer.writeString(title + " " + i + "s");
               RoomManager.getInstance().sendAll(MessageTag.COUNT_DOWN, writer);
               try {
                    Thread.sleep(1000);
               }
               catch (InterruptedException e) {
                    throw new RuntimeException(e);
               }
          }
          Writer writer = new Writer(5);
          writer.writeString("");
          RoomManager.getInstance().sendAll(MessageTag.COUNT_DOWN, writer);
          complete.invoke();
     }
     public void setState(GameState state) {
          this.state = state;
          RoomManager.getInstance().onChangeGameState(state);
          switch (state) {
               case NIGHT -> {
                    CompletableFuture<Void> counter = CompletableFuture.runAsync(() -> {
                         try {
                              Thread.sleep(500);
                         }
                         catch (InterruptedException e) {
                              throw new RuntimeException(e);
                         }
                         countDown("Đang say giấc", 20,() -> setState(GameState.WOLF_VOTE_COMPLETE));
                    });
               }
               case DAY -> {
                    setState(GameState.DISCUSSION);
               }
               case DISCUSSION -> {
                    CompletableFuture.runAsync(() -> {
                         try {
                              Thread.sleep(500);
                         }
                         catch (InterruptedException e) {
                              throw new RuntimeException(e);
                         }
                         countDown("Đang thảo luận", 20,() -> setState(GameState.VOTE));
                    });
               }
               case VOTE -> {
                    CompletableFuture.runAsync(() -> {
                         try {
                              Thread.sleep(500);
                         }
                         catch (InterruptedException e) {
                              throw new RuntimeException(e);
                         }
                         countDown("Đang bỏ phiếu", 20,() -> setState(GameState.VOTE_COMPLETE));
                    });
               }
               case VOTE_COMPLETE -> {
                    setState(GameState.NIGHT);
               }
               case WOLF_VOTE_COMPLETE -> {
                    setState(GameState.DAY);
               }
          }
     }
     public static GameTime getInstance() {
          if (instance == null) {
               instance = new GameTime();
          }
          return instance;
     }
     public void startGame() {
          setState(GameState.NIGHT);
     }
}
