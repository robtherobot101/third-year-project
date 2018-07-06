using System;
namespace mobileAppClient
{
    public class WaitingListItem
    {
        public string OrganType { get; set; }
        public CustomDate OrganRegisteredDate { get; set; }
        public int Id { get; set; }
        public int UserId { get; set; }
        public CustomDate OrganDeregisteredDate { get; set; }
        public int OrganDeregisteredCode { get; set; }

        public WaitingListItem()
        {
        }
    }
}
