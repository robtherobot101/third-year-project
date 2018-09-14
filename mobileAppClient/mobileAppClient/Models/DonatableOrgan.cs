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

        public string getTimeRemaining() {
            string timeString = "Time until expire: ";
            DateTime now = DateTime.Now;
            DateTime timeOfDeathAsDateTime = timeOfDeath.ToDateTime();
            TimeSpan ts = (now - timeOfDeathAsDateTime);
            switch(organType.ToLower()) {
                case("heart"):
                    timeString += ts.ToString(@"hh\:mm\:ss");
                    break;
                case("intestine"):
                    timeString += ts.ToString(@"hh\:mm");
                    break;
                default:
                    timeString += ts.ToString(@"hh");
                    break;
            }

            return timeString;
        }


    }
}
