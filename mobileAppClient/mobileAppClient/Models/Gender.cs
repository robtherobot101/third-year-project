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
        public static String ToPickerString(this Gender value)
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
    }
}
