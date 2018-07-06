using System;
using System.Collections.Generic;
namespace mobileAppClient
{
    public class Medication
    {
        public string Name { get; set; }
        public List<String> ActiveIngredients { get; set; }
        public List<String> History { get; set; }

        public Medication()
        {
        }
    }
}
