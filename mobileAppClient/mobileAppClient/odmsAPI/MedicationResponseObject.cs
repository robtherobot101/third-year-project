using System;
using System.Collections.Generic;

namespace mobileAppClient.odmsAPI
{
    /*
     * Class which holds medication auto-fill siggestions and drug active ingredients
     */
    public class MedicationResponseObject
    {
        public string query { get; set; }
        public List<string> suggestions { get; set; }
        public List<string> activeIngredients { get; set; }


        public MedicationResponseObject()
        {
        }
    }
}
