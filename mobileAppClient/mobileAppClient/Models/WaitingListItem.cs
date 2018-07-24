using System;
namespace mobileAppClient
{
    /*
     * Stores details regarding a user's waiting list position for JSON (de)serialisation
     */
    public class WaitingListItem
    {
        public string OrganType { get; set; }
        public CustomDate OrganRegisteredDate { get; set; }
        public int Id { get; set; }
        public int UserId { get; set; }
        public CustomDate OrganDeregisteredDate { get; set; }
        public int OrganDeregisteredCode { get; set; }
        public string DetailString { get; set; }

        public WaitingListItem()
        {
        }
    }
}
