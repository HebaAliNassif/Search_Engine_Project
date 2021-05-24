package Engine;

import org.bson.types.ObjectId;
import org.jsoup.nodes.Document;

public class WebPage {

    public ObjectId id = null;  //Web page id.
    public String url = null;   //Web page url.
    public Document document = null;
    public StringBuilder rawWebPage = null;
    public HtmlText htmlText = new HtmlText();   //Web page url.


    public WebPage(String url, Document document) {
        this.url = url;
        this.document = document;
        this.rawWebPage = new StringBuilder();
    }
}

class HtmlText {

    public String Plain;
    public String Headers;
    public String Bold;
    public String Title;
}