package Engine;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.tartarus.snowball.SnowballStemmer;
import org.tartarus.snowball.ext.englishStemmer;

import java.io.*;
import java.sql.SQLException;
import java.util.*;

public class Indexer implements Runnable{
    final private static float spamPercentage = (float) 0.4;
    WebPage webPage;
    public static final String DEFAULT_PAGE_TITLE = "default title";

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
                //currWordTermFrequency = new FieldData();
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
    void processText(String text, String tag)
    {
        String words[] = new String(Utilities.processString(text)).split(" ");
        int tagScore = Constants.TAG_TO_SCORE_MAP.getOrDefault(tag, 1);
        for (String word : words) {

            if (word.isEmpty()) {
                continue;
            }
            if (word.length() <= 2 || Constants.STOP_WORDS.contains(word)) {
                continue;
            }
            int pos = webPage.wordsCount++;
            List<Integer> positions = new ArrayList<>();
            positions.add(pos);
            positions = webPage.wordPosMap.putIfAbsent(word, positions);
            if (positions != null) {
                positions.add(pos);
            }
            String stem = Utilities.stemWord(word);
            // Update web page stem map
            FieldData info = webPage.wordsMap.putIfAbsent(stem, new FieldData(1, tagScore));
            if (info != null) {
                info.count++;
                info.score += tagScore;
            }
        }

    }

    private void traverseDOM(Node node, String tag) {
        if (node instanceof TextNode) {

            String currentNodeText = ((TextNode) node).text().trim();
            if (!currentNodeText.isEmpty()) {
                webPage.rawWebPage.append(currentNodeText + " ");
                processText(currentNodeText, tag);
            }
        }
        if (node instanceof Element) {
            if (!Constants.HTML_TAGS.contains(((Element) node).tagName())) {
                return;
            }
            for (Node child : node.childNodes()) {
                traverseDOM(child, ((Element) node).tagName());
            }
        }
    }

    @Override
    public void run() {

        String title = webPage.document.title();
        if (title.isEmpty())
            webPage.Title = DEFAULT_PAGE_TITLE;
        else
            webPage.Title = Utilities.processString(title);

        traverseDOM(webPage.document.body(), "");
        System.out.println(webPage.Title + "  "+ webPage.url +"\n");

        Main.databaseManager.addDocument(webPage);
        Main.databaseManager.addKeywordsInDoc(webPage);

        /*for (Map.Entry<String, FieldData> entry : webPage.wordsMap.entrySet()) {
            System.out.println(entry.getKey() + ":" + entry.getValue().count.toString() +"    "+ entry.getValue().score.toString());
        }
        //Plain Text
        int wordsCount = CountWordTermFreq(webPage.map, webPage.htmlText.Plain, 1);
        //Checking for a spam page (done only on the plain text)
        for (Object name : webPage.map.keySet())
        {
            //Spam Condition
            if ((((float) webPage.map.get(name).TermFrequency) / (float) wordsCount) >= spamPercentage) {
                System.out.println("Spam page with url: " + webPage.url);
                return;
            }
        }
        //System.out.println(webPage.document.body());


        //Bold Text
        //For a bold text, it is already added to the map with an importance value equals 1 at the previous step.
        //Calling the method again will increase the importance of the word to be total of 2 (1 + 1).
        CountWordTermFreq(webPage.map, webPage.htmlText.Bold, 1);

        //Header Text
        CountWordTermFreq(webPage.map, webPage.htmlText.Headers, 3);

        //Title Text
        CountWordTermFreq(webPage.map, webPage.htmlText.Title, 4);
        */
        //For testing only
        ////////////////////////////////////////////
        // new file object
        /*
        LinkedHashMap<String, FieldData> sortedMap = new LinkedHashMap<>();

        webPage.wordsMap.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(new Comparator<FieldData>() {
                    @Override
                    public int compare(FieldData o1, FieldData o2) {
                        return o2.score.compareTo(o1.score);
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
                        + entry.getValue().count +"\t" + entry.getValue().score);

                // new line
                bf.newLine();
            }

            bf.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        ////////////////////////////////////////////
    }
}
