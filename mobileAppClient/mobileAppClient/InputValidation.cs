using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Text.RegularExpressions;

namespace mobileAppClient
{
    /*
     * Class to handle all validation of input methods used 
     * widely across the application.
     */ 
    public static class InputValidation
    {
        /*
         * Returns true if the given input string is valid (non-null/empty, alpha chars)
         */
        public static bool IsValidTextInput(string input, bool numbersAllowed, bool emptyAllowed)
        {
            // Check if empty
            if (string.IsNullOrEmpty(input) && emptyAllowed)
            {
                return true;
            } else if (string.IsNullOrEmpty(input))
            {
                return false;
            }
                
            if (input.Length > 40)
            {
                return false;
            }

            // Check for numbers
            if (!numbersAllowed && (input.Any(char.IsDigit) || input.Any(char.IsSymbol)))
            {
                return false;
            }
            else
            {
                return true;
            }
        }

<<<<<<< HEAD
        public static bool IsValidNumericInput(string input, double lowerLimit, double upperLimit, bool emptyAllowed)
=======
        /*
         * Method to check if a given input is within a lower bound and an upper bound 
         * returning a boolean determining if it is valid or not.
         */ 
        public static bool IsValidNumericInput(string input, double lowerLimit, double upperLimit)
>>>>>>> 77ee599... Clarified all documentation in the application and ensured documentation was to a high standard. #document
        {
            // Check if empty
            if (string.IsNullOrEmpty(input) && emptyAllowed)
            {
                return true;
            }
            else if (string.IsNullOrEmpty(input))
            {
                return false;
            }

            try
            {
                double inputNumber = Convert.ToDouble(input);
                if (inputNumber < lowerLimit || inputNumber > upperLimit)
                {
                    return false;
                }
            } catch (Exception e)
            {
                Console.WriteLine("Error parsing string to double");
                return false;
            }
            return true;
        }

        /*
         * Method to check if the input into the blood pressure field is a 
         * valid blood pressure input.
         */ 
        public static bool IsValidBloodPressure(string input)
        {
            // Check if empty and allow it otherwise check the format
            if (string.IsNullOrEmpty(input))
            {
                return true;
            }

            Regex bloodPressureRegex = new Regex(@"^\d{1,3}\/\d{1,3}$");
            if (!bloodPressureRegex.Match(input).Success)
            {
                return false;
            }
            return true;
        }

        /*
         * Checks the validity of a given email. test@xamarin is valid for example
         */
        public static bool IsValidEmail(string email)
        {
            try
            {
                var addr = new System.Net.Mail.MailAddress(email);
                return addr.Address == email;
            }
            catch
            {
                return false;
            }
        }

        /*
         * Trims the whitespace of a given string, can handle null strings unlike String.Trim()
         */
        public static string Trim(this string value)
        {
            if (value != null)
            {
                return value.Trim();
            }
            return null;
        }
    }
}
