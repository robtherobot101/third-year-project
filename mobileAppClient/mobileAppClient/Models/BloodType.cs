using System;
namespace mobileAppClient
{
    /*
     * Enum to handle all different Blood Types of a user.
     */ 
    public enum BloodType
    {
        A_NEG,
        A_POS,
        B_NEG,
        B_POS,
        AB_NEG,
        AB_POS,
        O_NEG,
        O_POS
    }

    /*
     * Class to handle the conversion of BloodType Enum values to usable strings as 
     * well as inverse of this (from string to BloodType)
     */ 
    public static class BloodTypeExtensions
    {
        /*
         * Converts a given BloodType Enum into a string that is easily read by our attribute Pickers
         */
        public static String ToString(this BloodType value)
        {
            switch (value)
            {
                case BloodType.AB_NEG:
                    return "AB-";
                case BloodType.AB_POS:
                    return "AB+";
                case BloodType.A_NEG:
                    return "A-";
                case BloodType.A_POS:
                    return "A+";
                case BloodType.B_NEG:
                    return "B-";
                case BloodType.B_POS:
                    return "B+";
                case BloodType.O_NEG:
                    return "O-";
                case BloodType.O_POS:
                    return "O+";
                default:
                    // Unreachable
                    return null;
            }
        }

        /*
         * Converts a given string into a BloodType Enum used for when the user object is sent to the server.
         */ 
        public static BloodType ToBloodType(this string value)
        {
            switch (value)
            {
                case "AB-":
                    return BloodType.AB_NEG;
                case "AB+":
                    return BloodType.AB_POS;
                case "A-":
                    return BloodType.A_NEG;
                case "A+":
                    return BloodType.A_POS;
                case "B-":
                    return BloodType.B_NEG;
                case "B+":
                    return BloodType.B_POS;
                case "O-":
                    return BloodType.O_NEG;
                case "O+":
                    return BloodType.O_POS;
                default:
                    // Unreachable
                    return BloodType.O_POS;
            }
        }


        /*
         * Converts a given string into a BloodType Enum used for when the user object is recieved from the server.
         */
        public static BloodType ToBloodTypeJSON(this string value)
        {
            switch (value)
            {
                case "AB_NEG":
                    return BloodType.AB_NEG;
                case "AB_POS":
                    return BloodType.AB_POS;
                case "A_NEG":
                    return BloodType.A_NEG;
                case "A_POS":
                    return BloodType.A_POS;
                case "B_NEG":
                    return BloodType.B_NEG;
                case "B_POS":
                    return BloodType.B_POS;
                case "O_NEG":
                    return BloodType.O_NEG;
                case "O_POS":
                    return BloodType.O_POS;
                default:
                    // Unreachable
                    return BloodType.O_POS;
            }
        }
    }
}
