using System;
using System.Collections.Generic;

namespace mobileAppClient.odmsAPI
{
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
