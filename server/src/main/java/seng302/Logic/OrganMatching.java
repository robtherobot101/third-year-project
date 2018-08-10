package seng302.Logic;

import seng302.Config.DatabaseConfiguration;
import seng302.Logic.Database.GeneralUser;
import seng302.Model.DonatableOrgan;
import seng302.Model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrganMatching {

    private GeneralUser model;

    public OrganMatching(){
        model = new GeneralUser();
    }


    /**
     * checks if two users are within the correct age range to transfer organs
     * @param donor the donor to check
     * @param receiver the receiver to check
     * @return returns truew if they are in the correct age range, otherwise false
     */
    private boolean isWithinAgeRange(User donor, User receiver){
        if (donor.getAgeDouble() == receiver.getAgeDouble()){
            return true;
        } else if (donor.getAgeDouble() < 12 && receiver.getAgeDouble() < 12){
            return true;
        } else if (donor.getAgeDouble() > 12 && receiver.getAgeDouble() > 12){
            if (donor.getAgeDouble() > receiver.getAgeDouble()){
                return (donor.getAgeDouble() - receiver.getAgeDouble()) < 15;
            } else {
                return (receiver.getAgeDouble() - donor.getAgeDouble()) < 15;
            }
        }
        return false;
    }


    /**
     * check if two users have the same blood type
     * @param donor the first user
     * @param receiver the second user
     * @return returns true if both users have the same blood type, otherwise false
     */
    private boolean isSameBloodType(User donor, User receiver){
        return  (donor.getBloodType() == receiver.getBloodType());
    }


    /**
     * Gets all of the matches between an organ and receivers
     * @param organ the organ to match with
     * @return a list of matched users
     */
    public List<User> getAllMatches(DonatableOrgan organ){
        try {
            List<User> matches = new ArrayList<>();
            User donor = model.getUserFromId((int)organ.getDonorId());
            List<User> possibleMatches = model.getMatchingUsers(organ);
            for (User user: possibleMatches){
                if (isSameBloodType(donor, user) && isWithinAgeRange(donor, user)){
                    matches.add(user);
                }
            }
            return matches;

        } catch (SQLException e){
            System.out.println("Error connecting to the database");
            return new ArrayList<User>();
        }

    }
}
