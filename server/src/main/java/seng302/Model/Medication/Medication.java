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
     * @param id The id of the medication within the database.
     */
    public Medication(String name, String[] activeIngredients, ArrayList<String> history, int id) {
        this.name = name;
        this.activeIngredients = activeIngredients;
        this.history = new ArrayList<>();
        this.history.addAll(history);
        this.id = id;
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

    public void setActiveIngredients(String[] activeIngredients) {
        this.activeIngredients = activeIngredients;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return name;
    }


}
