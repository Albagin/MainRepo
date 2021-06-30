package executables;

import org.tinylog.Logger;
import utility.JsonReader;

public class Test {
    public static void main(String[] args) {
        JsonReader reader = new JsonReader();
        Logger.info(reader.getETA(53.090093, 18.592153, 50.783266, 16.286821));
    }
}
