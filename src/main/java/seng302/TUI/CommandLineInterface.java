package seng302.TUI;

import seng302.Core.BloodType;
import seng302.Core.Donor;
import seng302.Core.Gender;
import seng302.Core.Main;

import java.time.DateTimeException;
import java.time.LocalDate;
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
			} else if (!nextCommand.equals("quit")) {
				System.out.println("Input not recognised. Valid commands are: create, describe <id>, list, " +
						"set <id> <attribute> <value>, quit.");
			}

			System.out.print("$ ");
			nextCommand = scanner.next();
		}

		scanner.close();
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
			System.out.println(toDescribe);;
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
				} catch (DateTimeException e) {
					System.out.println("Please enter the date in the format dd/mm/yyyy.");
				}
				break;
			case "gender":
				try {
					toSet.setGender(Gender.parse(value));
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
					}
				} catch (NumberFormatException e) {
					System.out.println("Please enter a numeric weight.");
				}
				break;
			case "bloodType":
				try {
					toSet.setBloodType(BloodType.parse(value));
				} catch (IllegalArgumentException e) {
					System.out.println("Please enter blood type as A-, A+, B-, B+, O-, or O+.");
				}
				break;
			case "currentAddress":
				System.out.print("Enter the new donor's address: ");
				toSet.setCurrentAddress(value);
			default:
				System.out.println("Attribute '" + attribute + "' not recognised. Try dateOfDeath, gender, height, " +
						"weight, bloodType, or currentAddress.");
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
