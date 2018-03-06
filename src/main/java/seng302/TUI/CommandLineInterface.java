package seng302.TUI;

import java.util.ArrayList;
import seng302.Core.*;
import seng302.Files.History;

import java.io.PrintStream;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.Scanner;

/**
 * This class runs a command line interface (or text user interface), supplying the core functionality to a user through
 * a terminal.
 */
public class CommandLineInterface {
	private Scanner scanner;
	private PrintStream streamOut;

	/**
	 * The main loop for the command line interface, which calls specific methods to process each command.
	 */
	public void run() {
		scanner = new Scanner(System.in);
		streamOut = History.init();
		boolean success = false;
		String[] nextCommand;
		do {
			do {
				System.out.print("> ");
				nextCommand = scanner.nextLine().split(" ");
			} while (nextCommand.length == 0);
			switch (nextCommand[0].toLowerCase()) {
				case "create":
					if (nextCommand.length == 3) {
						try {
							Main.donors.add(new Donor(nextCommand[1].replace("\"", ""), LocalDate.parse(nextCommand[2], Donor.dateFormat)));
							System.out.println("New donor created early.");
							success = true;
						} catch (DateTimeException e) {
							System.out.println("Please enter a valid date of birth in the format dd/mm/yyyy.");
						}
					} else if (nextCommand.length > 3 && nextCommand[1].contains("\"") && nextCommand[nextCommand.length-2].contains("\"")) {
						String date = nextCommand[nextCommand.length-1];
						nextCommand = String.join(" ", nextCommand).split("\"");
						if (nextCommand.length == 3) {
							try {
								Main.donors.add(new Donor(nextCommand[1], LocalDate.parse(date, Donor.dateFormat)));
								System.out.println("New donor created late.");
								success = true;
							} catch (DateTimeException e) {
								System.out.println("Please enter a valid date of birth in the format dd/mm/yyyy.");
							}
						} else {
							printIncorrectUsageString("create", 2, "\"name part 1,name part 2\" <date of birth>");
						}
					} else {
						printIncorrectUsageString("create", 2, "\"name part 1,name part 2\" <date of birth>");
					}
					break;
				case "describe":
					if (nextCommand.length > 1 && nextCommand[1].contains("\"")) {
						describeDonor(String.join(" ", nextCommand).split("\"")[1]);
					} else if (nextCommand.length == 2) {
						describeDonor(nextCommand[1]);
						success = true;
					} else {
						printIncorrectUsageString("describe", 1, "<id>");
					}
					break;
				case "list":
					if (nextCommand.length == 1) {
						listDonors();
						success = true;
					} else {
						printIncorrectUsageString("list", 0, null);
					}
					break;
				case "set":
					if (nextCommand.length >= 4 && nextCommand[2].toLowerCase().equals("name") && nextCommand[3].contains("\"")) {
						long id = Long.parseLong(nextCommand[1]);
						nextCommand = String.join(" ", nextCommand).split("\"");
						if (nextCommand.length > 1) {
							try {
								setAttribute(id, "name", nextCommand[1]);
								success = true;
							} catch (NumberFormatException e) {
								System.out.println("Please enter a valid ID number.");
							}
						} else {
							System.out.println("Please enter a name.");
						}
					} else if (nextCommand.length == 4) {
						try {
							setAttribute(Long.parseLong(nextCommand[1]), nextCommand[2], nextCommand[3]);
							success = true;
						} catch (NumberFormatException e) {
							System.out.println("Please enter a valid ID number.");
						}
					} else {
						printIncorrectUsageString("set", 3, "<id> <attribute> <value>");
					}
					break;
                case "delete":
                    if (nextCommand.length == 2) {
                        try {
                            deleteDonor(Long.parseLong(nextCommand[1]));
							success = true;
                        } catch (NumberFormatException e) {
                            System.out.println("Please enter a valid ID number.");
                        }
                    } else {
                        printIncorrectUsageString("describe", 1, "<id>");
                    }
                    break;
				case "add":
					if (nextCommand.length == 3) {
						try {
							addOrgan(Long.parseLong(nextCommand[1]), nextCommand[2]);
							success = true;
						} catch (NumberFormatException e) {
							System.out.println("Please enter a valid ID number.");
						}
					} else {
						printIncorrectUsageString("add", 2, "<id> <organ>");
					}
					break;
				case "remove":
					if (nextCommand.length == 3) {
						try {
							removeOrgan(Long.parseLong(nextCommand[1]), nextCommand[2]);
							success = true;
						} catch (NumberFormatException e) {
							System.out.println("Please enter a valid ID number.");
						}
					} else {
						printIncorrectUsageString("remove", 2, "<id> <organ>");
					}
					break;
				case "organ_list":
					if (nextCommand.length == 1) {
						listOrgans();
						success = true;
					} else {
						printIncorrectUsageString("organ_list", 0, null);
					}
					break;
				case "donor_organs":
					if (nextCommand.length == 2) {
						try {
							listDonorOrgans(Long.parseLong(nextCommand[1]));
							success = true;
						} catch (NumberFormatException e) {
							System.out.println("Please enter a valid ID number.");
						}
					} else {
						printIncorrectUsageString("remove", 1, "<id>");
					}
					break;
				case "save":
					if (nextCommand.length >= 2) {
					    boolean relative = nextCommand[1].equals("-r");
					    String path = null;
					    int startLength = relative ? 2 : 1;
                        if (nextCommand.length >= 1 + startLength) {
                            if (nextCommand[startLength].contains("\"")) {
                                path = String.join(" ", nextCommand).split("\"")[1];
                            } else if (nextCommand.length == startLength + 1) {
                                path = nextCommand[startLength];
                            }
                        }
                        if (path != null) {
                            String savePath = Main.saveDonors(path, relative);
                            if (savePath != null) {
                                System.out.println("Donors saved to " + savePath + ".");
                                success = true;
                            } else {
                                System.out.println("Failed to save to " + path + ". Make sure the program has access to this file.");
                            }
                        } else {
                            System.out.println("The save command must be used with 1 or 2 arguments (save -r <filepath> or save <filepath>).");
                        }
					} else {
                        System.out.println("The save command must be used with 1 or 2 arguments (save -r <filepath> or save <filepath>).");
					}
					break;
				case "import":
					Main.importDonors(nextCommand[1]);
					break;
                case "help":
                    if (nextCommand.length == 1) {
                        System.out.println("Valid commands are: "
                            + "\n\t-create \"First Name,name part 2,name part n\" <date of birth>"
                            + "\n\t-describe <id> OR describe \"name substring 1,name substring 2,name substring n\""
                            + "\n\t-list"
                            + "\n\t-set <id> <attribute> <value>"
                            + "\n\t-delete <id>"
                            + "\n\t-add <id> <organ>"
                            + "\n\t-remove <id> <organ>"
                            + "\n\t-organ_list"
                            + "\n\t-donor_organs <id>"
                            + "\n\t-save [-r] <path> OR save [-r] \"File path with spaces\""
                            + "\n\t-help [<command>]"
                            + "\n\t-quit");
                        success = true;
                    } else if (nextCommand.length == 2) {
                    	success = true;
                        switch (nextCommand[1].toLowerCase()) {
                            case "create":
                                System.out.println("This command creates a new donor with a name and date of birth.\n"
                                    + "The syntax is: create <name> <date of birth>\n"
                                    + "Rules:\n"
                                    + "-The names must be comma separated without a space around the comma (eg. Andrew,Neil,Davidson)\n"
                                    + "-If there are any spaces in the name, the name must be enclosed in quotation marks (\")\n"
                                    + "-The date of birth must be entered in the format: dd/mm/yyyy\n"
                                    + "Example valid usage: create \"Test,User with,SpacesIn Name\" 01/05/1994");
                                break;
                            case "describe":
                                System.out.println("This command searches donors and displays information about them.\n"
                                    + "The syntax is: describe <id> OR describe <name>\n"
                                    + "Rules:\n"
                                    + "-If an id number is to be used as search criteria, it must be a number that is 0 or larger\n"
                                    + "-If a name or names are used, the names must be comma separated without a space around the comma (eg. drew,david)\n"
                                    + "-If a name or names are used, all donors whose names contain the input names in order will be returned as matches\n"
                                    + "-If there are any spaces in the name, the name must be enclosed in quotation marks (\")\n"
                                    + "Example valid usage: describe \"andrew,son\'");
                                break;
                            case "list":
                                System.out.println("This command lists all information about all donors in a table.\n"
                                    + "Example valid usage: list");
                                break;
                            case "set":
                                System.out.println("This command sets one attribute (apart from organs to be donated) of a donor. To find the id of a donor, "
                                    + "use the list and describe commands. To add or remove organs, instead use the add and remove commands.\n"
                                    + "The syntax is: set <id> <attribute> <value>\n"
                                    + "Rules:\n"
                                    + "-The id number must be a number that is 0 or larger\n"
                                    + "-The attribute must be one of the following (case insensitive): name, dateOfBirth, dateOfDeath, gender, height, "
                                    + "weight, bloodType, currentAddress\n"
                                    + "-If a name or names are used, all donors whose names contain the input names in order will be returned as matches\n"
                                    + "-The gender must be: male, female, or other\n"
                                    + "-The bloodType must be: A-, A+, B-, B+, AB-, AB+, O-, or O+\n"
                                    + "-The height and weight must be numbers that are larger than 0\n"
                                    + "-The date of birth and date of death values must be entered in the format: dd/mm/yyyy\n"
                                    + "Example valid usage: set 2 bloodtype ab+");
                                break;
                            case "delete":
                                System.out.println("This command deletes one donor. To find the id of a donor, use the list and describe commands.\n"
                                    + "The syntax is: delete <id>\n"
                                    + "Rules:\n"
                                    + "-The id number must be a number that is 0 or larger\n"
                                    + "-You will be asked to confirm that you want to delete this donor\n"
                                    + "Example valid usage: delete 1");
                                break;
                            case "add":
                                System.out.println("This command adds one organ to donate to a donor. To find the id of a donor, use the list and "
                                    + "describe commands.\n"
                                    + "The syntax is: add <id> <organ>\n"
                                    + "Rules:\n"
                                    + "-The id number must be a number that is 0 or larger\n"
                                    + "-The organ must be a donatable organ: liver, kidney, pancreas, heart, lung, intestine, cornea, middle-ear, skin, "
                                    + "bone-marrow, or connective-tissue.\n"
                                    + "Example valid usage: add 0 skin");
                                break;
                            case "remove":
                                System.out.println("This command removes one offered organ from a donor. To find the id of a donor, use the list and "
                                    + "describe commands.\n"
                                    + "The syntax is: remove <id> <organ>\n"
                                    + "Rules:\n"
                                    + "-The id number must be a number that is 0 or larger\n"
                                    + "-The organ must be a donatable organ: liver, kidney, pancreas, heart, lung, intestine, cornea, middle-ear, skin, "
                                    + "bone-marrow, or connective-tissue.\n"
                                    + "Example valid usage: remove 5 kidney");
                                break;
                            case "organ_list":
                                System.out.println("This command displays all of the organs that are currently offered by each donor. Donors that are "
                                    + "not yet offering any organs are not shown.\n"
                                    + "Example valid usage: organ_list");
                                break;
                            case "donor_organs":
                                System.out.println("This command displays the organs which a donor will donate or has donated. To find the id of a donor, "
                                    + "use the list and describe commands.\n"
                                    + "The syntax is: donor_organs <id>\n"
                                    + "Rules:\n"
                                    + "-The id number must be a number that is 0 or larger\n"
                                    + "Example valid usage: donor_organs 4");
                                break;
                            case "save":
                                System.out.println("This command saves the current donor database to a file in JSON format.\n"
                                    + "The syntax is: save [-r] <filepath>\n"
                                    + "Rules:\n"
                                    + "-If the -r flag is present, the filepath will be interpreted as relative\n"
                                    + "-If the filepath has spaces in it, it must be enclosed with quotation marks (\")\n"
                                    + "Example valid usage: save -r \"new folder\\donors.json\"");
                                break;
                            case "help":
                                System.out.println("This command displays information about how to use this program.\n"
                                    + "The syntax is: help OR help <command>\n"
                                    + "Rules:\n"
                                    + "-If the command argument is passed, the command must be: create, describe, list, set, delete, add, remove, organ_list, "
                                    + "donor_organs, save, help, or quit.\n"
                                    + "Example valid usage: help help");
                                break;
                            case "quit":
                                System.out.println("This command exits the program.\n"
                                    + "Example valid usage: quit");
                                break;
                            default:
                                System.out.println("Can not offer help with this command as it is not a valid command.");
                                success = false;
                        }

                    } else {
                        System.out.println("The help command must be used with 0 or 1 arguments (help or help <command>).");
                    }
                    break;
				case "quit":
					success = true;
					break;
				default:
                    System.out.println("Command not recognised. Enter 'help' to view available commands, or help <command> to view information about a specific command.");
			}
			if (success){
				String text = History.prepareFileString(nextCommand);
				History.printToFile(streamOut, text);
				success = false;
			}

		} while (!nextCommand[0].equals("quit"));
		scanner.close();
	}

