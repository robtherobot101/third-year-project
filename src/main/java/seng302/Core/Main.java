package seng302.Core;

import seng302.TUI.CommandLineInterface;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Main class that contains program initialization code and data that must be accessible from multiple parts of the
 * program.
 */
public class Main {
    private static long nextDonorId = -1;
    public static ArrayList<Donor> donors = new ArrayList<>();

    /**
     * Get the unique id number for the next donor or the last id number issued.
     * @param increment Whether to increment the unique id counter before returning the unique id value
     * @return returns either the next unique id number or the last issued id number depending on whether increment
     * was true or false
     */
    public static long getNextDonorId(boolean increment) {
    	if (increment) {
			nextDonorId++;
		}
    	return nextDonorId;
	}

    /**
     * Save the donor list to a json file.
     */
	public static void saveDonors() {

	}

    /**
     * Run the command line interface with 4 test donors preloaded.
     * @param args Not used
     */
    public static void main(String[] args) {
		donors.add(new Donor("testdude1 test", "01/02/0345", "02/03/0456", "male", 12.1, 111.45, "o+", "abc street 1235"));
		donors.add(new Donor("testdude2 test", "01/04/0345", "02/01/0456", "female", 1.234, 1.11111, "a-", "street sample text"));
		donors.add(new Donor("low-info", LocalDate.parse("12/04/0345", Donor.dateFormat)));
		donors.add(new Donor("testdude3 test", "01/05/0345", "09/03/0456", "other", 0.1, 12.4, "b-", "sdfghjkwor geirngoernignoe"));
		CommandLineInterface commandLineInterface = new CommandLineInterface();
        commandLineInterface.run();
    }
}
