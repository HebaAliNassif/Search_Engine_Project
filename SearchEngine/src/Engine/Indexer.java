package Engine;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

class FieldData {
    public Integer TermFrequency;
}

public class Indexer implements Runnable{
    final private static float spamPercentage = (float) 0.4;
    WebPage webPage;
    public Indexer(WebPage webPage) {
        this.webPage = webPage;
    }

    public static int CountWordTermFreq(Map<String, FieldData> map, String text, int wordImpotence) {
        String[] wordsList = text.split(" ");
        int wordCount = 0;
        for (String word : wordsList)
        {
            if(word.isEmpty())continue;
            FieldData currWordTermFrequency = map.get(word);
            if (currWordTermFrequency == null)
            {
                currWordTermFrequency = new FieldData();
                currWordTermFrequency.TermFrequency = wordImpotence;
            }
            else {
                currWordTermFrequency.TermFrequency += wordImpotence;
            }
            map.put(word, currWordTermFrequency);
            wordCount ++;
        }
        return wordCount;
    }

    private void recursiveDFS(Node node, String tag) {
        if (node instanceof TextNode) {
            String currentNodeText = ((TextNode) node).text().trim();
            if (!currentNodeText.isEmpty())
            {
                webPage.rawWebPage.append(currentNodeText + " ");
            }
        }
        if (node instanceof Element) {
            /*if (!Constants.HTML_TAGS.contains(tag)) {
                return;
            }*/
            for (Node child : node.childNodes()) {
                recursiveDFS(child, ((Element) node).tagName());
            }
        }
    }

    @Override
    public void run() {
        Map<String, FieldData> map = new HashMap<>();

        //Plain Text
        int wordsCount = CountWordTermFreq(map, webPage.htmlText.Plain, 1);
        //Checking for a spam page (done only on the plain text)
        for (Object name : map.keySet())
        {
            //Spam Condition
            if ((((float) map.get(name).TermFrequency) / (float) wordsCount) >= spamPercentage) {
                System.out.println("Spam page with url: " + webPage.url);
                return;
            }
        }

        //Bold Text
        //For a bold text, it is already added to the map with an importance value equals 1 at the previous step.
        //Calling the method again will increase the importance of the word to be total of 2 (1 + 1).
        CountWordTermFreq(map, webPage.htmlText.Bold, 1);

        //Header Text
        CountWordTermFreq(map, webPage.htmlText.Headers, 3);

        //Title Text
        CountWordTermFreq(map, webPage.htmlText.Title, 4);

        //For testing only
        ////////////////////////////////////////////
        // new file object
        LinkedHashMap<String, FieldData> sortedMap = new LinkedHashMap<>();

        map.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(new Comparator<FieldData>() {
                    @Override
                    public int compare(FieldData o1, FieldData o2) {
                        return o2.TermFrequency.compareTo(o1.TermFrequency);
                    }
                }))
                .forEachOrdered(x -> sortedMap.put(x.getKey(), x.getValue()));

        File file = new File(webPage.htmlText.Title);

        BufferedWriter bf = null;

        try {

            // create new BufferedWriter for the output file
            bf = new BufferedWriter(new FileWriter(file));

            // iterate map entries
            for (Map.Entry<String, FieldData> entry :
                    sortedMap.entrySet()) {

                // put key and value separated by a colon
                bf.write(entry.getKey() + ":"
                        + entry.getValue().TermFrequency);

                // new line
                bf.newLine();
            }

            bf.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ////////////////////////////////////////////
    }
}
