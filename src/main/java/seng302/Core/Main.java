package seng302.Core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
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
	 * Find a specific donor from the donor list based on their id.
	 * @param id The id of the donor to search for
	 * @return The donor object or null if the donor was not found
	 */
	public static Donor getDonorById(long id) {
		if (id < 0) {
			return null;
		}
		Donor found = null;
		for (Donor donor: donors) {
			if (donor.getId() == id) {
				found = donor;
				break;
			}
		}
		return found;
	}

	/**
	 * Find a specific donor from the donor list based on their name.
	 * @param names The names of the donor to search for
	 * @return The donor objects that matched the input names
	 */
	public static ArrayList<Donor> getDonorByName(String[] names) {
		ArrayList<Donor> found = new ArrayList<>();
		if (names.length == 0) {
			return donors;
		}
		int matched;
		for (Donor donor: donors) {
			matched = 0;
			for (String name: donor.getNameArray()) {
				if (name.toLowerCase().contains(names[matched].toLowerCase()))  {
					matched++;
					if (matched == names.length) {
						break;
					}
				}
			}
			if (matched == names.length) {
				found.add(donor);
			}
		}
		return found;
	}

    /**
     * Save the donor list to a json file.
     */
	public static boolean saveDonors(String filename) {
		PrintStream outputStream = null;
		boolean success;
		try {
            outputStream = new PrintStream(new FileOutputStream(filename));
            Gson gson = new GsonBuilder().create();
            gson.toJson(donors, outputStream);
            success = true;
        } catch (IOException e) {
		    success = false;
        } finally {
		    if (outputStream != null) {
                outputStream.close();
            }
        }
        return success;
	}

    /**
     * Run the command line interface with 4 test donors preloaded.
     * @param args Not used
     */
    public static void main(String[] args) {
        Main.donors.add(new Donor("Andrew,Neil,Davidson", "01/02/1998", "01/11/4000", "male", 12.1, 50.45, "o+", "1235 abc Street"));
        Main.donors.add(new Donor("Test Donor,Testperson", "01/04/1530", "31/01/1565", "other", 1.234, 1.11111, "a-", "street sample text"));
        Main.donors.add(new Donor("Singlename", LocalDate.parse("12/06/1945", Donor.dateFormat)));
        Main.donors.add(new Donor("Donor 2,Person", "01/12/1990", "09/03/2090", "female", 2, 60, "b-", "Sample Address"));
        Main.donors.add(new Donor("a,long,long,name", "01/11/3000", "01/11/4000", "other", 0.1, 12.4, "b-", "Example Address 12345"));
		CommandLineInterface commandLineInterface = new CommandLineInterface();
        commandLineInterface.run();
    }
}
