package seng302.Core;

public enum Gender {
	OTHER("other"), FEMALE("female"), MALE("male");

	private String gender;

	Gender(String gender) {
		this.gender = gender;
	}

	public String toString() {
		return gender;
	}

	public static Gender parse(String text) {
		for (Gender gender: Gender.values()) {
			if (gender.toString().equalsIgnoreCase(text)) {
				return gender;
			}
		}
		throw new IllegalArgumentException("Gender not recognised.");
	}
}
