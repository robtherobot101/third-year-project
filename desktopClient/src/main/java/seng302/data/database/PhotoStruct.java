package seng302.data.database;

public class PhotoStruct {
    private String data;

    /**
     * class initilizer for a photo struct
     * @param data String the data for the photo
     */
    PhotoStruct(String data) {
        this.data = data;
    }

    /**
     * method to return the data of a photostruct
     * @return String the data for a photo
     */
    public String getData() {
        return data;
    }

    /**
     * method to set the data of a photostruct
     * @param data String the data for a photo
     */
    public void setData(String data) {
        this.data = data;
    }
}
