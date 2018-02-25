package seng302;

public class Main {
    public static long nextDonorId = -1;

    public static long getNextDonorId(boolean increment) {
    	if (increment) {
			nextDonorId++;
		}
    	return nextDonorId;
	}

    public static void main(String[] args) {
        CommandLineInterface commandLineInterface = new CommandLineInterface();
        commandLineInterface.run();
    }
}
