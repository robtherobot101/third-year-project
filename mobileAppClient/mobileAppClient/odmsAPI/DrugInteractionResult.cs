using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.Text;

namespace mobileAppClient.odmsAPI
{
    public class DrugInteractionResult
    {
        public List<String> ageInteractions { get; set; }
        public List<String> durationInteractions { get; set; }
        public List<String> genderInteractions { get; set; }

        public DrugInteractionResult(String jsonString, int age, Gender gender)
        {
            ageInteractions = new List<string>();
            durationInteractions = new List<string>();
            genderInteractions = new List<string>();

            JObject joResponse = JObject.Parse(jsonString.Trim());
            JObject ageInteractionsRaw = (JObject)joResponse["age_interaction"];
            JObject durationInteractionsRaw = (JObject)joResponse["duration_interaction"];
            JObject genderInteractionsRaw = (JObject)joResponse["gender_interaction"];

            switch (age)
            {
                case int n when (n >= 10 && n <= 19):
                    ageInteractions = getEntries(ageInteractionsRaw, "10-19");
                    break;

                case int n when (n >= 20 && n <= 29):
                    ageInteractions = getEntries(ageInteractionsRaw, "20-29");
                    break;

                case int n when (n >= 30 && n <= 39):
                    ageInteractions = getEntries(ageInteractionsRaw, "30-39");
                    break;

                case int n when (n >= 40 && n <= 49):
                    ageInteractions = getEntries(ageInteractionsRaw, "40-49");
                    break;

                case int n when (n >= 50 && n <= 59):
                    ageInteractions = getEntries(ageInteractionsRaw, "50-59");
                    break;

                case int n when (n >= 60):
                    ageInteractions = getEntries(ageInteractionsRaw, "60+");
                    break;

                default:
                    ageInteractions = getEntries(ageInteractionsRaw, "nan");
                    break;
            }

            if (gender == Gender.MALE)
            {
                genderInteractions = getEntries(genderInteractionsRaw, "male");
            } else if (gender == Gender.FEMALE)
            {
                genderInteractions = getEntries(genderInteractionsRaw, "female");
            }

            // TODO get duration interactions
        }

        private List<String> getEntries(JObject jObject, string fieldName)
        {
            JArray interactions = (JArray)jObject[fieldName];
            List<String> entries = new List<string>();
            foreach (string str in interactions)
            {
                entries.Add(str);
            }
            return entries;
        }
    }

    
}
