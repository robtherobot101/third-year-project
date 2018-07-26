using System;
namespace mobileAppClient
{
    /*
     * Enum to handle all different genders of a user for both 
     * birth gender and gender identity
     */ 
    public enum Gender
    {
        MALE,
        FEMALE,
        NONBINARY
    }

    /*
     * Class to handle the conversion of Gender Enum values to usable strings as 
     * well as inverse of this (from string to BloodType)
     */ 
    public static class GenderExtensions
    {
       /*
        * Converts a given Gender Enum into a string that is easily read by our attribute Pickers
        */
        public static String ToString(this Gender value)
        {
            switch (value)
            {
                case Gender.MALE:
                    return "Male";
                case Gender.FEMALE:
                    return "Female";
                case Gender.NONBINARY:
                    return "Other";
                default:
                    // Unreachable
                    return null;
            }
        }

        /*
        * Converts a given gender string into a Gender enum that is stored in the User's details
        */
        public static Gender ToGender(this String value)
        {
            switch (value)
            {
                case "Male":
                    return Gender.MALE;
                case "Female":
                    return Gender.FEMALE;
                case "Other":
                    return Gender.NONBINARY;
                default:
                    // Unreachable
                    return Gender.MALE;
            }
        }
    }
}
