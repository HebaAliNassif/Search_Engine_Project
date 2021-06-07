package Engine;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.microsoft.sqlserver.jdbc.SQLServerDriver;

import java.util.logging.Level;
import java.util.logging.Logger;

public class DB {

    private static Connection connection;
    private Statement stmt, stmt2, stmt3;

    public DB() {
        try {

            connection = DriverManager.getConnection(Constants.DB_CONNECTION_STRING);
            stmt = connection.createStatement();
            stmt2 = connection.createStatement();
            stmt3 = connection.createStatement();
            if (connection != null) {
                System.out.println("Connected");
                DatabaseMetaData dm = (DatabaseMetaData) connection.getMetaData();
                System.out.println("Connecting to Database");
                System.out.println("Driver name: " + dm.getDriverName());
                System.out.println("Driver version: " + dm.getDriverVersion());
                System.out.println("Product name: " + dm.getDatabaseProductName());
                System.out.println("Product version: " + dm.getDatabaseProductVersion());
            }

        } catch (SQLException ex) {
            System.out.println("Not Connected");
            ex.printStackTrace();
        } finally {

        }
    }

    public boolean closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return true;
    }

    public void addDocument(WebPage webPage) {
        String singleString = (webPage.rawWebPage.toString()).replaceAll("\"", " ").replaceAll("'", " ");


        String sql = "IF EXISTS(select * from DocumentsTable where doc_url = '" + webPage.url + "')update DocumentsTable set doc_description ='"
                + singleString + "', word_count='" + webPage.wordsCount
                + "', title='" + webPage.Title + "' where doc_url = '" + webPage.url + "' ELSE insert into DocumentsTable values('" + webPage.url + "','" + singleString + "' ,'"
                + webPage.wordsCount + "','"
                + webPage.Title + "')";

        try {
            stmt.executeUpdate(sql);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void addKeywordsInDoc(WebPage webPage) {
        for (Map.Entry<String, FieldData> entry : webPage.wordsMap.entrySet()) {


            String sql = "IF EXISTS(select * from KeywordsInDocTable where keyword = '" + entry.getKey() + "' and doc_url='" + webPage.url + "') update KeywordsInDocTable set term_freq ='"
                    + entry.getValue().count + "', score='" + entry.getValue().score + "' where keyword = '" + entry.getKey() + "' and doc_url='" + webPage.url + "' ELSE insert into KeywordsInDocTable values('" + entry.getKey() + "','" + webPage.url + "' ,'" +
                    entry.getValue().count + "' ,'" + entry.getValue().score + "')";
            try {
                stmt.executeUpdate(sql);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    public void countKeyword() {
        try {
            String sql = "Select count(*)  AS total  from DocumentsTable";

            ResultSet rs = stmt.executeQuery(sql);
            int doc_count = 0;
            while (rs.next()) {
                doc_count = rs.getInt("total");
            }

            sql = "Select DISTINCT keyword from KeywordsInDocTable";
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                String str = rs.getString("keyword");
                sql = "Select count(*) AS total from KeywordsInDocTable where keyword = '" + str + "'";
                ResultSet rs_2 = stmt2.executeQuery(sql);
                int count = 0;
                while (rs_2.next()) {
                    count = rs_2.getInt("total");
                }
                int idf = (int) doc_count / count;


                sql = "IF EXISTS(select * from Keywords where keyword = '" + str + "') update Keywords set idf='" + idf + "'  where keyword = '" + str + "' ELSE insert into Keywords values('" + str + "' ,'" + idf + "')";
                stmt3.executeUpdate(sql);

            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();

        }
    }

    public List<String> getDocsContainsKeyword(String keyword) {
        List<String> Result = new ArrayList<String>();

        String sql = "Select doc_url from KeywordsInDocTable where keyword = '" + keyword + "'ORDER BY score DESC, term_freq DESC";
        ResultSet rs = null;
        try {
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                String str = rs.getString("doc_url");
                Result.add(str);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return Result;
    }

    public String getDocsDescription(String url) {
        String Result = "";

        String sql = "Select doc_description from DocumentsTable where doc_url = '" + url + "'";
        ResultSet rs = null;
        try {
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Result = rs.getString("doc_description");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return Result;
    }


}