using System;
using System.Collections.Generic;
namespace mobileAppClient
{
    /*
     * Class to handle Medications details with all relevant details from the server.
     */ 
    public class Medication
    {
        public string name { get; set; }
        public List<String> activeIngredients { get; set; }
        public List<String> history { get; set; }
        public string DetailString { get; set; }
        public int Id { get; set; }
    }
}
