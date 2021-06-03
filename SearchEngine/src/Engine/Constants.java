package Engine;

import java.util.*;


public class Constants {
    public static final String  DB_SERVER_URL = "search-engine-server.database.windows.net";
    public static final String  DB_USER_NAME = "heba";
    public static final String  DB_Password = "Aya12345";

    public static final String DB_CONNECTION_STRING = "jdbc:sqlserver://search-engine-server.database.windows.net:1433;" +
            "database=SearchEngine;" +
            "user=heba@search-engine-server;" +
            "password={Aya12345};" +
            "encrypt=true;trustServerCertificate=false;" +
            "hostNameInCertificate=*.database.windows.net;" +
            "loginTimeout=30;";

    static final String STOP_WORDS = "i|he|she|an|and|are|as|at|be|by|but|for|from|if|"
            + "in|into|is|it|its|no|not||on|or|of|where|which|how"
            + "such|that|the|their|then|there|these|they|this|to|was|were|will|([a-z])|([a-z][a-z])|([a-z][a-z][a-z])";

    public static final Set<String> HTML_TAGS = new HashSet<>(Arrays.asList(
            "body", "div", "p", "main", "article", "pre",
            "h1", "h2", "h3", "h4", "h5", "h6",
            "b", "i", "em", "blockquote", "strong",
            "a", "span", "ol", "ul", "li"
    ));

    public static final Map<String, Integer> TAG_TO_SCORE_MAP = new HashMap<String, Integer>() {
        {
            put("title", 50);
            put("h1", 35);
            put("h2", 30);
            put("h3", 25);
            put("strong", 15);
            put("em", 15);
            put("b", 15);
            put("h4", 7);
            put("h5", 5);
            put("h6", 5);
            put("i", 5);
        }
    };
}
