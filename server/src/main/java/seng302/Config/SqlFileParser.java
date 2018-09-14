package seng302.Config;

import javax.xml.crypto.Data;
import java.awt.image.DataBuffer;
import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class SqlFileParser {

    /**
     * Runs an sql Statement for a batch of sql statements defined in the filed given by the file input stream.
     *
     * @param connection The connection to the SQL database.
     * @param file The InputStream to the file containing sql statements.
     * @throws SQLException If an error occurs while defining the statement
     * @throws IOException If there is a problem reading the file.
     */
    public static void executeFile(Connection connection, InputStream file) throws SQLException, IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(file));
        try(Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
            System.out.println("Reading SQL File...");
            String line;
            StringBuilder sb = new StringBuilder();

            while ((line = br.readLine()) != null) {
                if (line.length() == 0 || line.startsWith("--")) {
                    continue;
                } else {
                    sb.append(line);
                }

                if (line.trim().endsWith(";")) {
                    statement.addBatch(sb.toString());
                    sb = new StringBuilder();
                }

            }
            br.close();
            statement.executeBatch();
        }
    }
}
