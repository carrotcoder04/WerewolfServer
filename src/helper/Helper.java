package helper;

import java.util.concurrent.ThreadLocalRandom;

public class Helper {
    public static int randomRange(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }
    public static int randomInt() {
        return randomRange(Constants.INT_MIN_VALUE,Constants.INT_MAX_VALUE);
    }
}
