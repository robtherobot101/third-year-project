package seng302.TUI;

import seng302.Core.*;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.InputMismatchException;
import java.util.Scanner;

public class CommandLineInterface {
	private Scanner scanner;

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
	    Donor donor = getDonorById(id);
	    if (donor != null) {
			if (!donor.getOrgans().isEmpty()) {
				System.out.println(donor.getName() + ": " + donor.getOrgans());
			} else {
				System.out.println("No organs available from donor!");
			}
		}
    }


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

	private void describeDonor(long id) {
		Donor toDescribe = getDonorById(id);
		if (toDescribe != null) {
			System.out.println(toDescribe);
		}
	}

	private void listDonors() {
		System.out.println(Donor.tableHeader);
		for (Donor donor: Main.donors) {
			System.out.println(donor.getString(true));
		}
	}

	private void setAttribute(long id, String attribute, String value) {
		Donor toSet = getDonorById(id);
		if (toSet == null) {
			return;
		}
		switch (attribute) {
			case "dateofdeath":
			case "dateOfDeath":
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
			case "bloodType":
				try {
					toSet.setBloodType(BloodType.parse(value));
                    toSet.setLastModified();
				} catch (IllegalArgumentException e) {
					System.out.println("Please enter blood type as A-, A+, B-, B+, O-, or O+.");
				}
				break;
			case "currentaddress":
			case "currentAddress":
				System.out.print("Enter the new donor's address: ");
				toSet.setCurrentAddress(value);
                toSet.setLastModified();
			default:
				System.out.println("Attribute '" + attribute + "' not recognised. Try dateOfDeath, gender, height, " +
						"weight, bloodType, or currentAddress.");
		}

	}

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
