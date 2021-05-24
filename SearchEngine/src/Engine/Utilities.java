package Engine;

import java.util.ArrayList;
import java.util.List;

public class Utilities {

    public static String processString(String str) {

        str = str.toLowerCase()
                .replaceAll("[^\\dA-Za-z ]", " ")
                .replaceAll("\\b(\\d+)\\b", " ")
                .replaceAll("\\s+", " ")
                .replaceAll("\\b(" + Constants.STOP_WORDS + ")\\b\\s+", " ")
                .trim();

        return str;
    }

}
