package seng302.Logic.Database;

import seng302.Config.DatabaseConfiguration;
import seng302.Controllers.CLIController;
import seng302.Model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

public class Administration {

    /**
     * method to call to the database to check if it is online
     * @throws SQLException when the connection to te database has an error
     */
    public void status() throws SQLException{
        try(Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            String query = "SELECT staff_id FROM ADMIN WHERE staff_id = 1";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet result = statement.executeQuery();
            result.next();
        }
    }


    /**
     * method to resample the database with default entries
     * @throws SQLException when the connection to te database has an error
     */
    public void resample() throws SQLException{
        ArrayList<User> allUsers = new ArrayList<>();
        User user1 = new User("Andy", new String[]{"Robert"}, "French", LocalDate.now(), "andy", "andy@andy.com", "andrew");
        allUsers.add(user1);
        User user2 = new User("Buzz", new String[]{"Buzzy"}, "Knight", LocalDate.now(), "buzz", "buzz@buzz.com", "drowssap");
        allUsers.add(user2);
        User user3 = new User("James", new String[]{"Mozza"}, "Morritt", LocalDate.now(), "mozza", "mozza@mozza.com", "mozza");
        allUsers.add(user3);
        User user4 = new User("Jono", new String[]{"Zilla"}, "Hills", LocalDate.now(), "jonozilla", "zilla@zilla.com", "zilla");
        allUsers.add(user4);
        User user5 = new User("James", new String[]{"Mackas"}, "Mackay", LocalDate.now(), "mackas", "mackas@mackas.com", "mackas");
        allUsers.add(user5);
        User user6 = new User("Nicky", new String[]{"The Dark Horse"}, "Zohrab-Henricks", LocalDate.now(), "nicky", "nicky@nicky.com", "nicky");
        allUsers.add(user6);
        User user7 = new User("Kyran", new String[]{"Playing Fortnite"}, "Stagg", LocalDate.now(), "kyran", "kyran@kyran.com", "fortnite");
        allUsers.add(user7);
        User user8 = new User("Andrew", new String[]{"Daveo"}, "Davidson", LocalDate.now(), "andrew", "andrew@andrew.com", "andrew");
        allUsers.add(user8);

        GeneralUser userDatabase = new GeneralUser();
        for (User user: allUsers) {
            userDatabase.insertUser(user);
        }

        try (Connection connection = DatabaseConfiguration.getInstance().getConnection()){
            PreparedStatement countryStatement = connection.prepareStatement("INSERT INTO `COUNTRIES`(`country`) VALUES ('Afghanistan'), ('Albania'), ('Algeria'), ('Andorra'), ('Angola'), ('Antigua & Deps'), ('Argentina'), ('Armenia'), ('Australia'), ('Austria'), ('Azerbaijan'), ('Bahamas'), ('Bahrain'), ('Bangladesh'), ('Barbados'), ('Belarus'), ('Belgium'), ('Belize'), ('Benin'), ('Bhutan'), ('Bolivia'), ('Bosnia Herzegovina'), ('Botswana'), ('Brazil'), ('Brunei'), ('Bulgaria'), ('Burkina'), ('Burundi'), ('Cambodia'), ('Cameroon'), ('Canada'), ('Cape Verde'), ('Central African Rep'), ('Chad'), ('Chile'), ('China'), ('Colombia'), ('Comoros'), ('Congo'), ('Congo {Democratic Rep}'), ('Costa Rica'), ('Croatia'), ('Cuba'), ('Cyprus'), ('Czech Republic'), ('Denmark'), ('Djibouti'), ('Dominica'), ('Dominican Republic'), ('East Timor'), ('Ecuador'), ('Egypt'), ('El Salvador'), ('Equatorial Guinea'), ('Eritrea'), ('Estonia'), ('Ethiopia'), ('Fiji'), ('Finland'), ('France'), ('Gabon'), ('Gambia'), ('Georgia'), ('Germany'), ('Ghana'), ('Greece'), ('Grenada'), ('Guatemala'), ('Guinea'), ('Guinea-Bissau'), ('Guyana'), ('Haiti'), ('Honduras'), ('Hungary'), ('Iceland'), ('India'), ('Indonesia'), ('Iran'), ('Iraq'), ('Ireland {Republic}'), ('Israel'), ('Italy'), ('Ivory Coast'), ('Jamaica'), ('Japan'), ('Jordan'), ('Kazakhstan'), ('Kenya'), ('Kiribati'), ('Korea North'), ('Korea South'), ('Kosovo'), ('Kuwait'), ('Kyrgyzstan'), ('Laos'), ('Latvia'), ('Lebanon'), ('Lesotho'), ('Liberia'), ('Libya'), ('Liechtenstein'), ('Lithuania'), ('Luxembourg'), ('Macedonia'), ('Madagascar'), ('Malawi'), ('Malaysia'), ('Maldives'), ('Mali'), ('Malta'), ('Marshall Islands'), ('Mauritania'), ('Mauritius'), ('Mexico'), ('Micronesia'), ('Moldova'), ('Monaco'), ('Mongolia'), ('Montenegro'), ('Morocco'), ('Mozambique'), ('Myanmar'), ('Namibia'), ('Nauru'), ('Nepal'), ('Netherlands'), ('New Zealand'), ('Nicaragua'), ('Niger'), ('Nigeria'), ('Norway'), ('Oman'), ('Pakistan'), ('Palau'), ('Panama'), ('Papua New Guinea'), ('Paraguay'), ('Peru'), ('Philippines'), ('Poland'), ('Portugal'), ('Qatar'), ('Romania'), ('Russia'), ('Rwanda'), ('St Kitts & Nevis'), ('St Lucia'), ('Saint Vincent & the Grenadines'), ('Samoa'), ('San Marino'), ('Sao Tome & Principe'), ('Saudi Arabia'), ('Senegal'), ('Serbia'), ('Seychelles'), ('Sierra Leone'), ('Singapore'), ('Slovakia'), ('Slovenia'), ('Solomon Islands'), ('Somalia'), ('South Africa'), ('South Sudan'), ('Spain'), ('Sri Lanka'), ('Sudan'), ('Suriname'), ('Swaziland'), ('Sweden'), ('Switzerland'), ('Syria'), ('Taiwan'), ('Tajikistan'), ('Tanzania'), ('Thailand'), ('Togo'), ('Tonga'), ('Trinidad & Tobago'), ('Tunisia'), ('Turkey'), ('Turkmenistan'), ('Tuvalu'), ('Uganda'), ('Ukraine'), ('United Arab Emirates'), ('United Kingdom'), ('United States'), ('Uruguay'), ('Uzbekistan'), ('Vanuatu'), ('Vatican City'), ('Venezuela'), ('Vietnam'), ('Yemen'), ('Zambia'), ('Zimbabwe');");
            countryStatement.executeUpdate();

            PreparedStatement regionStatement = connection.prepareStatement("INSERT INTO `NZREGIONS` (`NAME`) VALUES ('Northland'),('Auckland'),('Waikato'),('Bay of Plenty'),('Gisborne'),('Hawke''s Bay'),('Taranaki'),('Manawatu-Wanganui'),('Wellington'),('Tasman'),('Nelson'),('Marlborough'),('West Coast'),('Canterbury'),('Otago'),('Southland');");
            regionStatement.executeUpdate();
        }
    }

