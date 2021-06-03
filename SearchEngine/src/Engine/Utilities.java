package Engine;

import org.tartarus.snowball.SnowballStemmer;
import org.tartarus.snowball.ext.englishStemmer;

import java.util.ArrayList;
import java.util.List;

public class Utilities {

    public static String processString(String str) {

        str = str.toLowerCase();
        str = str.replaceAll("[^\\dA-Za-z ]", " ");
        str = str.replaceAll("\\b(\\d+)\\b", " ");
        str = str.replaceAll("\\s+", " ");
        str = str.trim();

        return str;
    }
    public static List<String> removeStopWords(List<String> words) {
        List<String> ret = new ArrayList<>();

        for (String word : words) {
            if (word.length() <= 2 || Constants.STOP_WORDS.contains(word)) {
                continue;
            }

            ret.add(word);
        }

        return ret;
    }

    public static List<String> stemWords(List<String> words) {
        List<String> ret = new ArrayList<>();

        for (String word : words) {
            ret.add(stemWord(word));
        }

        return ret;
    }

    public static String stemWord(String word) {
        SnowballStemmer stemmer = new englishStemmer();

        String lastWord = word;

        while (true) {
            stemmer.setCurrent(word);
            stemmer.stem();
            word = stemmer.getCurrent();

            if (word.equals(lastWord))
                break;

            lastWord = word;
        }

        return word;
    }


}
