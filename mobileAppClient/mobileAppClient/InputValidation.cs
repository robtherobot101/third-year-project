using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Text.RegularExpressions;

namespace mobileAppClient
{
    public static class InputValidation
    {
        /*
         * Returns true if the given input string is valid (non-null/empty, alpha chars)
         */
        public static bool IsValidTextInput(string input, bool numbersAllowed)
        {
            // Check if empty
            if (string.IsNullOrEmpty(input))
                return false;

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

        public static bool IsValidNumericInput(string input, double lowerLimit, double upperLimit)
        {
            if (string.IsNullOrEmpty(input))
                return false;

            Regex decimalRegex = new Regex(@"^[0 - 9]([.][0 - 9]{ 1, 3 })?$");
            if (!decimalRegex.Match(input).Success)
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

        public static bool IsValidBloodPressure(string input)
        {
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
