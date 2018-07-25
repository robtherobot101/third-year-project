package seng302.Config;

import javax.xml.crypto.Data;
import java.awt.image.DataBuffer;
import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class SqlFileParser {

    public static Statement parse(Connection connection, InputStream file) throws SQLException, IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(file));
        Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
        System.out.println("Reading SQL File...");
        String line;
        StringBuilder sb = new StringBuilder();

        while( (line=br.readLine())!=null)
        {
            if(line.length()==0 || line.startsWith("--"))
            {
                continue;
            }else
            {
                sb.append(line);
            }

            if(line.trim().endsWith(";"))
            {
                statement.addBatch(sb.toString());
                sb = new StringBuilder();
            }

        }
        br.close();
        return statement;
    }

}
