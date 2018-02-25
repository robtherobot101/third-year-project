package seng302;

import java.time.LocalDateTime;

public class Donor {
	private String name, currentAddress;
	private LocalDateTime dateOfBirth, dateOfDeath, creationTime;
	private Gender gender;
	private double height, width;
	private BloodType bloodType;
	private long id;

	public Donor(String name, LocalDateTime dateOfBirth, LocalDateTime dateOfDeath, Gender gender, double height, double width, BloodType bloodType, String currentAddress) {
		this.name = name;
		this.dateOfBirth = dateOfBirth;
		this.dateOfDeath = dateOfDeath;
		this.gender = gender;
		this.height = height;
		this.width = width;
		this.bloodType = bloodType;
		this.currentAddress = currentAddress;
		this.id = Main.getNextDonorId(true);
	}
}
