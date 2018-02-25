package seng302;

public enum BloodType {
	A_NEG("A-"), A_POS("A+"), B_NEG("B-"), B_POS("B+"), O_NEG("O-"), O_POS("O+");

	private String type;

	BloodType(String type) {
		this.type = type;
	}

	public String toString() {
		return type;
	}
}
