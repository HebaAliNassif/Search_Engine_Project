package Engine;

import java.util.*;

public class QueryProcessor {
    public static Set<String> Pages = new HashSet<String>();
    public static Set<String> Descriptions = new HashSet<String>();
    public void Process(String query)
    {
        query = Utilities.processString(query);
        String[] words = query.split(" ");
        ArrayList<LinkedList<QueryResultInfo>> result = new ArrayList<>(words.length);
        if(query.startsWith("\"") && query.endsWith("\"")) {

        }
        else {
            for (String word : words) {
                Pages.addAll(Main.databaseManager.getDocsContainsKeyword(word));
            }
            for (String page : Pages) {
                String description = Main.databaseManager.getDocsDescription(page);
                Descriptions.add(description);
            }

        }


    }
    public class QueryResultInfo {
    }
}
