import config.Config;
import network.Server;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        new Server(Config.PORT);
        Scanner scanner = new Scanner(System.in);
        while(true) {
            scanner.nextLine();
        }
    }
}