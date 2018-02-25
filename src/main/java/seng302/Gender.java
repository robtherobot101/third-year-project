package seng302;

public enum Gender {
	OTHER("other"), FEMALE("female"), MALE("male");

	private String gender;

	Gender(String gender) {
		this.gender = gender;
	}

	public String toString() {
		return gender;
	}
}
