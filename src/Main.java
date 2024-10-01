import config.Config;
import message.structure.StringMessage;
import network.Server;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        new Server(Config.PORT);
        Scanner scanner = new Scanner(System.in);
        while(true) {
            String message = scanner.nextLine();
            StringMessage stringMessage = new StringMessage(message);
            Server.getInstance().NotifyAll(stringMessage);
        }
    }
}