	/**
	 * Prints a message to the console advising the user on how to correctly use a command they failed to use.
	 * @param command The command name
	 * @param argc The argument count
	 * @param args The arguments
	 */
	private void printIncorrectUsageString(String command, int argc, String args) {
		switch (argc) {
			case 0:
				System.out.println(String.format("The %s command does not accept arguments.", command));
				break;
			case 1:
				System.out.println(String.format("The %s command must be used with 1 argument (%s %s).", command, command, args));
				break;
			default:
				System.out.println(String.format("The %s command must be used with %d arguments (%s %s).", command, argc, command, args));
		}

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
	    Donor donor = Main.getDonorById(id);
	    if (donor == null) {
			System.out.println(String.format("Donor with ID %d not found.", id));
		} else {
			if (!donor.getOrgans().isEmpty()) {
				System.out.println(donor.getName() + ": " + donor.getOrgans());
			} else {
				System.out.println("No organs available from donor!");
			}
		}
    }

    /**
     * Displays information about a donor.
     * @param id The id of the donor to describe - either an ID number or a name to search for
     */
	private void describeDonor(String id) {
		try {
			Donor toDescribe = Main.getDonorById(Long.parseLong(id));
			if (toDescribe == null) {
				System.out.println(String.format("Donor with ID %s not found.", id));
			} else {
				System.out.println(toDescribe);
			}
		} catch (NumberFormatException e) {
			ArrayList<Donor> toDescribe = Main.getDonorByName(id.split(","));
			if (toDescribe.size() == 0) {
				System.out.println(String.format("No donors with names matching %s were found.", id));
			} else {
				System.out.println(Donor.tableHeader);
				for (Donor donor: toDescribe) {
					System.out.println(donor.getString(true));
				}
			}
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
		Donor toSet = Main.getDonorById(id);
		if (toSet == null) {
			System.out.println(String.format("Donor with ID %d not found.", id));
			return;
		}
		switch (attribute.toLowerCase()) {
			case "name":
				toSet.setName(value);
				System.out.println("New name set.");
				break;
			case "dateofbirth":
				try {
					toSet.setDateOfBirth(LocalDate.parse(value, Donor.dateFormat));
					System.out.println("New date of birth set.");
				} catch (DateTimeException e) {
					System.out.println("Please enter a valid date in the format dd/mm/yyyy.");
				}
				break;
			case "dateofdeath":
				try {
					toSet.setDateOfDeath(LocalDate.parse(value, Donor.dateFormat));
					System.out.println("New date of death set.");
				} catch (DateTimeException e) {
					System.out.println("Please enter a valid date in the format dd/mm/yyyy.");
				}
				break;
			case "gender":
				try {
					toSet.setGender(Gender.parse(value));
					System.out.println("New gender set.");
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
						System.out.println("New height set.");
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
						System.out.println("New weight set.");
					}
				} catch (NumberFormatException e) {
					System.out.println("Please enter a numeric weight.");
				}
				break;
			case "bloodtype":
				try {
					toSet.setBloodType(BloodType.parse(value));
					System.out.println("New blood type set.");
				} catch (IllegalArgumentException e) {
					System.out.println("Please enter blood type as A-, A+, B-, B+, AB-, AB+, O-, or O+.");
				}
				break;
			case "currentaddress":
				toSet.setCurrentAddress(value);
				System.out.println("New address set.");
			default:
				System.out.println("Attribute '" + attribute + "' not recognised. Try name, dateOfBirth, dateOfDeath, gender, height, " +
						"weight, bloodType, or currentAddress.");
		}

	}

