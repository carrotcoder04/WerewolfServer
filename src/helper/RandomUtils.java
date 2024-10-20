package helper;

import java.util.Random;

public class RandomUtils {
    private static final Random rand = new Random();
    public static int randomRange(int min, int max) {
        return rand.nextInt(min,max);
    }
    public static int randomInt() {
        return rand.nextInt();
    }

}
