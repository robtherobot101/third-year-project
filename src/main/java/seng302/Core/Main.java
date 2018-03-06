package seng302.Core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.lang.reflect.Type;
import java.net.URISyntaxException;

import com.google.gson.reflect.TypeToken;
import jdk.internal.util.xml.impl.Input;
import seng302.TUI.CommandLineInterface;

import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;

import static java.lang.System.in;

/**
 * Main class that contains program initialization code and data that must be accessible from multiple parts of the
 * program.
 */
public class Main {
    private static long nextDonorId = -1;
    public static ArrayList<Donor> donors = new ArrayList<>();
    private static String jarPath;

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
	public static String saveDonors(String filename, boolean relative) {
		PrintStream outputStream = null;
		File outputFile = null;
		boolean success;
		try {
		    if (relative) {
		        outputFile = new File(jarPath + File.separatorChar + filename);
            } else {
                outputFile = new File(filename);
            }
            outputStream = new PrintStream(new FileOutputStream(outputFile));
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
        if (success) {
		    return outputFile.getAbsolutePath();
        } else {
		    return null;
        }
	}

	/**
	 * Imports a JSON object of donor information and adds it to the current donor list.
	 *
	 * TODO:
	 * Make it work around ID nums to avoid repetition. (Maybe add another constructor to accept donor objects dunno if
	 * would work tho, or change ID to be set when added to the arraylist instead of creation, also dunno.
	 * Errors
	 * Add to help/command history
	 * Check works with jar
	 *
	 * @param filename name/location of the file.
	 */
	public static void importDonors(String filename) {
		File inputFile = new File((Main.jarPath + "\\"  + filename));
		Path filePath = inputFile.toPath();
		Type type = new TypeToken<ArrayList<Donor>>() {}.getType();
		try (InputStream in = Files.newInputStream(filePath);
			BufferedReader reader = new BufferedReader(new InputStreamReader(in))){
			Gson gson = new GsonBuilder().create();
			ArrayList<Donor> importedList = gson.fromJson(reader, type);
			System.out.println(importedList);
			Main.donors.addAll(importedList);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

	}

    /**
     * Run the command line interface with 4 test donors preloaded.
     * @param args Not used
     */
    public static void main(String[] args) {
        try {
            jarPath = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getAbsolutePath();
            Main.donors.add(new Donor("Andrew,Neil,Davidson", "01/02/1998", "01/11/4000", "male", 12.1, 50.45, "o+", "1235 abc Street"));
            Main.donors.add(new Donor("Test Donor,Testperson", "01/04/1530", "31/01/1565", "other", 1.234, 1.11111, "a-", "street sample text"));
            Main.donors.add(new Donor("Singlename", LocalDate.parse("12/06/1945", Donor.dateFormat)));
            Main.donors.add(new Donor("Donor 2,Person", "01/12/1990", "09/03/2090", "female", 2, 60, "b-", "Sample Address"));
            Main.donors.add(new Donor("a,long,long,name", "01/11/3000", "01/11/4000", "other", 0.1, 12.4, "b-", "Example Address 12345"));
            CommandLineInterface commandLineInterface = new CommandLineInterface();
            commandLineInterface.run();
        } catch (URISyntaxException e) {
            System.err.println("Unable to read jar path. Please run from a directory with a simpler path. Stack trace:");
            e.printStackTrace();
        }
    }
}