	/**
	 * Ask for confirmation to delete the specified donor, and then delete it if the user confirms the action.
	 * @param id The id of the donor to consider deleting
	 */
	private void deleteDonor(long id) {
		Donor donor = Main.getDonorById(id);
		if (donor == null) {
			System.out.println(String.format("Donor with ID %d not found.", id));
			return;
		}
		System.out.print(String.format("Are you sure you want to delete %s, ID %d? (y/n) ", donor.getName(), donor.getId()));
		String nextLine = scanner.nextLine();
		while (!nextLine.toLowerCase().equals("y") && !nextLine.toLowerCase().equals("n")) {
			System.out.println("Answer not recognised. Please enter y or n: ");
			nextLine = scanner.nextLine();
		}
		if (nextLine.equals("y")) {
			Main.donors.remove(donor);
			System.out.println("Donor removed. This change will permanent once the donor list is saved.");
		} else {
			System.out.println("Donor was not removed.");
		}
	}

    /**
     * Adds an organ object to a donors list of available organs.
     * @param id the donor giving the organ.
     * @param organ the type of organ being donated.
     */
    private void addOrgan(long id, String organ) {
	    Donor toSet = Main.getDonorById(id);
	    if (toSet == null) {
			System.out.println(String.format("Donor with ID %d not found.", id));
	        return;
        }
        try {
            toSet.setOrgan(Organ.parse(organ));
        } catch (IllegalArgumentException e) {
            System.out.println("Error in input! Available organs: liver, kidney, pancreas, heart, lung, intestine, " +
			"cornea, middle-ear, skin, bone-marrow, connective-tissue");
        }

    }

	/**
	 * Removes an organ object from a donors available organ set, if it exists.
	 * @param id the donor having the organ removed from the set
	 * @param organ the organ being removed
	 */
	private void removeOrgan(long id, String organ) {
        Donor toSet = Main.getDonorById(id);
        if(toSet == null){
			System.out.println(String.format("Donor with ID %d not found.", id));
            return;
        }
        try {
            toSet.removeOrgan(Organ.parse(organ));
        } catch (IllegalArgumentException e) {
            System.out.println("Error in input! Available organs: liver, kidney, pancreas, heart, lung, intestine, " +
                    "cornea, middle-ear, skin, bone-marrow, connective-tissue");
        }

    }
}
