using System;
namespace mobileAppClient
{
    public class HistoryItem
    {
        public string Action { get; set; }
        public string Description { get; set; }
        public CustomDateTime DateTime { get; set; }

        public HistoryItem()
        {
        }
    }
}
