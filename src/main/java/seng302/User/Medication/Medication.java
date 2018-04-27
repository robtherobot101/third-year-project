package seng302.User.Medication;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Medication that = (Medication) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name);
    }

    public void setActiveIngredients(String[] activeIngredients) { this.activeIngredients = activeIngredients; }

    @Override
    public String toString(){
        return name;
    }



}
