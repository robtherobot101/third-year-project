package seng302.Generic;

import java.sql.*;

public class Database {

    private static String testDatabase = "seng302-2018-team300-test";
    private static String username = "seng302-team300";
    private static String password = "WeldonAside5766";
    private static String url = "jdbc:mysql://mysql2.csse.canterbury.ac.nz/";
    private static String jdbcDriver = "com.mysql.cj.jdbc.Driver";

    public static void main(String args[]) {
        try{
            Class.forName(jdbcDriver);
            Connection con= DriverManager.getConnection(
                    url + testDatabase, username, password);
            System.out.println("Connected to database");
            Statement stmt=con.createStatement();
            ResultSet rs=stmt.executeQuery("select * from USER");
            System.out.println("Users:");

            while(rs.next())
                System.out.println(rs.getString(1));
            con.close();
        }catch(Exception e){
            System.out.println(e);}
    }
}