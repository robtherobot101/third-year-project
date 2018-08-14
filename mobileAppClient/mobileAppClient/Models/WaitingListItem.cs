using System;
namespace mobileAppClient
{
    /*
     * Stores details regarding a user's waiting list position for JSON (de)serialisation
     */
    public class WaitingListItem
    {
        public string organType { get; set; }
        public CustomDate organRegisteredDate { get; set; }
        public int id { get; set; }
        public int userId { get; set; }
        public CustomDate organDeregisteredDate { get; set; }
        public int organDeregisteredCode { get; set; }
        public string DetailString { get; set; }
        public Boolean isConflicting { get; set; }
        public Xamarin.Forms.Color CellColour { get; set; }

        public WaitingListItem()
        {
        }
    }
}
