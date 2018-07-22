﻿using System;
using mobileAppClient.odmsAPI.RequestFormat;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Text;
using System.Threading.Tasks;
using Xamarin.Forms;
using Xamarin.Forms.Xaml;
namespace mobileAppClient.odmsAPI
{
    public class DrugInteractionAPI
    {
        private string apiEndpoint = "https://www.ehealthme.com/api/v1/drug-interaction/";

        public async Task<string> RetrieveDrugInteractions(string drugA, string drugB)
        {
            HttpClient client = ServerConfig.Instance.client;

            string query = String.Format("{0}/{1}/", drugA, drugB);
            string reversedQuery = String.Format("{0}/{1}/", drugB, drugA);

            var response = await client.GetAsync(apiEndpoint + query);
            var responseContent = await response.Content.ReadAsStringAsync();

            Console.WriteLine(responseContent);

            return responseContent;
            
        }

    }
}
