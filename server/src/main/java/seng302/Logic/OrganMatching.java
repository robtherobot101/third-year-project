package seng302.Logic;

import com.sun.javafx.scene.web.Debugger;
import seng302.Logic.Database.GeneralUser;
import seng302.Model.DonatableOrgan;
import seng302.Model.User;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class OrganMatching {

    private GeneralUser model;
    private static DonatableOrgan organ;

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
        if (receiver.getDateOfDeath() == null){
            return false;
        }
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
     * @return a list of matched users
     */
    private List<User> getAllMatches(User donor) throws SQLException{
        List<User> matches = new ArrayList<>();
        List<User> possibleMatches = model.getMatchingUsers(organ);
        for (User user: possibleMatches){
            if (isSameBloodType(donor, user) && isWithinAgeRange(donor, user)){
                matches.add(user);
            }
        }
        return matches;
    }

    /**
     * gets the top 5 matches by area and waiting time
     * @param region the region to compare to
     * @param possibleMatches the list of possible matches
     * @return returns a the top 5 users
     */
    private List<User> getBestMatches(String region, List<User> possibleMatches){
        List<User> topMatches = new ArrayList<>();
        for(User user: possibleMatches){
            if (user.getRegion() != null && user.getRegion().equals(region)){
                topMatches.add(user);
            }
        }
        topMatches.sort(new WaitingComparator());

        if (topMatches.size() < 5){
            possibleMatches.removeAll(topMatches);
            possibleMatches.sort(new WaitingComparator());
            int diff = 5 - topMatches.size();
            if (possibleMatches.size() < diff){
                topMatches.addAll(possibleMatches);
            } else {
                topMatches.addAll(possibleMatches.subList(0, diff));
            }
        }
        if (topMatches.size() > 5) {
            return topMatches.subList(0, 4);
        } else {
            return topMatches;
        }
    }

    /**
     * returns a list of the top 5 users ids
     * @param organ the organ to compare against
     * @return the list of users ids
     */
    public List<Long> getTop5Matches(DonatableOrgan organ, String receiverNameQuery){
        try {
            OrganMatching.organ = organ;
            User donor = model.getUserFromId((int) OrganMatching.organ.getDonorId());
            List<User> matches = getAllMatches(donor);
            matches = getBestMatches(donor.getRegionOfDeath(), matches);
            List<Long> topMatches = new ArrayList<>();
            for (User user : matches){
                if(user.getName() != null && user.getName().toLowerCase().contains(receiverNameQuery.toLowerCase())){
                    topMatches.add(user.getId());
                } else if(user.getName() == null && user.getName().equals("")){
                    topMatches.add(user.getId());
                }
            }
            return topMatches;
        } catch (SQLException e){
            System.out.println("Error communicating with the database");
            return new ArrayList<>();
        }
    }

    /**
     * Compares to users based on waiting time
     */
    static class WaitingComparator implements Comparator<User> {
        @Override
        public int compare(User user1, User user2) {
            int i = 0;
            int j = 0;

            for(; i < user1.getWaitingListItems().size(); i++) {
                if (user1.getWaitingListItems().get(i).getOrganType().toString().equals(organ.getOrganType().toString()) && user1.getWaitingListItems().get(i).getOrganDeregisteredDate() == null){
                    break;
                }
            }
            for(; j < user2.getWaitingListItems().size(); j++) {
                if (user2.getWaitingListItems().get(j).getOrganType().toString().equals(organ.getOrganType().toString()) && user2.getWaitingListItems().get(j).getOrganDeregisteredDate() == null){
                    break;
                }
            }
            try{
                int comp = user1.getWaitingListItems().get(i).getOrganRegisteredDate().compareTo(user2.getWaitingListItems().get(j).getOrganRegisteredDate());;
                return comp;
            } catch (IndexOutOfBoundsException e) {
                System.out.println("Avaliable organs comparator is broken");
                return 0;
            }
        }
    }
}
