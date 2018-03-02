package seng302.TUI;

import seng302.Core.*;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.Scanner;

/**
 * This class runs a command line interface (or text user interface), supplying the core functionality to a user through
 * a terminal.
 */
public class CommandLineInterface {
	private Scanner scanner;

	/**
	 * The main loop for the command line interface, which calls specific methods to process each command.
	 */
	public void run() {
		scanner = new Scanner(System.in);
		String[] nextCommand;
		do {
			do {
				System.out.print("$ ");
				nextCommand = scanner.nextLine().split(" ");
			} while (nextCommand.length == 0);
			switch (nextCommand[0]) {
				case "create":
					if (nextCommand.length == 1) {
						createDonor();
					} else {
						System.out.println("The create command does not accept arguments.");
					}
					break;
				case "describe":
					if (nextCommand.length == 2) {
						try {
							describeDonor(Long.parseLong(nextCommand[1]));
						} catch (NumberFormatException e) {
							System.out.println("Please enter a valid ID number.");
						}
					} else {
						System.out.println("The describe command must be used with 1 argument (describe <id>).");
					}
					break;
				case "list":
					if (nextCommand.length == 1) {
						listDonors();
					} else {
						System.out.println("The list command does not accept arguments.");
					}
					break;
				case "set":
					if (nextCommand.length == 4) {
						try {
							setAttribute(Long.parseLong(nextCommand[1]), nextCommand[2], nextCommand[3]);
						} catch (NumberFormatException e) {
							System.out.println("Please enter a valid ID number.");
						}
					} else {
						System.out.println("The set command must be used with 3 arguments (set <id> <attribute> <value>).");
					}
					break;
				case "add":
					if (nextCommand.length == 3) {
						try {
							addOrgan(Long.parseLong(nextCommand[1]), nextCommand[2]);
						} catch (NumberFormatException e) {
							System.out.println("Please enter a valid ID number.");
						}
					} else {
						System.out.println("The add command must be used with 2 arguments (add <id> <organ>).");
					}
					break;
				case "remove":
					if (nextCommand.length == 3) {
						try {
							removeOrgan(Long.parseLong(nextCommand[1]), nextCommand[2]);
						} catch (NumberFormatException e) {
							System.out.println("Please enter a valid ID number.");
						}
					} else {
						System.out.println("The remove command must be used with 2 arguments (remove <id> <organ>).");
					}
					break;
				case "organ_list":
					if (nextCommand.length == 1) {
						listOrgans();
					} else {
						System.out.println("The organ_list command does not accept arguments.");
					}
					break;
				case "donor_organs":
					if (nextCommand.length == 2) {
						try {
							listDonorOrgans(Long.parseLong(nextCommand[1]));
						} catch (NumberFormatException e) {
							System.out.println("Please enter a valid ID number.");
						}
					} else {
						System.out.println("The remove command must be used with 1 arguments (donor_organs <id>).");
					}
					break;
				case "quit":
					break;
				default:
					System.out.println("Input not recognised. Valid commands are: "
						+ "\n\t-create"
						+ "\n\t-describe <id>"
						+ "\n\t-list"
						+ "\n\t-set <id> <attribute> <value>"
						+ "\n\t-add <id> <organ>"
						+ "\n\t-remove <id> <organ>"
						+ "\n\t-organ_list"
						+ "\n\t-donor_organs <id>"
						+ "\n\t-quit");
			}
		} while (!nextCommand[0].equals("quit"));
		scanner.close();
	}

	/**
	 * Lists all donors who have at least 1 organ to donate and their available organs.
     * If none exist, a message is displayed.
	 */
    private void listOrgans() {
        boolean organsAvailable = false;
	    for (Donor donor : Main.donors) {
	        if(!donor.getOrgans().isEmpty()){
	            System.out.println(donor.getName() + ": " + donor.getOrgans());
	            organsAvailable = true;
            }
        }
        if (!organsAvailable){
	        System.out.println("No organs available from any donor!");
        }

    }

    /**
     * Lists a specific donor and their available organs. If they have none a message is displayed.
     * @param id the donors id.
     */
    private void listDonorOrgans(long id) {
	    Donor donor = getDonorById(id);
	    if (donor != null) {
			if (!donor.getOrgans().isEmpty()) {
				System.out.println(donor.getName() + ": " + donor.getOrgans());
			} else {
				System.out.println("No organs available from donor!");
			}
		}
    }

