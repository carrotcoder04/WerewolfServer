import config.network.NetworkConfig;
import message.data.TextMessage;
import message.tag.MessageTag;
import network.server.Server;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Server server = new Server(NetworkConfig.PORT);
        server.start();
        Scanner scanner = new Scanner(System.in);
        while(true) {
            String message = scanner.nextLine();
            TextMessage textMessage = new TextMessage(MessageTag.TEXT_MESSAGE,message,null);
            server.broadcastAll(textMessage);
        }
    }
}