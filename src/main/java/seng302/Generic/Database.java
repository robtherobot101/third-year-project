package seng302.Generic;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.*;

public class Database {

    private static String testDatabase = "seng302-2018-team300-test";

    public static void main(String args[]) {
        try{
            System.out.println("Trying to get Driver");
            Class.forName("com.mysql.jdbc.Driver");
            System.out.println("Got Driver");
            Connection con= DriverManager.getConnection(
                    "jdbc:mysql://mysql2.csse.canterbury.ac.nz/" + testDatabase,"seng302-team300","WeldonAside5766");
            System.out.println("Connected to database");
            Statement stmt=con.createStatement();
            ResultSet rs=stmt.executeQuery("select * from USERS");
            System.out.println("Got to Users");
            System.out.println("Users:");

            while(rs.next())
                System.out.println(rs.getString(0));
            con.close();
        }catch(Exception e){
            System.out.println(e);}
    }
}