    /**
     * Creates a new donor from user input with a name and date of birth.
     */
    private void createDonor() {
		System.out.print("Enter the new donor's name: ");
		String name = scanner.nextLine();

		LocalDate dateOfBirth = null;
		while (dateOfBirth == null) {
			System.out.print("Enter the new donor's date of birth (dd/mm/yyyy): ");
			try {
				dateOfBirth = LocalDate.parse(scanner.nextLine(), Donor.dateFormat);
			} catch (DateTimeException e) {
				System.out.println("Please enter a valid date in the format dd/mm/yyyy.");
			}
		}

		Main.donors.add(new Donor(name, dateOfBirth));
	}

    /**
     * Displays information about a donor.
     * @param id The id of the donor to describe
     */
	private void describeDonor(long id) {
		Donor toDescribe = getDonorById(id);
		if (toDescribe != null) {
			System.out.println(toDescribe);
		}
	}

    /**
     * Displays a table containing information about all donors.
     */
	private void listDonors() {
		System.out.println(Donor.tableHeader);
		for (Donor donor: Main.donors) {
			System.out.println(donor.getString(true));
		}
	}

    /**
     * Set the value of an attribute of a donor.
     * @param id The id of the donor to set an attribute of
     * @param attribute The attribute to set a new value for
     * @param value The new value for the attribute
     */
	private void setAttribute(long id, String attribute, String value) {
		Donor toSet = getDonorById(id);
		if (toSet == null) {
			return;
		}
		switch (attribute.toLowerCase()) {
			case "dateofdeath":
				try {
					toSet.setDateOfDeath(LocalDate.parse(value, Donor.dateFormat));
                    toSet.setLastModified();
				} catch (DateTimeException e) {
					System.out.println("Please enter a valid date in the format dd/mm/yyyy.");
				}
				break;
			case "gender":
				try {
					toSet.setGender(Gender.parse(value));
                    toSet.setLastModified();
				} catch (IllegalArgumentException e) {
					System.out.println("Please enter gender as other, female, or male.");
				}
				break;
			case "height":
				try {
					double height = Double.parseDouble(value);
					if (height <= 0) {
						System.out.println("Please enter a height which is larger than 0.");
					} else {
						toSet.setHeight(height);
                        toSet.setLastModified();
					}
				} catch (NumberFormatException e) {
					System.out.println("Please enter a numeric height.");
				}
				break;
			case "weight":
				try {
					double weight = Double.parseDouble(value);
					if (weight <= 0) {
						System.out.println("Please enter a weight which is larger than 0.");
					} else {
						toSet.setWeight(weight);
                        toSet.setLastModified();
					}
				} catch (NumberFormatException e) {
					System.out.println("Please enter a numeric weight.");
				}
				break;
			case "bloodtype":
				try {
					toSet.setBloodType(BloodType.parse(value));
                    toSet.setLastModified();
				} catch (IllegalArgumentException e) {
					System.out.println("Please enter blood type as A-, A+, B-, B+, O-, or O+.");
				}
				break;
			case "currentaddress":
				System.out.print("Enter the new donor's address: ");
				toSet.setCurrentAddress(value);
                toSet.setLastModified();
			default:
				System.out.println("Attribute '" + attribute + "' not recognised. Try dateOfDeath, gender, height, " +
						"weight, bloodType, or currentAddress.");
		}

	}

    /**
     * Adds an organ object to a donors list of available organs.
     * @param id the donor giving the organ.
     * @param organ the type of organ being donated.
     */
    private void addOrgan(long id, String organ) {
	    Donor toSet = getDonorById(id);
	    if (toSet == null) {
	        return;
        }
        try {
            toSet.setOrgan(Organ.parse(organ));
            toSet.setLastModified();
        } catch (IllegalArgumentException e) {
            System.out.println("Error in input! Available organs: liver, kidney, pancreas, heart, lung, intestine, " +
			"cornea, middle-ear, skin, bone-marrow, connective-tissue");
        }

    }

    private void removeOrgan(long id, String organ) {
        Donor toSet = getDonorById(id);
        if(toSet == null){
            return;
        }
        try {
            toSet.removeOrgan(Organ.parse(organ));
            toSet.setLastModified();
        } catch (IllegalArgumentException e) {
            System.out.println("Error in input! Available organs: liver, kidney, pancreas, heart, lung, intestine, " +
                    "cornea, middle-ear, skin, bone-marrow, connective-tissue");
        }

    }

    /**
     * Find a specific donor from the main donor list based on their id.
     * @param id The id of the donor to search for
     * @return The donor object or null if the donor was not found
     */
	private Donor getDonorById(long id) {
		if (id < 0) {
			System.out.println("ID numbers start at 0. Please try an ID number 0 or higher.");
			return null;
		}
		Donor found = null;
		for (Donor donor: Main.donors) {
			if (donor.getId() == id) {
				found = donor;
				break;
			}
		}
		if (found == null) {
			System.out.println(String.format("Donor with ID %d not found.", id));
		}
		return found;
	}

}
