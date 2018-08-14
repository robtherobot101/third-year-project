using System;
namespace mobileAppClient
{
    public class CustomMapObject
    {
        public string name { get; set; }
        public int userId { get; set; }
        public long timeOfDeath { get; set; }
        public string currentAddress { get; set; }
        public string region { get; set; }
        public string cityOfDeath { get; set; }
        public string regionOfDeath { get; set; }
        public string countryOfDeath { get; set; }

        public CustomMapObject()
        {
        }
    }
}
