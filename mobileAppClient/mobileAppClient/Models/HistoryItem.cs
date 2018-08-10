using System;
namespace mobileAppClient
{
    /*
     * Class to handle history items coming from the server with all relevant details.
     */ 
    public class HistoryItem
    {
        public string action { get; set; }
        public string description { get; set; }
        public CustomDateTime dateTime { get; set; }
        public int id { get; set; }

    }
}
