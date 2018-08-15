using System;
using System.Globalization;
using Newtonsoft.Json;
using Xamarin.Forms;

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

        [JsonIgnore]
        public string DiagnosisDetailString
        {
            get
            {
                return "Diagnosed on " + this.diagnosisDate.day + " of " + new DateTimeFormatInfo().GetAbbreviatedMonthName(this.diagnosisDate.month) + ", " + this.diagnosisDate.year;
            }
        } 


        [JsonIgnore]
        public string CellText {
            get
            {
                if (this.isChronic)
                {
                    return this.name + " (CHRONIC)";
                }
                else
                {
                    return this.name;
                }
            }
        }

        [JsonIgnore]
        public Color CellColour {
            get
            {
                if (this.isChronic)
                {
                    return Color.Red;
                }
                else
                {
                    return Color.Blue;
                }
            }
        } 

        public Disease(string name, CustomDate date, bool Chronic, bool Cured)
        {
            this.name = name;
            this.diagnosisDate = date;
            this.isChronic = Chronic;
            this.isCured = Cured;
        }
    }
}
