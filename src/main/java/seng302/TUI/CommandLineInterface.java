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
		System.out.print("$ ");
		String nextCommand = scanner.next();

		while (!nextCommand.equals("quit")) {
			if (nextCommand.equals("create")) {
				createDonor();
			} else if (nextCommand.equals("describe")) {
				try {
					describeDonor(scanner.nextLong());
				} catch (NumberFormatException e) {
					System.out.println("Please enter a valid ID number.");
				}
			} else if (nextCommand.equals("list")) {
				listDonors();
			} else if (nextCommand.equals("set")) {
				try {
					setAttribute(scanner.nextLong(), scanner.next(), scanner.next());
				} catch (NumberFormatException e) {
					System.out.println("Please enter a valid ID number.");
				}
			}
			// Consider changing the number format exception to input mismatch exception, eg try "set asdf"
			else if(nextCommand.equals("add")) {
			    try {
                    addOrgan(scanner.nextLong(), scanner.next());
                } catch (InputMismatchException e) {
                    System.out.println("Please enter a valid ID number.");
                }
            } else if(nextCommand.equals("remove")) {
                try {
                    removeOrgan(scanner.nextLong(), scanner.next());
                } catch (InputMismatchException e) {
                    System.out.println("Please enter a valid ID number.");
                }
            } else if (nextCommand.equals("organ_list")) {
                    listOrgans();
            } else if (nextCommand.equals("donor_organs")) {
                try {
                    listDonorOrgans(scanner.nextLong());
                } catch (InputMismatchException e) {
                    System.out.println("Please enter a valid ID number.");
                }
            }

            else if (!nextCommand.equals("quit")) {
				System.out.println("Input not recognised. Valid commands are: create, describe <id>, list, " +
						"set <id> <attribute> <value>, add <id> <organ>, remove <id> <organ> organ_list, " +
                        "donor_organs <id>, quit.");
			}

			System.out.print("$ ");
			nextCommand = scanner.next();
		}

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
        if(!donor.getOrgans().isEmpty()){
            System.out.println(donor.getName() + ": " + donor.getOrgans());
        } else {
            System.out.println("No organs available from donor!");
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
				System.out.println("Please enter a date in the format dd/mm/yyyy.");
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
			case "dateOfDeath":
				try {
					toSet.setDateOfDeath(LocalDate.parse(value, Donor.dateFormat));
                    toSet.setLastModified();
				} catch (DateTimeException e) {
					System.out.println("Please enter the date in the format dd/mm/yyyy.");
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
			case "bloodType":
				try {
					toSet.setBloodType(BloodType.parse(value));
                    toSet.setLastModified();
				} catch (IllegalArgumentException e) {
					System.out.println("Please enter blood type as A-, A+, B-, B+, O-, or O+.");
				}
				break;
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
	    if(toSet == null){
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
