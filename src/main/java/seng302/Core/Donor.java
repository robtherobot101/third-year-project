package seng302.Core;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Donor {
	public static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	public static final DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy, HH:mm:ss");
	public static final String tableHeader = "Donor ID | Creation Time        | Name                 | Date of Birth" +
			" | Date of Death | Gender | Height | Weight | Blood Type | Current Address";
	private String name, currentAddress;
	private LocalDate dateOfBirth, dateOfDeath;
	private LocalDateTime creationTime;
	private Gender gender;
	private double height, weight;
	private BloodType bloodType;
	private long id;

	public Donor (String name, LocalDate dateOfBirth) {
		this.name = name;
		this.dateOfBirth = dateOfBirth;
		this.dateOfDeath = null;
		this.gender = null;
		this.height = -1;
		this.weight = -1;
		this.bloodType = null;
		this.currentAddress = null;
		this.creationTime = LocalDateTime.now();
		this.id = Main.getNextDonorId(true);
	}

	public Donor(String name, String dateOfBirth, String dateOfDeath, String gender, double height, double weight,
				 String bloodType, String currentAddress) throws DateTimeException, IllegalArgumentException {
		this.name = name;
		this.dateOfBirth = LocalDate.parse(dateOfBirth, dateFormat);
		this.dateOfDeath = LocalDate.parse(dateOfDeath, dateFormat);
		this.gender = Gender.parse(gender);
		this.height = height;
		this.weight = weight;
		this.bloodType = BloodType.parse(bloodType);
		this.currentAddress = currentAddress;
		this.creationTime = LocalDateTime.now();
		this.id = Main.getNextDonorId(true);
	}

	public void setDateOfDeath(LocalDate dateOfDeath) {
		this.dateOfDeath = dateOfDeath;
	}

	public void setHeight(double height) {
		this.height = height;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public void setBloodType(BloodType bloodType) {
		this.bloodType = bloodType;
	}

	public void setCurrentAddress(String currentAddress) {
		this.currentAddress = currentAddress;
	}

	public long getId() {
		return id;
	}

	public String getString(boolean table) {
		String dateOfDeathString, heightString, weightString;
		if (dateOfDeath != null) {
			dateOfDeathString = dateFormat.format(dateOfDeath);
		} else {
			dateOfDeathString = null;
		}
		if (height == -1) {
			heightString = null;
		} else {
			heightString = String.format("%.2f", height);
		}
		if (weight == -1) {
			weightString = null;
		} else {
			weightString = String.format("%.2f", weight);
		}

		if (table) {
			return String.format("%-8d | %s | %-20s | %10s    | %-10s    | %-6s | %-5s  | %-6s | %-4s       | %s",
					id, dateTimeFormat.format(creationTime), name, dateFormat.format(dateOfBirth),
					dateOfDeathString, gender, heightString, weightString, bloodType, currentAddress);
		} else {
			return String.format("Donor (ID %d) created at %s Name: %s, Date of Birth: %s, Date of death: %s, " +
							"Gender: %s, Height: %s, Width: %s, Blood type: %s, Current address: %s.",
					id, dateTimeFormat.format(creationTime), name, dateFormat.format(dateOfBirth),
					dateOfDeathString, gender, heightString, weightString, bloodType, currentAddress);
		}
	}

	public String toString() {
		return getString(false);
	}
}
