package seng302.Core;

/**
 * This enum represents the most common genders.
 */
public enum Gender {
	OTHER("other"), FEMALE("female"), MALE("male");

	private String gender;

	Gender(String gender) {
		this.gender = gender;
	}

	public String toString() {
		return gender;
	}

	/**
	 * Converts a string to its corresponding Gender enum value. Throws an IllegalArgumentException if the string
	 * does not match any Gender.
	 * @param text The string to convert
	 * @return The Gender corresponding to the input string
	 */
	public static Gender parse(String text) {
		for (Gender gender: Gender.values()) {
			if (gender.toString().equalsIgnoreCase(text)) {
				return gender;
			}
		}
		throw new IllegalArgumentException("Gender not recognised.");
	}
}
