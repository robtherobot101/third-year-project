using System;
<<<<<<< HEAD
=======
using mobileAppClient.odmsAPI.RequestFormat;
using Newtonsoft.Json;
using System.Collections.Generic;
using System.Linq;
>>>>>>> 77ee599... Clarified all documentation in the application and ensured documentation was to a high standard. #document
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

            string query = String.Format("{0}/{1}/", drugA, drugB);
            string reversedQuery = String.Format("{0}/{1}/", drugB, drugA);

            var response = await client.GetAsync(apiEndpoint + query);

            // Reverse query attempt if 202, I dont know their API needs this
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