    /**
     * method to empty the database tables with no values
     * @throws SQLException when the connection to te database has an error
     */
    public void reset() throws SQLException {

        try(Connection connection = DatabaseConfiguration.getInstance().getConnection()) {

            String update = "DELETE FROM COUNTRIES";
            PreparedStatement statement = connection.prepareStatement(update);
            System.out.println("Reset of database (COUNTRIES): -> Successful -> Rows Removed: " + statement.executeUpdate());

            update = "DELETE FROM WAITING_LIST_ITEM";
            statement = connection.prepareStatement(update);
            System.out.println("Reset of database (WAITING_LIST_ITEM): -> Successful -> Rows Removed: " + statement.executeUpdate());

            update = "DELETE FROM TOKEN";
            statement = connection.prepareStatement(update);
            System.out.println("Reset of database (TOKEN): -> Successful -> Rows Removed: " + statement.executeUpdate());

            update = "DELETE FROM PROCEDURES";
            statement = connection.prepareStatement(update);
            System.out.println("Reset of database (PROCEDURE): -> Successful -> Rows Removed: " + statement.executeUpdate());

            update = "DELETE FROM MEDICATION";
            statement = connection.prepareStatement(update);
            System.out.println("Reset of database (MEDICATION): -> Successful -> Rows Removed: " + statement.executeUpdate());

            update = "DELETE FROM DONATION_LIST_ITEM";
            statement = connection.prepareStatement(update);
            System.out.println("Reset of database (DONATION_LIST_ITEM): -> Successful -> Rows Removed: " + statement.executeUpdate());

            update = "DELETE FROM DISEASE";
            statement = connection.prepareStatement(update);
            System.out.println("Reset of database (DISEASE): -> Successful -> Rows Removed: " + statement.executeUpdate());

            update = "DELETE FROM HISTORY_ITEM";
            statement = connection.prepareStatement(update);
            System.out.println("Reset of database (HISTORY_ITEM): -> Successful -> Rows Removed: " + statement.executeUpdate());

            update = "DELETE FROM ADMIN";
            statement = connection.prepareStatement(update);
            System.out.println("Reset of database (ADMIN): -> Successful -> Rows Removed: " + statement.executeUpdate());

            update = "DELETE FROM CLINICIAN";
            statement = connection.prepareStatement(update);
            System.out.println("Reset of database (CLINICIAN): -> Successful -> Rows Removed: " + statement.executeUpdate());

            update = "DELETE FROM USER";
            statement = connection.prepareStatement(update);
            System.out.println("Reset of database (USER): -> Successful -> Rows Removed: " + statement.executeUpdate());


            update = "ALTER TABLE USER AUTO_INCREMENT = 1";
            statement = connection.prepareStatement(update);
            System.out.println("Reset of AutoIncrement(USER): -> Successful -> " + statement.executeUpdate());

            update = "ALTER TABLE CLINICIAN AUTO_INCREMENT = 1";
            statement = connection.prepareStatement(update);
            System.out.println("Reset of AutoIncrement(CLINICIAN): -> Successful -> " + statement.executeUpdate());

            update = "ALTER TABLE ADMIN AUTO_INCREMENT = 1";
            statement = connection.prepareStatement(update);
            System.out.println("Reset of AutoIncrement(ADMIN): -> Successful -> " + statement.executeUpdate());

            update = "ALTER TABLE DISEASE AUTO_INCREMENT = 1";
            statement = connection.prepareStatement(update);
            System.out.println("Reset of AutoIncrement(DISEASE): -> Successful -> " + statement.executeUpdate());

            update = "ALTER TABLE DONATION_LIST_ITEM AUTO_INCREMENT = 1";
            statement = connection.prepareStatement(update);
            System.out.println("Reset of AutoIncrement(DONATION LIST ITEM): -> Successful -> " + statement.executeUpdate());

            update = "ALTER TABLE MEDICATION AUTO_INCREMENT = 1";
            statement = connection.prepareStatement(update);
            System.out.println("Reset of AutoIncrement(MEDICATION): -> Successful -> " + statement.executeUpdate());

            update = "ALTER TABLE PROCEDURES AUTO_INCREMENT = 1";
            statement = connection.prepareStatement(update);
            System.out.println("Reset of AutoIncrement(PROCEDURES): -> Successful -> " + statement.executeUpdate());

            update = "ALTER TABLE WAITING_LIST_ITEM AUTO_INCREMENT = 1";
            statement = connection.prepareStatement(update);
            System.out.println("Reset of AutoIncrement(WAITING LIST ITEM): -> Successful -> " + statement.executeUpdate());

            update = "ALTER TABLE HISTORY_ITEM AUTO_INCREMENT = 1";
            statement = connection.prepareStatement(update);
            System.out.println("Reset of AutoIncrement(HISTORY_ITEM): -> Successful -> " + statement.executeUpdate());

            String insert = "INSERT INTO CLINICIAN(username, password, name, work_address, region, staff_id) " +
                    "VALUES(?, ?, ?, ?, ?, ?)";
            statement = connection.prepareStatement(insert);
            statement.setString(1, "default");
            statement.setString(2, "default");
            statement.setString(3, "default");
            statement.setString(4, "default");
            statement.setString(5, "default");
            statement.setInt(6, 1);
            System.out.println("Inserting Default Clinician -> Successful -> Rows Added: " + statement.executeUpdate());

            insert = "INSERT INTO ADMIN(username, password, name, work_address, region, staff_id) " +
                    "VALUES(?, ?, ?, ?, ?, ?)";
            statement = connection.prepareStatement(insert);
            statement.setString(1, "admin");
            statement.setString(2, "default");
            statement.setString(3, "default");
            statement.setString(4, "default");
            statement.setString(5, "default");
            statement.setInt(6, 1);
            System.out.println("Inserting Default Admin -> Successful -> Rows Added: " + statement.executeUpdate());
        }
    }
}
