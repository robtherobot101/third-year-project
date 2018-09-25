using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.Net;
using System.Text;

namespace mobileAppClient.odmsAPI
{
    /*
     * Stores + parses details from results from the eHealth Drug Interaction API
     */
    public class DrugInteractionResult
    {
        public List<String> ageInteractions { get; set; }
        public List<String> durationInteractions { get; set; }
        public List<String> genderInteractions { get; set; }
        public bool gotInteractions { get; set; }
        public HttpStatusCode resultStatusCode { get; set; }

        /*
         * Main constructor that parses the JSON returned from the API
         */
        public DrugInteractionResult(String jsonString, int age, string gender)
        {
            ageInteractions = new List<string>();
            durationInteractions = new List<string>();
            genderInteractions = new List<string>();

            JObject joResponse = JObject.Parse(jsonString.Trim());
            JObject ageInteractionsRaw = (JObject)joResponse["age_interaction"];
            JObject durationInteractionsRaw = (JObject)joResponse["duration_interaction"];
            JObject genderInteractionsRaw = (JObject)joResponse["gender_interaction"];

            ageInteractions.Add("Based on age:");
            genderInteractions.Add("Based on gender:");
            switch (age)
            {
                case int n when (n >= 10 && n <= 19):
                    ageInteractions.AddRange(getEntries(ageInteractionsRaw, "10-19"));
                    break;

                case int n when (n >= 20 && n <= 29):
                    ageInteractions.AddRange(getEntries(ageInteractionsRaw, "20-29"));
                    break;

                case int n when (n >= 30 && n <= 39):
                    ageInteractions.AddRange(getEntries(ageInteractionsRaw, "30-39"));
                    break;

                case int n when (n >= 40 && n <= 49):
                    ageInteractions.AddRange(getEntries(ageInteractionsRaw, "40-49"));
                    break;

                case int n when (n >= 50 && n <= 59):
                    ageInteractions.AddRange(getEntries(ageInteractionsRaw, "50-59"));
                    break;

                case int n when (n >= 60):
                    ageInteractions.AddRange(getEntries(ageInteractionsRaw, "60+"));
                    break;

                default:
                    ageInteractions.AddRange(getEntries(ageInteractionsRaw, "nan"));
                    break;
            }


            if(gender != null) {
                if (gender.Equals("Male"))
                {
                    genderInteractions.AddRange(getEntries(genderInteractionsRaw, "male"));
                }
                else if (gender.Equals("Female"))
                {
                    genderInteractions.AddRange(getEntries(genderInteractionsRaw, "female"));
                }   
            } else {
                genderInteractions.Add("No gender interactions");
            }


            durationInteractions.Add("\n1 - 2 years");
            durationInteractions.AddRange(getEntries(durationInteractionsRaw, "1 - 2 years"));

            durationInteractions.Add("\n1 - 6 months");
            durationInteractions.AddRange(getEntries(durationInteractionsRaw, "1 - 6 months"));

            durationInteractions.Add("\n10+ years");
            durationInteractions.AddRange(getEntries(durationInteractionsRaw, "10+ years"));

            durationInteractions.Add("\n2 - 5 years");
            durationInteractions.AddRange(getEntries(durationInteractionsRaw, "2 - 5 years"));

            durationInteractions.Add("\n5 - 10 years");
            durationInteractions.AddRange(getEntries(durationInteractionsRaw, "5 - 10 years"));

            durationInteractions.Add("\n6 - 12 months");
            durationInteractions.AddRange(getEntries(durationInteractionsRaw, "6 - 12 months"));

            durationInteractions.Add("\n< 1 month");
            durationInteractions.AddRange(getEntries(durationInteractionsRaw, "< 1 month"));

            durationInteractions.Add("\nDuration not specified");
            durationInteractions.AddRange(getEntries(durationInteractionsRaw, "not specified"));
        }

        /*
         * Constructor used when the API fails in returning drug interactions
         */
        public DrugInteractionResult(bool gotInteractions, HttpStatusCode statusCode)
        {
            this.gotInteractions = gotInteractions;
            this.resultStatusCode = statusCode;
        }

        /*
         * Returns the string entries as a list from a certain root-level field from a given JObject
         */
        private List<String> getEntries(JObject jObject, string fieldName)
        {
            JArray interactions;
            try {
                interactions = (JArray)jObject[fieldName];
            } catch (NullReferenceException) {
                List<string> emptyInteractions = new List<string> { "No interactions" };
                return emptyInteractions;
            } 

            List<String> entries = new List<string>();
            if (interactions == null)
            {
                List<string> emptyInteractions = new List<string>{ "No interactions" };
                return emptyInteractions;
            }

            foreach (string str in interactions)
            {
                entries.Add(String.Format("- {0}", UppercaseFirst(str)));
            }
            return entries;
        }

        /*
         * Returns the string s with an uppercase first char
         */
        private string UppercaseFirst(string s)
        {
            if (string.IsNullOrEmpty(s))
            {
                return string.Empty;
            }
            char[] a = s.ToCharArray();
        
            a[0] = char.ToUpper(a[0]);
            return new string(a);
        }
    }

    
}
