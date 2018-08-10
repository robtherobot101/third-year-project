using System;
namespace mobileAppClient
{
    /*
     * Class to handle Disease objects coming from the server with all relevant attributes. 
     */ 
    public class Disease
    {
        public string Name { get; set; }
        public CustomDate DiagnosisDate { get; set; }
        public bool IsChronic { get; set; }
        public bool IsCured { get; set; }
        public int Id { get; set; }
        public string DiagnosisDateString { get; set; }
        public string CellText { get; set; }
        public Xamarin.Forms.Color CellColour { get; set; }

        public Disease(string name, CustomDate date, bool Chronic, bool Cured)
        {
            this.Name = name;
            this.DiagnosisDate = date;
            this.IsChronic = Chronic;
            this.IsCured = Cured;
        }
    }
}
