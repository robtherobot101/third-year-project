using System;
using System.Collections.Generic;
namespace mobileAppClient
{
    /*
     * Class to handle Medications details with all relevant details from the server.
     */ 
    public class Medication
    {
        public string Name { get; set; }
        public List<String> ActiveIngredients { get; set; }
        public List<String> History { get; set; }
        public string DetailString { get; set; }
        public int Id { get; set; }


    }
}
