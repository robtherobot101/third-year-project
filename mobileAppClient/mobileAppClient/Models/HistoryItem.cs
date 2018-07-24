using System;
namespace mobileAppClient
{
    /*
     * Class to handle history items coming from the server with all relevant details.
     */ 
    public class HistoryItem
    {
        public string Action { get; set; }
        public string Description { get; set; }
        public CustomDateTime DateTime { get; set; }

    }
}
