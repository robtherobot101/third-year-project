package seng302.Model.Medication;

import seng302.Model.User;

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
    private int id;

    /**
     * constructor method to create a new medication object to be passed on to the Database
     * @param name String the name of the medication
     */
    public Medication(String name) {
        this.name = name;
    }

    /**
     * constructor method to create a new medication object to be passed on to the Database
     * @param name String the name of the medication
     * @param activeIngredients String[] list of all active ingredients in the medication
     */
    public Medication(String name, String[] activeIngredients) {
        this.name = name;
        this.activeIngredients = activeIngredients;
        history = new ArrayList<>();
    }

    /**
     * Constructor used for the creation of a medication from the Database
     * @param name Name of the medication
     * @param activeIngredients All the ingredients in the medication
     * @param history All of the history of the medication's use.
     * @param id The id of the medication within the Database.
     */
    public Medication(String name, String[] activeIngredients, ArrayList<String> history, int id) {
        this.name = name;
        this.activeIngredients = activeIngredients;
        this.history = new ArrayList<>();
        this.history.addAll(history);
        this.id = id;
    }

    /**
     * method to get the name of a medication
     * @return String name of the medication
     */
    public String getName() { return name; }

    /**
     * method to set the name of a medication
     * @param name String new name of a medication
     */
    public void setName(String name) { this.name = name; }

    /**
     * method to get the list of active ingredients of a medication
     * @return String[] string array containing all the active ingredients in a medication
     */
    public String[] getActiveIngredients() { return activeIngredients; }

    /**
     * method to get the history of a medication object
     * @return ArrayList containing all the previous times the medication was used
     */
    public ArrayList<String> getHistory() { return history; }

    //TODO what are these for, do we need them anymore?
    /**
     * Add a line to the User history that states that they started taking this medication now.
     */
    public void startedTaking() {
        history.add("Started taking on " + User.dateTimeFormat.format(LocalDateTime.now()));
    }

    /**
     * Add a line to the User history that states that they stopped taking this medication now.
     */
    public void stoppedTaking() {
        history.add("Stopped taking on " + User.dateTimeFormat.format(LocalDateTime.now()));
    }

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

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    /**
     * method to set the active ingredients of a medication object
     * (overrides existing active ingredients)
     * @param activeIngredients String[] list containing all the names of the active ingredients
     */
    public void setActiveIngredients(String[] activeIngredients) { this.activeIngredients = activeIngredients; }

    /**
     * method to get the medication id
     * @return int the medication id
     */
    public int getId() { return id; }

    /**
     * method to set the medication id
     * @param id int the new medication id
     */
    public void setId(int id) { this.id = id; }

    @Override
    public String toString() {
        return name;
    }


}
