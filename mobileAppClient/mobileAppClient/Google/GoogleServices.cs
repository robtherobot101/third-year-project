using System;
using System.Collections.Generic;
using System.Net;
using System.Net.Http;
using System.Text;
using System.Threading.Tasks;
using mobileAppClient.odmsAPI;
using Newtonsoft.Json;

namespace mobileAppClient.Google
{
    public static class GoogleServices
    {
        private static HttpClient client;
        private static readonly string redirect_uri = "http://csse-s302g3.canterbury.ac.nz/oauth2redirect";
        private static readonly string client_id = "990254303378-hompkeqv6gthfgaut6j0bipdu6bf9ef0.apps.googleusercontent.com";
        private static readonly string client_secret = "yHw2OvqSYK4ocE0SH5-AHfJc";

        public static string GetLoginAddr()
        {
            return
                $"https://accounts.google.com/o/oauth2/v2/auth?response_type=code&scope=email" +
                $"&redirect_uri={redirect_uri}" +
                $"&client_id={client_id}";
        }

        private static string GetTokenAddr(string code)
        {
            return $"https://www.googleapis.com/oauth2/v4/token?grant_type=authorization_code" +
                   $"&code={code}" +
                   $"&client_id={client_id}" +
                   $"&client_secret={client_secret}" +
                   $"&redirect_uri={redirect_uri}";
        }

        private static string GetProfileAddr(string token)
        {
            return $"https://www.googleapis.com/plus/v1/people/me" +
                   $"?access_token={token}";
        }

        private static async Task<string> GetUserToken(string code)
        {
            client = ServerConfig.Instance.client;

            HttpContent content = new StringContent("");
            HttpResponseMessage response = null;

            response = await client.PostAsync(GetTokenAddr(code), content);
            string rawToken = await response.Content.ReadAsStringAsync();

            dynamic tokenData = JsonConvert.DeserializeObject<dynamic>(rawToken);

            return tokenData.access_token;
        }

        public static async void GetUserProfile(string code)
        {
            client = ServerConfig.Instance.client;

            HttpResponseMessage response = null;

            string access_token = await GetUserToken(code);

            response = await client.GetAsync(GetProfileAddr(access_token));

            if (response.StatusCode == HttpStatusCode.OK)
            {

            }
            string rawProfile = await response.Content.ReadAsStringAsync();

            dynamic foundProfile = JsonConvert.DeserializeObject<dynamic>(rawProfile);

            string firstName = foundProfile.name.givenName;
            string lastName = foundProfile.name.familyName;
            string email = foundProfile.emails[0].value;
            string imageURL = foundProfile.image.url;
        }
    }
}
