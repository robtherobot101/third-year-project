using System;
using System.Collections.Generic;
using System.Net;
using System.Net.Http;
using System.Threading.Tasks;

namespace mobileAppClient.odmsAPI
{
    public class DrugAutoFillActiveIngredientsAPI
    {
        public DrugAutoFillActiveIngredientsAPI()
        {
        }

        public async Task<List<String>> apiRequest(string url) 
        {
            if (!await ServerConfig.Instance.IsConnectedToInternet())
            {
                Console.WriteLine("Not connected");
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
            }

            if (response.StatusCode != HttpStatusCode.OK)
            {
                Console.WriteLine("Status code was: " + response.StatusCode);
            }

            string responseContent = await response.Content.ReadAsStringAsync();
            Console.WriteLine(responseContent);
            return new List<string>();

        }


        public async Task<List<string>> autocomplete(string query)
        {
            query = query.Replace(" ", "+");
            query = query.Replace("%", "%25");
            List<string> medications = await apiRequest("https://iterar-mapi-us.p.mashape.com/api/autocomplete?query=" + query);
            return medications;
        }

        ///**
        // * Gets all the active ingredients of a given medicine.
        // *
        // * @param medicine The medicine to get the active ingredients of.
        // * @return Returns the active ingredients as a string arraylist
        // */
        //public static String activeIngredients(String medicine)
        //{
        //    medicine = medicine.replace(" ", "+");
        //    medicine = medicine.replace("%", "%25");
        //    return apiRequest(String.format("https://iterar-mapi-us.p.mashape.com/api/%s/substances.json", medicine));
        //}

        ///**
        // * Sends the api requests to MAPI.
        // *
        // * @param url The api url to call.
        // * @return returns a String of the result of the api request.
        // */
        //private static String apiRequest(String url)
        //{
        //    try
        //    {
        //        HttpResponse<JsonNode> response = Unirest.get(url)
        //                .header("X-Mashape-Key", "yqCc8Xzox7mshwvnVGeVGRhqb5q7p1QFwldjsnkT3j48eJ4Zfj")
        //                .header("Accept", "application/json")
        //                .asJson();
        //        int n;
        //        n = response.getRawBody().available();
        //        byte[] bytes = new byte[n];
        //        response.getRawBody().read(bytes, 0, n);
        //        return new String(bytes, StandardCharsets.UTF_8);
        //    }
        //    catch (Exception e)
        //    {
        //        return "";
        //    }
        //}
    }
}
