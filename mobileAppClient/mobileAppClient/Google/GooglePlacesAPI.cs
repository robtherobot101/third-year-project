using mobileAppClient.odmsAPI;
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.Net;
using System.Net.Http;
using System.Text;
using System.Threading.Tasks;

namespace mobileAppClient.Google
{
    class GooglePlacesAPI
    {
        String apiKey = "AIzaSyD7DEH6Klk3ZyduVyqbaVEyTscj4sp48PQ";
        String autoCompleteEndPoint = "https://maps.googleapis.com/maps/api/place/queryautocomplete/json";
        
        /*
         * Sends a request to the Google Places API and returns the JSON result as a String
         */
        private async Task<String> AutoCompleteRequest(String input, String type)
        {
            HttpClient client = ServerConfig.Instance.client;
            User loggedInUser = UserController.Instance.LoggedInUser;

            string query;
            if(type == "")
            {
                query = String.Format("input={0}&key={1}", input, apiKey);
            }
            else
            {
                query = String.Format("input={0}&type={1}&key={2}", input, type, apiKey);
            }

            Console.WriteLine("Query String: " + query);
            HttpResponseMessage response;
            try
            {
                response = await client.GetAsync(autoCompleteEndPoint + "?"+ query);
                if (response.StatusCode == HttpStatusCode.OK)
                {
                    return await response.Content.ReadAsStringAsync();
                } else
                {
                    Console.WriteLine("Google maps API returned: " + response.StatusCode.ToString());
                    return null;
                }
            }
            catch (HttpRequestException)
            {
                return null;
            }
        }

        /*
         * Queries the Google Places autocomplete API and returns a list of tuples with the parts of the address split
         * into two parts
         */
        public async Task<List<Tuple<String, String>>> AddressAutocomplete(List<String> tokens, List<String> acceptedPlaceTypes)
        {
            if (tokens.Count < 3)
            {
                return new List<Tuple<String, String>>();
            }

            String response;

            String query = String.Join(", ", tokens);
            tokens.Reverse();
            String partialToken = (tokens.Count == 0) ? "" : tokens[0];

            if (acceptedPlaceTypes.Count == 1)
            {
                response = await AutoCompleteRequest(query, acceptedPlaceTypes[0]);
            } else
            {
                response = await AutoCompleteRequest(query, "");
            }

            List<Tuple<String, String>> results = new List<Tuple<String, String>>();
            if (response == null)
            {
                return new List<Tuple<String, String>>();
            }

            try
            {
                JObject joResponse = JObject.Parse(response);
                JArray predictions = (JArray)joResponse["predictions"];

                foreach (JObject prediction in predictions)
                {
                    String desc = (String)prediction["description"];
                    Console.WriteLine("Returned: " + desc);
                    List<String> ResponseTypes = ((JArray)prediction["types"]).ToObject<List<String>>();
                    Boolean isAcceptedType = false;
                    foreach(String type in acceptedPlaceTypes)
                    {
                        if (ResponseTypes.Contains(type))
                        {
                            isAcceptedType = true;
                        }
                    }
                    if (isAcceptedType)
                    {
                        JObject structuredFormatting = (JObject)prediction["structured_formatting"];
                        String mainText = (String)structuredFormatting["main_text"];
                        String secondaryText = (String)structuredFormatting["secondary_text"];
                        List<String> addressAreaTokens = unMatchedTokens(secondaryText, tokens.GetRange(1, tokens.Count - 1));
                        if(mainText.ToLower().Contains(partialToken.ToLower()))
                        {
                            Boolean allMatched = true;
                            foreach(String token in tokens.GetRange(1, tokens.Count - 1))
                            {
                                if(desc.ToLower().Contains(token.ToLower()) == false)
                                {
                                    allMatched = false;
                                }
                            }

                            if(allMatched)
                            {
                                Tuple<String, String> addressInfo = new Tuple<String, String>(mainText + ", " + String.Join(", ", addressAreaTokens), secondaryText);
                                results.Add(addressInfo);
                            }
                        }
                    }
                }
                return results;
            }
            catch (NullReferenceException)
            {
                return new List<Tuple<String, String>>();
            }
            catch (JsonReaderException)
            {
                return new List<Tuple<String, String>>();
            }
        }

        /*
         * Returns the subset of searchTokens which do not appear in secondaryText
         */
        private List<String> unMatchedTokens(String secondaryText, List<String> searchTokens)
        {

            List<String> unMatched = new List<String>();
            string[] areaTokens = secondaryText.Split(new string[] { ", " }, StringSplitOptions.None);
            foreach(String areaToken in areaTokens)
            {
                Boolean matched = false;
                foreach(String searchToken in searchTokens)
                {
                    if(searchToken.ToLower() == areaToken.ToLower())
                    {
                        matched = true;
                    }
                }

                if(matched == false)
                {
                    unMatched.Add(areaToken);
                }
            }
            return unMatched;
        }

        /*
         * Queries the Google Places autocomplete API for a city and returns a list of tuples with the 
         * city name in the firt position, and the rest of the area location in the second (...,region, country)
         */
        public async Task<List<Tuple<String, String>>> CityAutocomplete(List<String> tokens, List<String> acceptedPlaceTypes)
        {
            if(tokens.Count < 3)
            {
                return new List<Tuple<String, String>>();
            }

            String response;

            String query = String.Join(", ", tokens);
            tokens.Reverse();
            Console.WriteLine("Number of tokens = " + tokens.Count);
            String area = String.Join(", ", tokens.GetRange(1, tokens.Count - 1));
            String partialToken = tokens[0];

            if (acceptedPlaceTypes.Count == 1)
            {
                response = await AutoCompleteRequest(query, acceptedPlaceTypes[0]);
            }
            else
            {
                response = await AutoCompleteRequest(query, "");
            }

            List<Tuple<String, String>> results = new List<Tuple<String, String>>();
            if (response == null)
            {
                return new List<Tuple<String, String>>();
            }

            try
            {
                JObject joResponse = JObject.Parse(response);
                JArray predictions = (JArray)joResponse["predictions"];

                foreach (JObject prediction in predictions)
                {
                    String desc = (String)prediction["description"];
                    Console.WriteLine("Returned: " + desc);
                    List<String> ResponseTypes = ((JArray)prediction["types"]).ToObject<List<String>>();
                    Boolean isAcceptedType = false;
                    foreach (String type in acceptedPlaceTypes)
                    {
                        if (ResponseTypes.Contains(type))
                        {
                            isAcceptedType = true;
                        }
                    }
                    if (isAcceptedType)
                    {
                        JObject structuredFormatting = (JObject)prediction["structured_formatting"];
                        String mainText = (String)structuredFormatting["main_text"];
                        String secondaryText = (String)structuredFormatting["secondary_text"];
                        if (secondaryText.ToLower().Contains(area.ToLower()) &&
                           mainText.ToLower().Contains(partialToken.ToLower()))
                        {
                            Tuple<String, String> addressInfo = new Tuple<String, String>(mainText, secondaryText);
                            results.Add(addressInfo);
                        }
                    }
                }
                return results;
            }
            catch (NullReferenceException)
            {
                return new List<Tuple<String, String>>();
            }
            catch (JsonReaderException)
            {
                return new List<Tuple<String, String>>();
            }
        }
    }
}
