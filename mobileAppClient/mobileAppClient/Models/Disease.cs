using System;
namespace mobileAppClient
{
    /*
     * Class to handle Disease objects coming from the server with all relevant attributes. 
     */ 
    public class Disease
    {
        public string name { get; set; }
        public CustomDate diagnosisDate { get; set; }
        public bool isChronic { get; set; }
        public bool isCured { get; set; }
        public int id { get; set; }
        public string DiagnosisDateString { get; set; }
        public string CellText { get; set; }
        public Xamarin.Forms.Color CellColour { get; set; }

        public Disease(string name, int id)
        {
            this.name = name;
            this.id = id;
        }
    }
}
