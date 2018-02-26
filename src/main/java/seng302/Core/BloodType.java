package seng302.Core;

public enum BloodType {
	A_NEG("A-"), A_POS("A+"), B_NEG("B-"), B_POS("B+"), O_NEG("O-"), O_POS("O+");

	private String type;

	BloodType(String type) {
		this.type = type;
	}

	public String toString() {
		return type;
	}

	public static BloodType parse(String text) {
		for (BloodType bloodType: BloodType.values()) {
			if (bloodType.toString().equalsIgnoreCase(text)) {
				return bloodType;
			}
		}
		throw new IllegalArgumentException("Blood type not recognised.");
	}
}
