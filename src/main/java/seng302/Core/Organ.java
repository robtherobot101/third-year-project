package seng302.Core;

public enum Organ {
    LIVER("liver"), KIDNEY("kidney"), PANCREAS("pancreas"), HEART("heart"), LUNG("lung"), INTESTINE("intestine"),
    CORNEA("cornea"), EAR("middle ear"), SKIN("skin"), BONE("bone marrow"), TISSUE("connective tissue");

    private String organ;

    Organ(String organ){
        this.organ = organ;
    }

    public String toString(){
        return organ;
    }

    public static Organ parse(String text){
        for(Organ organ : Organ.values()){
            if (organ.toString().equalsIgnoreCase(text)){
                return organ;
            }
        }
        throw new IllegalArgumentException("Organ not recognised");
    }
}


