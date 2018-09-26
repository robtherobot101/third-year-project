package seng302.Logic;

import seng302.Logic.Database.GeneralUser;
import seng302.Model.Attribute.Organ;
import seng302.Model.DonatableOrgan;
import seng302.Model.User;
import seng302.Server;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class OrganMatching {

    private GeneralUser model;

    public OrganMatching(){
        model = new GeneralUser();
    }

    /**
     * gets the top 5 matches by area and waiting time
     * @param region the region to compare to
     * @param possibleMatches the list of possible matches
     * @return returns a the top 5 users
     */
    private List<User> getBestMatches(String region, List<User> possibleMatches, DonatableOrgan organ) {
        List<User> topMatches = new ArrayList<>();
        for(User user: possibleMatches){
            if (user.getRegion().equals(region)){
                topMatches.add(user);
            }
        }
        topMatches.sort(new WaitingComparator(organ));

        if (topMatches.size() < 5){
            possibleMatches.removeAll(topMatches);
            possibleMatches.sort(new WaitingComparator(organ));
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
     * @param receiverNameQuery the user who is receiving
     * @return the list of users ids
     */
    public List<User> getTop5Matches(DonatableOrgan organ, String receiverNameQuery){
        try {
            User donor = model.getUserFromId((int) organ.getDonorId());
            List<User> matches = model.getMatchingUsers(organ, (int)ChronoUnit.MONTHS.between(donor.getDateOfBirth(), LocalDate.now()), donor.getBloodType());
            List<User> topMatches = new ArrayList<>();
            if (matches.size() != 0) {
                matches = getBestMatches(donor.getRegionOfDeath(), matches, organ);
                for (User user : matches) {
                    if (user.getName() != null && user.getName().toLowerCase().contains(receiverNameQuery.toLowerCase())) {
                        topMatches.add(user);
                    }
                }
            }
            return topMatches;
        } catch (SQLException e){
            e.printStackTrace();
            Server.getInstance().log.debug("Error communicating with the database");
            return new ArrayList<>();
        }
    }

    /**
     * Compares to users based on waiting time
     */
    class WaitingComparator implements Comparator<User> {
        private DonatableOrgan organ;

        WaitingComparator(DonatableOrgan organ) {
            this.organ = organ;
        }

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
                System.out.println("Available organs comparator is broken");
                return 0;
            }
        }
    }
}
