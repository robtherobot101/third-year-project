using System;
using System.Collections.Generic;

namespace mobileAppClient
{
    public class CustomMapObject
    {
        public string firstName { get; set; }
        public string middleName { get; set; }
        public string lastName { get; set; }
        public string gender { get; set; }
        public int id { get; set; }
        public string currentAddress { get; set; }
        public string region { get; set; }
        public string cityOfDeath { get; set; }
        public string regionOfDeath { get; set; }
        public string countryOfDeath { get; set; }
        public List<DonatableOrgan> organs { get; set; }
        public CustomDateTime dateOfDeath { get; set; }

        public CustomMapObject()
        {
        }
    }
}
