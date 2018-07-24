using System;
using System.Collections.Generic;

namespace mobileAppClient
{
    /*
     * Stores details regarding a user for JSON (de)serialisation
     */
    public class Procedure
    {
        public string Summary { get; set; }
        public string Description { get; set; }
        public CustomDate Date { get; set; }
        public List<String> OrgansAffected { get; set; }
        public int Id { get; set; }
        public string DetailString { get; set; }
    }
}
