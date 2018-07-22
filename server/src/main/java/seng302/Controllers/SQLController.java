package seng302.Controllers;

import com.google.gson.Gson;
import seng302.Logic.Database.CustomQuerying;
import seng302.Logic.Database.GeneralClinician;
import seng302.Model.Query;
import spark.Request;
import spark.Response;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class SQLController {

    private CustomQuerying model;

    public SQLController() {
        model = new CustomQuerying();
    }

    public String executeQuery(Request request, Response response) {
        Query query = new Gson().fromJson(request.body(), Query.class);
        if (query == null) {
            response.status(400);
            response.body("Missing Command Body");
            return null;
        } else {
            try {
                ResultSet set = model.executeQuery(query.toString());
                // TODO try to execute the query - Return result
                response.body(createTable(set));
                return null;
            } catch (Exception e) {
                response.status(500);
                response.body("Internal Server Error");
                return null;
            }
        }
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
}
