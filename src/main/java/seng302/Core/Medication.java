package seng302.Core;

/**
 * Class contains all the information for a given Medication.
 */
public class Medication {

    private String name;
    private String[] activeIngredients;

    public Medication(String name) {
        this.name = name;
    }

    public Medication(String name, String[] activeIngredients) {
        this.name = name;
        this.activeIngredients = activeIngredients;
    }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String[] getActiveIngredients() { return activeIngredients; }

    public void setActiveIngredients(String[] activeIngredients) { this.activeIngredients = activeIngredients; }
}
