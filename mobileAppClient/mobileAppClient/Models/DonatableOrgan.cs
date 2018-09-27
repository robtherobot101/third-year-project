using System;
using System.Collections.Generic;

namespace mobileAppClient
{
    /*
     * Class to handle information about an organ which can be donated
     */
    public class DonatableOrgan
    {
        public CustomDateTime timeOfDeath { get; set; }
        public string organType { get; set; }
        public int donorId { get; set; }
        public int id { get; set; }
        public bool expired { get; set; }
        public List<User> topReceivers { get; set; }
        public int inTransfer { get; set; }

        public DonatableOrgan()
        {
        }

        /*
         * Returns a tuple which defines the time remaining 
         * in string and long format
         */
        public Tuple<string, long> getTimeRemaining() {
            string timeString = "Time to expire: ";
            DateTime now = DateTime.Now;
            DateTime timeOfDeathAsDateTime = timeOfDeath.ToDateTimeWithSeconds();
            TimeSpan ts = (now - timeOfDeathAsDateTime);
            long timeBurning = Convert.ToInt64(ts.TotalSeconds);
            long timeRemaining = 0;
            TimeSpan t;
            switch (organType.ToLower()) {
                case("heart"):
                case("lung"):
                    timeRemaining = 21600 - timeBurning;
                    t = TimeSpan.FromSeconds(timeRemaining);
                    timeString += t.ToString(@"dd\:hh\:mm\:ss") + " days";
                    break;
                case("pancreas"):
                case("liver"):
                    timeRemaining = 86400 - timeBurning;
                    t = TimeSpan.FromSeconds(timeRemaining);
                    timeString += t.ToString(@"dd\:hh\:mm\:ss") + " days";
                    break;
                case("kidney"):
                    timeRemaining = 259200 - timeBurning;
                    t = TimeSpan.FromSeconds(timeRemaining);
                    timeString += t.ToString(@"dd\:hh\:mm\:ss") + " days";
                    break;
                case ("intestine"):
                    timeRemaining = 36000 - timeBurning;
                    t = TimeSpan.FromSeconds(timeRemaining);
                    timeString += t.ToString(@"dd\:hh\:mm\:ss") + " days";
                    break;
                case ("cornea"):
                    timeRemaining = 604800 - timeBurning;
                    t = TimeSpan.FromSeconds(timeRemaining);
                    timeString += t.ToString(@"dd\:hh\:mm\:ss") + " days";
                    break;
                case("ear"):
                case("skin"):
                case("bone"):
                    timeRemaining = 315360000 - timeBurning;
                    t = TimeSpan.FromSeconds(timeRemaining);
                    timeString += t.ToString(@"dd\:hh\:mm\:ss") + " days";
                    break;
                case ("tissue"):
                    timeRemaining = 157680000 - timeBurning;
                    t = TimeSpan.FromSeconds(timeRemaining);
                    timeString += t.ToString(@"dd\:hh\:mm\:ss") + " days";
                    break;
            }


            return new Tuple<string, long>(timeString, timeRemaining);
        }


    }
}
