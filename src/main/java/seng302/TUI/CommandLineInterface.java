package seng302.TUI;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
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
				System.out.print("> ");
				nextCommand = scanner.nextLine().split(" ");
			} while (nextCommand.length == 0);
			switch (nextCommand[0]) {
				case "create":
					if (nextCommand.length == 3) {
						try {
							Main.donors.add(new Donor(nextCommand[1].replace("\"", ""), LocalDate.parse(nextCommand[2], Donor.dateFormat)));
							System.out.println("New donor created.");
						} catch (DateTimeException e) {
							System.out.println("Please enter a valid date of birth in the format dd/mm/yyyy.");
						}
					} else if (nextCommand.length > 3 && nextCommand[1].contains("\"") && nextCommand[nextCommand.length-2].contains("\"")) {
						String date = nextCommand[nextCommand.length-1];
						nextCommand = String.join(" ", nextCommand).split("\"");
						if (nextCommand.length == 3) {
							try {
								Main.donors.add(new Donor(nextCommand[1], LocalDate.parse(date, Donor.dateFormat)));
								System.out.println("New donor created.");
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
					} else {
						printIncorrectUsageString("describe", 1, "<id>");
					}
					break;
				case "list":
					if (nextCommand.length == 1) {
						listDonors();
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
							} catch (NumberFormatException e) {
								System.out.println("Please enter a valid ID number.");
							}
						} else {
							System.out.println("Please enter a name.");
						}
					} else if (nextCommand.length == 4) {
						try {
							setAttribute(Long.parseLong(nextCommand[1]), nextCommand[2], nextCommand[3]);
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
					} else {
						printIncorrectUsageString("organ_list", 0, null);
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
						printIncorrectUsageString("remove", 1, "<id>");
					}
					break;
				case "save":
					if (nextCommand.length >= 2) {
					    String[] afterCommand = new String[nextCommand.length-1];
                        System.arraycopy(nextCommand, 1, afterCommand,0,nextCommand.length-1);
                        if (Main.saveDonors(String.join(" ", afterCommand))) {
                            System.out.println("Donors saved.");
                        } else {
                            System.out.println("Failed to save to " + String.join(" ", afterCommand) + ". Make sure the program has access to this file.");
                        }
					} else {
						printIncorrectUsageString("save", 1, "<filename>");
					}
					break;
				case "quit":
					break;
				default:
					System.out.println("Input not recognised. Valid commands are: "
						+ "\n\t-create \"name part 1,/name part 2\" <date of birth>"
						+ "\n\t-describe <id>"
						+ "\n\t-describe \"name part 1,name part 2\""
						+ "\n\t-list"
						+ "\n\t-set <id> <attribute> <value>"
                        + "\n\t-delete <id>"
						+ "\n\t-add <id> <organ>"
						+ "\n\t-remove <id> <organ>"
						+ "\n\t-organ_list"
						+ "\n\t-donor_organs <id>"
						+ "\n\t-save <filename>"
						+ "\n\t-quit");
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
					System.out.println("Please enter blood type as A-, A+, B-, B+, O-, or O+.");
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

    private void addOrgan(long id, String organ) {
	    Donor toSet = Main.getDonorById(id);
	    if (toSet == null) {
			System.out.println(String.format("Donor with ID %d not found.", id));
	        return;
        }
        try {
            toSet.setOrgan(Organ.parse(organ));
			System.out.println("Organ added.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error in input! Available organs: liver, kidney, pancreas, heart, lung, intestine, " +
			"cornea, middle-ear, skin, bone-marrow, connective-tissue");
        }

    }

    private void removeOrgan(long id, String organ) {
        Donor toSet = Main.getDonorById(id);
        if(toSet == null){
			System.out.println(String.format("Donor with ID %d not found.", id));
            return;
        }
        try {
            toSet.removeOrgan(Organ.parse(organ));
			System.out.println("Organ removed.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error in input! Available organs: liver, kidney, pancreas, heart, lung, intestine, " +
                    "cornea, middle-ear, skin, bone-marrow, connective-tissue");
        }

    }



}
