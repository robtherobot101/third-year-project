using System;
using System.Collections.Generic;

namespace mobileAppClient
{
    public class Procedure
    {
        public string Summary { get; set; }
        public string Description { get; set; }
        public CustomDate Date { get; set; }
        public List<String> OrgansAffected { get; set; }
        public int Id { get; set; }


        public Procedure()
        {
        }
    }
}
