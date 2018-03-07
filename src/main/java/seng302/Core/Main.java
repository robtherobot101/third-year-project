package seng302.Core;

import com.google.gson.*;

import java.io.*;
import java.lang.reflect.Type;
import java.net.URISyntaxException;

import com.google.gson.reflect.TypeToken;
import seng302.TUI.CommandLineInterface;

import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Main class that contains program initialization code and data that must be accessible from multiple parts of the
 * program.
 */
public class Main {
    private static long nextDonorId = -1;
    public static ArrayList<Donor> donors = new ArrayList<>();
    private static String jarPath;



	/**
     * Class to serialize LocalDates without requiring reflexive access
     */
    private static class LocalDateSerializer implements JsonSerializer<LocalDate> {
        public JsonElement serialize(LocalDate date, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(Donor.dateFormat.format(date));
        }
    }

    /**
     * Class to serialize LocalDateTimes without requiring reflexive access
     */
    private static class LocalDateTimeSerializer implements JsonSerializer<LocalDateTime> {
        public JsonElement serialize(LocalDateTime date, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(Donor.dateTimeFormat.format(date));
        }
    }

    /**
     * Class to deserialize LocalDates without requiring reflexive access
     */
    private static class LocalDateDeserializer implements JsonDeserializer<LocalDate> {
        public LocalDate deserialize(JsonElement date, Type typeOfSrc, JsonDeserializationContext context) {
            return LocalDate.parse(date.toString().replace("\"", ""), Donor.dateFormat);
        }
    }

    /**
     * Class to deserialize LocalDateTimes without requiring reflexive access
     */
    private static class LocalDateTimeDeserializer implements JsonDeserializer<LocalDateTime> {
        public LocalDateTime deserialize(JsonElement date, Type typeOfSrc, JsonDeserializationContext context) {
            return LocalDateTime.parse(date.toString().replace("\"", ""), Donor.dateTimeFormat);
        }
    }

    private static Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateSerializer())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeSerializer())
            .registerTypeAdapter(LocalDate.class, new LocalDateDeserializer())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer()).create();

    public static String getJarPath() {
        return jarPath;
    }

	/**
	 * Only called in testing.
 	 * @param jarPath the jarpath of the app.
	 */
	public static void setJarPath(String jarPath) {
		Main.jarPath = jarPath;
	}

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
     * @param path The path of the file to save to
     * @return Whether the save completed successfully
     */
	public static boolean saveDonors(String path) {
		PrintStream outputStream = null;
		File outputFile;
		boolean success;
		try {
		    outputFile = new File(path);
            outputStream = new PrintStream(new FileOutputStream(outputFile));
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
	 * Imports a JSON object of donor information and replaces the information in the donor list.
	 *
	 * @param path path of the file.
     * @return Whether the command executed successfully
	 */
	public static boolean importDonors(String path) {
		File inputFile = new File(path);
		Path filePath;
		try {
            filePath = inputFile.toPath();
        } catch (InvalidPathException e) {
            return false;
        }
		Type type = new TypeToken<ArrayList<Donor>>() {}.getType();
		// May have to add backup data here in order to undo actions
		// Save to disk in a temp file structure? (And delete on quit)
		// Make copies of the list in arrays?
		// Lot of potential hurdles to discuss here.
		try (InputStream in = Files.newInputStream(filePath);
			BufferedReader reader = new BufferedReader(new InputStreamReader(in))){
			ArrayList<Donor> importedList = gson.fromJson(reader, type);
			System.out.println("Opened file successfully.");
			Main.donors.clear();
			Main.donors.addAll(importedList);
			nextDonorId = -1;
			for (Donor donor: Main.donors) {
			    if (donor.getId() > nextDonorId) {
			        nextDonorId = donor.getId();
                }
            }
			System.out.println("Imported list successfully.");
			return true;
		} catch (IOException e) {
			System.out.println("IOException on " + path + ": Check your inputs and permissions!");
		} catch (JsonSyntaxException | DateTimeException e1) {
		    System.out.println("Invalid syntax in input file.");
		}
		return false;
	}

    /**
     * Run the command line interface with 4 test donors preloaded.
     * @param args Not used
     */
    public static void main(String[] args) {
        try {
            jarPath = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile().getAbsolutePath();
            Main.donors.add(new Donor("Andrew,Neil,Davidson", "01/02/1998", "01/11/4000", "male", 12.1, 50.45, "o+", "Canterbury", "1235 abc Street"));
            Main.donors.add(new Donor("Test Donor,Testperson", "01/04/1530", "31/01/1565", "other", 1.234, 1.11111, "a-", "Auckland", "street sample text"));
            Main.donors.add(new Donor("Singlename", LocalDate.parse("12/06/1945", Donor.dateFormat)));
            Main.donors.add(new Donor("Donor 2,Person", "01/12/1990", "09/03/2090", "female", 2, 60, "b-", "Sample Region", "Sample Address"));
            Main.donors.add(new Donor("a,long,long,name", "01/11/3000", "01/11/4000", "other", 0.1, 12.4, "b-", "Example region", "Example Address 12345"));
            CommandLineInterface commandLineInterface = new CommandLineInterface();
            commandLineInterface.run();
        } catch (URISyntaxException e) {
            System.err.println("Unable to read jar path. Please run from a directory with a simpler path. Stack trace:");
            e.printStackTrace();
        }
    }
}
