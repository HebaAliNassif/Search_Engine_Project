package Engine;

import org.jsoup.nodes.Document;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


class FieldData {
    public Integer TermFrequency;
    public Integer score;
    public Integer count;

    public FieldData(Integer score, Integer count) {
        this.score = score;
        this.count = count;
    }


}


public class WebPage {

    public String Title;
    //Web page url
    public String url = null;
    //Web page html document
    public Document document = null;
    //Web page raw text
    public StringBuilder rawWebPage = null;
    //Web page text saving
    public HtmlText htmlText = new HtmlText();
    //Count the words on the document
    public int wordsCount = 0;

    //Positions of each word on the document
    public Map<String, List<Integer>> wordPosMap;
    //Map for all the words in the document
    public Map<String, FieldData> wordsMap;

    public Map<String, FieldData> map = new HashMap<>();

    public WebPage(String url, Document document) {
        this.url = url;
        this.document = document;
        this.rawWebPage = new StringBuilder();
        this.wordsMap = new HashMap<>();
        this.wordPosMap = new HashMap<>();
    }
}

class HtmlText {

    public String Plain;
    public String Headers;
    public String Bold;
    public String Title;
}