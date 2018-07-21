using System;
namespace mobileAppClient
{
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

    public static class BloodTypeExtensions
    {
        /*
         * Converts a given BloodType Enum into a string that is easily read by our attribute Pickers
         */
        public static String ToPickerString(this BloodType value)
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
    }
}
