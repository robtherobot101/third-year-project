package seng302.Logic;

import seng302.Config.DatabaseConfiguration;

import java.sql.*;

public class SqlSanitation {

    /**
     * Checks if the query has any illegal arguments in it and returns the corresponding string statement.
     * @param sqlCommand The query to sanitize.
     * @return Returns a String statement that explains the problems with the query or an empty string if the query is okay.
     */
    public String sanitizeSqlString(String sqlCommand) {
        String printStream = "";
        if(sqlCommand.toLowerCase().contains("delete")){
            printStream = "You do not have permission to delete from the Database.";
        } else if (sqlCommand.toLowerCase().contains("update")) {
            printStream = "You do not have permission to update in the Database.";
        } else if (sqlCommand.toLowerCase().contains("create")) {
            printStream = "You do not have permission to create in the Database.";
        } else if (sqlCommand.toLowerCase().contains("password")) {
            printStream = "You do not have permission to view the passwords of users in the Database.";
        } else if (sqlCommand.toLowerCase().contains("drop")) {
            printStream = "You do not have permission to drop in the Database.";
        } else if (sqlCommand.toLowerCase().contains("insert")) {
            printStream = "You do not have permission to insert into the Database.";
        } else if (sqlCommand.toLowerCase().contains("alter")) {
            printStream = "You do not have permission to alter the Database.";
        }
        return printStream;
    }

    /**
     * Prints a result set from a sql query to be printed as a table.
     *
     * @param rs The result set to be printed in a table.
     * @return Returns a PrintStream of the table to be printed.
     */
    private String createTable(ResultSet rs) {
        String tableString;
        StringBuilder table = new StringBuilder();
        try {
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnsNumber = rsmd.getColumnCount();
//            for (int i = 1; i <= columnsNumber; i++){
//                table.append(rsmd.getColumnName(i));
//                table.append(", ");
//            }
//            table.append("\n");
            while (rs.next()) {
                for (int i = 1; i <= columnsNumber; i++) {
                    String columnValue = rs.getString(i);
                    table.append(rsmd.getColumnName(i));
                    table.append(": ");
                    table.append(columnValue);
                    table.append(", ");
                }
                table.append("\n");
            }
        } catch (SQLException e){
            tableString = e.toString();
            return tableString;
        }
        tableString = table.toString();
        return tableString;
    }

    /**
     * Executes a query and displays it in a table.
     * @param query The query to execute.
     * @return Returns a string table of the results.
     */
    public CommandLineResponse executeQuery(String query) {
            try (Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
                PreparedStatement statement = connection.prepareStatement(query);
                ResultSet resultSet = statement.executeQuery();
                return new CommandLineResponse(true, createTable(resultSet));
            } catch (SQLException e) {
                return new CommandLineResponse(false, e.getMessage());
            }
    }
}
