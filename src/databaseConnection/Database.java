package databaseConnection;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Database {
    public static Connection conn;
    static String user = "";
    static String password = "";
    static String database = "";
    static String db_port = "";
    static String host = "";

    static {
        try (InputStream input = new FileInputStream("./properties/db.properties")) {

            Properties prop = new Properties();
            prop.load(input);

            user = prop.getProperty("USER");
            password = prop.getProperty("PASSWORD");
            database = prop.getProperty("DATABASE");
            db_port = prop.getProperty("DB_PORT");
            host = prop.getProperty("HOST");

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        connectDatabase();
    }

    public static void connectDatabase(){

        try {
            String url = String.format("jdbc:mysql://%s:%s/%s", host, db_port, database);
            conn = DriverManager.getConnection(url, user, password);
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
    }

    public static boolean getConnectionStatus() {
        try {
            if(!conn.isClosed()){
                return true;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }

        return false;
    }

    static void printColumnsNames(String[] columns, List<String> rows){
        String tableEdge = "+";
        String columnNames = "|";

        for(String s: columns){
            tableEdge = tableEdge.concat("--------------------+");
            columnNames = columnNames.concat(String.format("%20s|", s));
        }

        System.out.println(tableEdge);
        System.out.println(columnNames);
        System.out.println(tableEdge);

        for(String row : rows) System.out.println(row);

        System.out.println(tableEdge);
    }

    public static void printTable(String query, String[] columnsNames, String tableName){
        List<String> rows = new ArrayList<>(columnsNames.length);

        if(columnsNames[0].equals("")){
            columnsNames = getColumnsNames(tableName).split("[|]");
        }

        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                String row = "|";

                for (String columnName : columnsNames) row = row.concat(String.format("%20s|", rs.getString(columnName.trim())));
                rows.add(row);
            }

            printColumnsNames(columnsNames, rows);

            stmt.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public static String[] getTablesNames(){
        ArrayList<String> tablesList = new ArrayList<>();

        try {
            String query = String.format("show tables");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                tablesList.add(rs.getString("Tables_in_" + database));
            }

            stmt.close();
        }catch (SQLException e) {
            e.printStackTrace();
        }

        return tablesList.toArray(new String[0]);
    }


    public static String getColumnsNames(String table){
        String columns = "";

        try {
            String query = String.format("DESCRIBE %s", table);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                columns = columns.concat(String.format(" %1$7s|", rs.getString("Field")));
            }

            stmt.close();
        }catch (SQLException e) {
            e.printStackTrace();
        }

        return columns;
    }

    public static void executeUpdate(String query){
        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(query);
            System.out.println("Update successful");
            stmt.close();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public static String[] getRecords(String table, String column, String conditions){
        ArrayList<String> result = new ArrayList<>();

        try {
            String query = String.format("SELECT %s FROM %s %s", column, table, conditions);
            Statement stmt = conn.createStatement();
            System.out.println(query);
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                result.add(rs.getString(column));
            }

            stmt.close();
        } catch (SQLException e){
            e.printStackTrace();
        }

        return result.toArray(new String[0]);
    }

    public static String[] getPayementsForPDF(){
        ArrayList<String> result = new ArrayList<>();

        try {
            String query = String.format("SELECT payment.value, shop.name AS shop, type.name AS type, date, comment, title " +
                    "FROM payment, shop, type " +
                    "WHERE payment.shop_id = shop.id AND payment.type_id = type.id " +
                    "ORDER BY date");
            Statement stmt = conn.createStatement();
            System.out.println(query);
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                String value = rs.getString("value");;
                String type = rs.getString("type");
                String shop = rs.getString("shop");
                String title = rs.getString("title");
                String comment = rs.getString("comment");
                String date = rs.getString("date");

                result.add(String.format("%12s, %15s, %12s, %25s, %25s, %15s", value, type, shop, title, comment, date));
            }

            stmt.close();
        } catch (SQLException e){
            e.printStackTrace();
        }

        return result.toArray(new String[0]);
    }

    public static ResultSet getPayments(String dataOd, String dataDo){
        ResultSet rs = null;

        try {
            String query = "select p.value, s.name AS shop, t.name AS type, p.date, p.comment, p.title from payment p, shop s, type t WHERE p.shop_id = s.id AND p.type_id = t.id ";
            String condition = String.format("AND date >= \"%s\" AND date <= \"%s\"", dataOd, dataDo);
            rs = conn.createStatement().executeQuery(query + condition);
        }catch (SQLException e){
            e.printStackTrace();
        }

        return rs;
    }

    public static void exit(){
        try {
            conn.close();
        }catch(SQLException e){
            e.printStackTrace();
        }

        System.exit(1);
    }
}
