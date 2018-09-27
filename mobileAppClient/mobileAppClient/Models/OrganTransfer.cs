using mobileAppClient.Models;
using System;

namespace mobileAppClient.Models
{

    /// <summary>
    /// Holds information about an organ transfer
    /// </summary>
    public class OrganTransfer
    {
        public int id { get; set; }
        public double startLat { get; set; }
        public double startLon { get; set; }
        public double endLat { get; set; }
        public double endLon { get; set; }
        public long receiverId { get; set; }
        public CustomDateTime arrivalTime { get; set; }
        public Organ organType { get; set; }

        public OrganTransfer()
        {
        }
    }
}
