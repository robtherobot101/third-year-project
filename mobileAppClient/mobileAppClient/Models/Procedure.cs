using System;
using System.Collections.Generic;
using System.Globalization;
using Newtonsoft.Json;

namespace mobileAppClient
{
    /*
     * Stores details regarding a user for JSON (de)serialisation
     */
    public class Procedure
    {
        public string summary { get; set; }
        public string description { get; set; }
        public CustomDate date { get; set; }
        public List<String> organsAffected { get; set; }

        [JsonIgnore]
        public string detailString
        {
            get
            {
                DateTimeFormatInfo dateTimeFormat = new DateTimeFormatInfo();
                if (this.date.ToDateTime() < DateTime.Today)
                {
                    // Was in past
                    return this.description + ", was due on " + this.date.day + " of " +
                           dateTimeFormat.GetAbbreviatedMonthName(this.date.month) + ", " + this.date.year;
                }
                else
                {
                    // Is in future
                    return this.description + ", due on " + this.date.day + " of " +
                           dateTimeFormat.GetAbbreviatedMonthName(this.date.month) + ", " + this.date.year;
                }
            }
        }

        public Procedure(string summary, string description, CustomDate date,
            List<String> organsAffected)
        {
            this.summary = summary;
            this.description = description;
            this.date = date;
    
            organsAffected = organsAffected.ConvertAll(org => org.ToUpper());
            this.organsAffected = organsAffected;
        }
    }

    
}
