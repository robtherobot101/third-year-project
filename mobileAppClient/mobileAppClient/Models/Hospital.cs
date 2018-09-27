using System;
using System.Collections.Generic;
using System.Text;

namespace mobileAppClient.Models
{
    /*
     * Class which holds information about a helicopter on the map
     */
    public class Hospital
    {
        public string name { get; set; }
        public string address { get; set; }
        public string region { get; set; }
        public string country { get; set; }
        public double latitude { get; set; }
        public double longitude { get; set; }
    }
}
