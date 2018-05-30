package seng302.ProfileReader.Users;

import com.opencsv.CSVReader;
import com.opencsv.bean.AbstractCSVToBean;
import seng302.Generic.Debugger;
import seng302.User.User;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UserReaderCSV implements UserReader {
    public List<User> getProfiles(String path) {
        Debugger.log("UserReaderCSV: getProfiles called");

        CSVReader reader = null;
        try {
            reader = new CSVReader(new FileReader(path));
        } catch (FileNotFoundException fnfe) {
            Debugger.log(fnfe);
        }
        int entryCount = 0;
        String [] nextLine;
        try {
            while ((nextLine = reader.readNext()) != null) {
                entryCount++;
                System.out.println(nextLine[0] + " " +nextLine[1] + " " + nextLine[2] + " " +nextLine[3] + " " + nextLine[4]);
            }
            System.out.println(entryCount + " entries");
        } catch (IOException ioe) {
            Debugger.log(ioe);
        }
        List<User> userList = new ArrayList<>();
        return userList;
    }
}
