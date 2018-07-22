using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace mobileAppClient
{
    public static class InputValidation
    {
        /*
         * Returns true if the given input string is valid (non-null/empty, alpha chars)
         */
        public static bool IsValidTextInput(string input, bool nonAlphaAllowed)
        {
            // Check if empty
            if (string.IsNullOrEmpty(input))
                return false;

            // Check for numbers
            if (!nonAlphaAllowed && !input.All(char.IsLetter))
            {
                return false;
            }
            else
            {
                return true;
            }
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
