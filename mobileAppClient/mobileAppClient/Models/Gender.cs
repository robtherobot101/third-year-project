using System;
namespace mobileAppClient
{
    public enum Gender
    {
        MALE,
        FEMALE,
        NONBINARY
    }

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
