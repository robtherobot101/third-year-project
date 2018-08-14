package seng302.User.Medication;

import seng302.User.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Class contains all the information for a given Medication.
 */
public class Medication {

    private String name;
    private String[] activeIngredients;
    private ArrayList<String> history;

    public Medication(String name) {
        this.name = name;
    }

    public Medication(String name, String[] activeIngredients) {
        this.name = name;
        this.activeIngredients = activeIngredients;
        history = new ArrayList<>();
    }

    /**
     * Constructor used for the creation of a medication from the database
     * @param name Name of the medication
     * @param activeIngredients All the ingredients in the medication
     * @param history All of the history of the medication's use.
     */
    public Medication(String name, String[] activeIngredients, ArrayList<String> history) {
        this.name = name;
        this.activeIngredients = activeIngredients;
        this.history = new ArrayList<>();
        this.history.addAll(history);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getActiveIngredients() {
        return activeIngredients;
    }

    public ArrayList<String> getHistory() {
        return history;
    }

    /**
     * Add a line to the user history that states that they started taking this medication now.
     */
    public void startedTaking() {
        history.add("Started taking on " + User.dateTimeFormat.format(LocalDateTime.now()));
    }

    /**
     * Add a line to the user history that states that they stopped taking this medication now.
     */
    public void stoppedTaking() {
        history.add("Stopped taking on " + User.dateTimeFormat.format(LocalDateTime.now()));
    }

    /**
     * overrides the equals function to work for medications
     * @param o the object to compared with
     * @return true if the a the same, otherwise false
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Medication that = (Medication) o;
        return Objects.equals(name, that.name);
    }

    /**
     * overrides the hash code of the medication
     * @return returns the hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    /**
     * sets the active ingredients of the medication
     * @param activeIngredients active ingredients in the medication
     */
    public void setActiveIngredients(String[] activeIngredients) {
        this.activeIngredients = activeIngredients;
    }

    /**
     * overrides the toString function
     * @return the name of the medication
     */
    @Override
    public String toString() {
        return name;
    }


}
