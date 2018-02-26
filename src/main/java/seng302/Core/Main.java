package seng302.Core;

import seng302.TUI.CommandLineInterface;

import java.time.LocalDate;
import java.util.ArrayList;

public class Main {
    private static long nextDonorId = -1;
    public static ArrayList<Donor> donors = new ArrayList<>();

    public static long getNextDonorId(boolean increment) {
    	if (increment) {
			nextDonorId++;
		}
    	return nextDonorId;
	}

    public static void main(String[] args) {
		donors.add(new Donor("testdude1 test", "01/02/0345", "02/03/0456", "male", 12.1, 111.45, "o+", "abc street 1235"));
		donors.add(new Donor("testdude2 test", "01/04/0345", "02/01/0456", "female", 1.234, 1.11111, "a-", "street sample text"));
		donors.add(new Donor("low-info", LocalDate.parse("12/04/0345", Donor.dateFormat)));
		donors.add(new Donor("testdude3 test", "01/05/0345", "09/03/0456", "other", 0.1, 12.4, "b-", "sdfghjkwor geirngoernignoe"));
		CommandLineInterface commandLineInterface = new CommandLineInterface();
        commandLineInterface.run();
    }
}
