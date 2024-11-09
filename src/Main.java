import config.network.NetworkConfig;
import network.server.Server;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Server server = new Server(NetworkConfig.PORT);
        server.start();
        Scanner scanner = new Scanner(System.in);
        while(true) {
            String message = scanner.nextLine();
        }
    }
}