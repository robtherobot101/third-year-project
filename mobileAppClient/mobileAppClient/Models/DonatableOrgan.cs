using System;
namespace mobileAppClient
{
    public class DonatableOrgan
    {
        public CustomDateTime timeOfDeath { get; set; }
        public string organType { get; set; }
        public int donorId { get; set; }
        public int id { get; set; }
        public bool expired { get; set; }

        public DonatableOrgan()
        {
        }

        public Tuple<string, int> getTimeRemaining() {
            string timeString = "Time until expire: ";
            DateTime now = DateTime.Now;
            DateTime timeOfDeathAsDateTime = timeOfDeath.ToDateTime();
            TimeSpan ts = (now - timeOfDeathAsDateTime);
            int timeBurning = ts.Seconds;
            int timeRemaining = 0;
            TimeSpan t;
            switch (organType.ToLower()) {
                case("heart"):
                case("lung"):
                    timeRemaining = 21600 - timeBurning;
                    t = TimeSpan.FromSeconds(timeRemaining);
                    timeString += t.ToString(@"hh\:mm\:ss");
                    break;
                case("pancreas"):
                case("liver"):
                    timeRemaining = 86400 - timeBurning;
                    t = TimeSpan.FromSeconds(timeRemaining);
                    timeString += t.ToString(@"hh\:mm\:ss");
                    break;
                case("kidney"):
                    timeRemaining = 259200 - timeBurning;
                    t = TimeSpan.FromSeconds(timeRemaining);
                    timeString += t.ToString(@"hh\:mm\:ss");
                    break;
                case ("intestine"):
                    timeRemaining = 36000 - timeBurning;
                    t = TimeSpan.FromSeconds(timeRemaining);
                    timeString += t.ToString(@"hh\:mm\:ss");
                    break;
                case ("cornea"):
                    timeRemaining = 604800 - timeBurning;
                    t = TimeSpan.FromSeconds(timeRemaining);
                    timeString += t.ToString(@"hh\:mm\:ss");
                    break;
                case("middle-ear"):
                case("skin"):
                case("bone-marrow"):
                    timeRemaining = 315360000 - timeBurning;
                    t = TimeSpan.FromSeconds(timeRemaining);
                    timeString += t.ToString(@"yy\:hh\:mm\:ss");
                    break;
                case ("connective-tissue"):
                    timeRemaining = 157680000 - timeBurning;
                    t = TimeSpan.FromSeconds(timeRemaining);
                    timeString += t.ToString(@"yy\:hh\:mm\:ss");
                    break;
            }


            return new Tuple<string, int>(timeString, timeRemaining);
        }


    }
}
