using System;
using mobileAppClient.odmsAPI.RequestFormat;
using Newtonsoft.Json;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Threading.Tasks;
namespace mobileAppClient.odmsAPI
{
    /*
     * Handles communication with the eHealth drug interaction API
     */
    public class DrugInteractionAPI
    {
        private string apiEndpoint = "https://www.ehealthme.com/api/v1/drug-interaction/";

        /*
         * Returns a DrugInteractionResult based on two drug names
         */
        public async Task<DrugInteractionResult> RetrieveDrugInteractions(string drugA, string drugB)
        {
            HttpClient client = ServerConfig.Instance.client;
            User loggedInUser = UserController.Instance.LoggedInUser;

            // Removes whitespace from any drugs with a space -> fixes issue with fetching from API
            drugA = drugA.Replace(" ", "-");
            drugB = drugB.Replace(" ", "-");

            string query = String.Format("{0}/{1}/", drugA, drugB);
            string reversedQuery = String.Format("{0}/{1}/", drugB, drugA);

            HttpResponseMessage response;
            try
            {
                response = await client.GetAsync(apiEndpoint + query);
            } catch (HttpRequestException e) {
                return new DrugInteractionResult(false, HttpStatusCode.BadRequest);
            }

            // Reverse query attempt if 202, sometimes API will only work with a certain combination
            if (response.StatusCode == HttpStatusCode.Accepted)
            {
                response = await client.GetAsync(apiEndpoint + reversedQuery);
            }

            if (response.StatusCode != HttpStatusCode.OK)
            {
                return new DrugInteractionResult(false, response.StatusCode);
            }
            var responseContent = await response.Content.ReadAsStringAsync();

            DrugInteractionResult drugInteractionResult = new DrugInteractionResult(responseContent, Convert.ToInt32(loggedInUser.getAge()), loggedInUser.gender);
            return drugInteractionResult;
        }
    }
}
