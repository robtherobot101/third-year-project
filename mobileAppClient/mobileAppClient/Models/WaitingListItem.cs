using mobileAppClient.Models;
using System;
namespace mobileAppClient
{
    /*
     * Stores details regarding a user's waiting list position for JSON (de)serialisation
     */
    public class WaitingListItem
    {
        public Organ organType { get; set; }
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

        public String deregisterReason()
        {
            String output;
            if (this.organDeregisteredCode == 1)
            {
                output = "Error Registering";
            }
            else if (this.organDeregisteredCode == 2)
            {
                output = "Disease Cured";
            }
            else if (this.organDeregisteredCode == 3)
            {
                output = "Receiver Deceased";
            }
            else
            {
                output = "Successful Transplant";
            }
            return output;
        }
    }
}
