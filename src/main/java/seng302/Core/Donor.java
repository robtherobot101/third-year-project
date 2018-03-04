package seng302.Core;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.EnumSet;

/**
 * This class contains information about organ donors.
 */
public class Donor {
	public static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	public static final DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy, HH:mm:ss");
	public static final String tableHeader = "Donor ID | Creation Time        | Name                   | Date of Birth" +
			" | Date of Death | Gender | Height | Weight | Blood Type | Current Address                | Last Modified";

	private String[] name;
	private LocalDate dateOfBirth, dateOfDeath;
	private LocalDateTime creationTime;
	private LocalDateTime lastModified;
	private Gender gender;
	private double height, weight;
	private BloodType bloodType;
	private long id;
	private String currentAddress;
	private EnumSet<Organ> organs = EnumSet.noneOf(Organ.class);

	public Donor (String name, LocalDate dateOfBirth) {
		setName(name);
		this.dateOfBirth = dateOfBirth;
		this.dateOfDeath = null;
		this.gender = null;
		this.height = -1;
		this.weight = -1;
		this.bloodType = null;
		this.currentAddress = null;
		this.creationTime = LocalDateTime.now();
		this.lastModified = creationTime;
		this.id = Main.getNextDonorId(true);
	}

	public Donor(String name, String dateOfBirth, String dateOfDeath, String gender, double height, double weight,
				 String bloodType, String currentAddress) throws DateTimeException, IllegalArgumentException {
		setName(name);
		this.dateOfBirth = LocalDate.parse(dateOfBirth, dateFormat);
		this.dateOfDeath = LocalDate.parse(dateOfDeath, dateFormat);
		this.gender = Gender.parse(gender);
		this.height = height;
		this.weight = weight;
		this.bloodType = BloodType.parse(bloodType);
		this.currentAddress = currentAddress;
		this.creationTime = LocalDateTime.now();
		this.lastModified = creationTime;
		this.id = Main.getNextDonorId(true);
	}

	public String getName() {
		return String.join(" ", name);
	}

	public String[] getNameArray() {
		return name;
	}
	public EnumSet<Organ> getOrgans() {
		return organs;
	}

	public long getId() {
		return id;
	}

	public void setName(String name) {
		this.name = name.split(",");
		setLastModified();
	}

	public void setDateOfBirth(LocalDate dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
		setLastModified();
	}

	public void setDateOfDeath(LocalDate dateOfDeath) {
		this.dateOfDeath = dateOfDeath;
		setLastModified();
	}

	public void setHeight(double height) {
		this.height = height;
		setLastModified();
	}

	public void setWeight(double weight) {
		this.weight = weight;
		setLastModified();
	}

	public void setGender(Gender gender) {
		this.gender = gender;
		setLastModified();
	}

	public void setBloodType(BloodType bloodType) {
		this.bloodType = bloodType;
		setLastModified();
	}

	public void setCurrentAddress(String currentAddress) {
		this.currentAddress = currentAddress;
		setLastModified();
	}

	public void setOrgan(Organ organ){
		if (!organs.contains(organ)) {
            this.organs.add(organ);
        } else {
		    System.out.println("Organ already being donated.");
        }
		setLastModified();
    }

    public void removeOrgan(Organ organ) {
        if (organs.contains(organ)) {
            this.organs.remove(organ);
        } else {
            System.out.println("Organ not in list.");
        }
		setLastModified();
    }

	private void setLastModified() {
		lastModified = LocalDateTime.now();
	}

    /**
     * Get a string containing key information about the donor. Can be formatted as a table row.
     * @param table Whether to format the information as a table row
     * @return The information string
     */
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
			return String.format("%-8d | %s | %-22s | %10s    | %-10s    | %-6s | %-5s  | %-6s | %-4s       | %-30s | %s ",
					id, dateTimeFormat.format(creationTime), getName(), dateFormat.format(dateOfBirth),
					dateOfDeathString, gender, heightString, weightString, bloodType, currentAddress,
                    dateTimeFormat.format(lastModified));
		} else {
			return String.format("Donor (ID %d) created at %s Name: %s, Date of Birth: %s, Date of death: %s, " +
							"Gender: %s, Height: %s, Width: %s, Blood type: %s, Current address: %s, Last Modified: %s.",
					id, dateTimeFormat.format(creationTime), getName(), dateFormat.format(dateOfBirth),
					dateOfDeathString, gender, heightString, weightString, bloodType, currentAddress,
                    dateTimeFormat.format(lastModified));

		}
	}

	public String toString() {
		return getString(false);
	}
}
