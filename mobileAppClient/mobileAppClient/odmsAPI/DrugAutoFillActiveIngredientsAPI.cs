using System;
using System.Collections.Generic;
using System.Net;
using System.Net.Http;
using System.Threading.Tasks;
using Newtonsoft.Json;

namespace mobileAppClient.odmsAPI
{
    /*
     * Handles api calls for drug interactions
     */
    public class DrugAutoFillActiveIngredientsAPI
    {
        public DrugAutoFillActiveIngredientsAPI()
        {
        }

        /// <summary>
        /// Fetches a medication response object with medication auto-fill suggestions
        /// </summary>
        /// <returns>
        /// The MedicationResponseObject
        /// </returns>
        public async Task<MedicationResponseObject> apiRequest(string url) 
        {
            if (!ServerConfig.Instance.IsConnectedToInternet())
            {
                Console.WriteLine("Not connected");
                return new MedicationResponseObject();
            }

            // Fetch the url and client from the server config class
            HttpClient client = ServerConfig.Instance.client;

            HttpResponseMessage response = null;
            var request = new HttpRequestMessage(new HttpMethod("GET"), url);
            request.Headers.Add("X-Mashape-Key", "yqCc8Xzox7mshwvnVGeVGRhqb5q7p1QFwldjsnkT3j48eJ4Zfj");
            request.Headers.Add("Accept", "application/json");

            try
            {
                response = await client.SendAsync(request);
            }
            catch (HttpRequestException e)
            {
                Console.WriteLine(e.StackTrace);
                return new MedicationResponseObject();
            }

            if (response.StatusCode != HttpStatusCode.OK)
            {
                Console.WriteLine("Status code was: " + response.StatusCode);
                return new MedicationResponseObject();
            }

            string responseContent = await response.Content.ReadAsStringAsync();
            MedicationResponseObject medicationsReturned = new MedicationResponseObject();
            try {
                medicationsReturned = JsonConvert.DeserializeObject<MedicationResponseObject>(responseContent);
            } catch (JsonSerializationException) {
                List<string> activeIngredientsList = JsonConvert.DeserializeObject<List<string>>(responseContent);
                medicationsReturned.activeIngredients = new List<string>();
                medicationsReturned.activeIngredients.AddRange(activeIngredientsList);
            }

            return medicationsReturned;

        }


        /*
         * Top level call for the api request. 
         * Removes spaces and percentages from the url
         */
        public async Task<MedicationResponseObject> autocomplete(string query)
        {
            query = query.Replace(" ", "+");
            query = query.Replace("%", "%25");
            MedicationResponseObject medicationsReturned = await apiRequest("https://iterar-mapi-us.p.mashape.com/api/autocomplete?query=" + query);
            return medicationsReturned;
        }


        /*
         * Top level call for the api request. 
         * Removes spaces and percentages from the url
         */
        public async Task<MedicationResponseObject> activeIngredients(string medicine)
        {
            medicine = medicine.Replace(" ", "+");
            medicine = medicine.Replace("%", "%25");
            MedicationResponseObject medicationsReturned = await apiRequest("https://iterar-mapi-us.p.mashape.com/api/" + medicine + "/substances.json");
            return medicationsReturned;
        }
    }
}
