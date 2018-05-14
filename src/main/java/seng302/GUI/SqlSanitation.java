package seng302.GUI;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class SqlSanitation {

    /**
     * Checks if the query has any illegal arguments in it and returns the corresponding string statement.
     * @param sqlCommand The query to sanitize.
     * @return Returns a String statement that explains the problems with the query or an empty string if the query is okay.
     */
    public static String sanitizeSqlString(String sqlCommand) {
        String printStream = "";
        if(sqlCommand.toLowerCase().contains("delete")){
            printStream = "You do not have permission to delete from the database.";
        } else if (sqlCommand.toLowerCase().contains("update")) {
            printStream = "You do not have permission to update in the database.";
        } else if (sqlCommand.toLowerCase().contains("create")) {
            printStream = "You do not have permission to create in the database.";
        } else if (sqlCommand.toLowerCase().contains("password")) {
            printStream = "You do not have permission to view the passwords of users in the database.";
        } else if (sqlCommand.toLowerCase().contains("drop")) {
            printStream = "You do not have permission to drop in the database.";
        } else if (sqlCommand.toLowerCase().contains("insert")) {
            printStream = "You do not have permission to insert into the database.";
        } else if (sqlCommand.toLowerCase().contains("alter")) {
            printStream = "You do not have permission to alter the database.";
        }
        return printStream;
    }

    /**
     * Prints a result set from a sql query to be printed as a table.
     *
     * @param rs The result set to be printed in a table.
     * @return Returns a PrintStream of the table to be printed.
     */
    protected String printTable(ResultSet rs) {
        String tableString;
        StringBuilder table = new StringBuilder();
        try {
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnsNumber = rsmd.getColumnCount();
            while (rs.next()) {
                for (int i = 1; i <= columnsNumber; i++) {
                    if (i > 1) table.append(",  ");
                    String columnValue = rs.getString(i);
                    table.append(columnValue);
                    table.append(" ");
                    table.append(rsmd.getColumnName(i));
                }
                table.append("\n");
            }
        } catch (SQLException e){
            tableString = "An error occurred.";
            return tableString;
        }
        tableString = table.toString();
        return tableString;
    }
}
