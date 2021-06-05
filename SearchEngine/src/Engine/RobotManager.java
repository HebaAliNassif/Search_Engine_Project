package Engine;


import crawlercommons.robots.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.http.HttpClient;
import java.net.URL;
import java.util.*;


public class RobotManager {

    RobotManager() {
    }

    static public HashMap<String, ArrayList<String>> Allowed = new HashMap<String, ArrayList<String>>();
    static public HashMap<String, ArrayList<String>> Disallowed = new HashMap<String, ArrayList<String>>();

    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:88.0) Gecko/20100101 Firefox/88.0";

    public URL GetUrlRobotstxt(String URL) throws IOException {
        URL url_temp = new URL(URL);
        String FinalURL = new String();
        FinalURL = url_temp.getProtocol() + "://" + url_temp.getHost() + "/robots.txt" ;
        return (new URL(FinalURL));
    }

    public boolean RobotSafe(String URL) throws IOException {
        URL RobotURL = null;
        URL UrlToCheck = null;
        try {
            /*if(!URL.contains("https://"))
            URL= "https://" + URL ;*/
            UrlToCheck = new URL(URL);
            //.println(" url to check " + UrlToCheck);
            String protocol = UrlToCheck.getProtocol();
            if (!protocol.equals("http") && !protocol.equals("https")) {
                return false;
            }
            RobotURL = GetUrlRobotstxt(URL);
           // System.out.println(RobotURL);
       /* } catch (MalformedURLException e) {
            System.out.println(" Invalid Robot URL ");
        }*/


            if (!Allowed.containsKey(RobotURL.getProtocol() + "://" + RobotURL.getHost()) && !Disallowed.containsKey(RobotURL.getProtocol() + "://" + RobotURL.getHost())) {

                if (GetRules(RobotURL)) {
                    return true;
                }
            }

            String file = UrlToCheck.getFile(); // gets the directory we are searching for
            try {
                for (String s : Allowed.get(RobotURL.getProtocol() + "://" + RobotURL.getHost())) {
                    if ((file.compareToIgnoreCase(s) == 0)) {
                        System.out.println(" due to " + s + "  your URL is allowed !!");
                        return true;
                    }
                }

                for (String s : Disallowed.get(RobotURL.getProtocol() + "://" + RobotURL.getHost())) {
                    if (file.startsWith(s)) {
                        System.out.println(" due to " + s + "  your URL is disallowed !!");
                        return false;
                    }
                }
            } catch (NullPointerException ex) {
                return false;
            }
        } catch (MalformedURLException e) {
            System.out.println(" Invalid Robot URL ");
            return false;
        }


        return true;
    }

    public boolean GetRules(URL RobotURL) throws IOException {
        HttpURLConnection connection;


        try {
            System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");
            connection = (HttpURLConnection) RobotURL.openConnection();
            connection.addRequestProperty("User-Agent", "Mozilla/5.0");
        } catch (IOException e) {
            System.out.println("connection error!");
            return false;
        }

        try {

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String RobotTxt = new String();

        ArrayList<String> disallowed = new ArrayList<String>();
        ArrayList<String> allowed = new ArrayList<String>();

        while ((RobotTxt = reader.readLine()) != null) {
            if (RobotTxt.equals("User-agent: *")) ;
            break;
        }

        while ((RobotTxt = reader.readLine()) != null) {
            if (!RobotTxt.startsWith("Disallow:") && !RobotTxt.startsWith("Allow:"))
                break;
        try {
            String[] splited = RobotTxt.split(" ");

            if (splited[0].equals("Disallow:")) {
                disallowed.add(splited[1]);
            } else if (splited[0].equals("Allow:")) {
                allowed.add(splited[1]);
            }
        } catch(ArrayIndexOutOfBoundsException  e) {
            System.out.println(" error in robots file line , resuming... ");

        }

        }

        //System.out.println(RobotURL.getProtocol() + "://" + RobotURL.getHost());
        reader.close();
        Allowed.put(RobotURL.getProtocol() + "://" + RobotURL.getHost(), allowed);
        Disallowed.put(RobotURL.getProtocol() + "://" + RobotURL.getHost(), disallowed);
        allowed = null;
        disallowed = null;
        } catch(IOException e) {
            System.out.println(" File Not found , resuming... ");
            return false;
        }
        return true;
    }


    public static void main(String[] args) throws IOException {
        RobotManager r = new RobotManager();
        boolean x = r.GetRules(r.GetUrlRobotstxt("https://www.souq.com"));

        Iterator hmIterator = r.Disallowed.entrySet().iterator();

        while (hmIterator.hasNext()) {
            Map.Entry mapElement = (Map.Entry) hmIterator.next();
            System.out.print(mapElement.getKey() + " : ");
            List<String> allow = new ArrayList<>();
            allow = (List<String>) mapElement.getValue();
            for (int i = 0; i < allow.size(); i++) {
                System.out.print(allow.get(i) + " ");
            }

            System.out.println();
        }

        boolean safe = r.RobotSafe("https://www.google.com/sdchs");
        System.out.println(safe);

    }


}
