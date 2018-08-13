using System;
using System.Collections.Generic;
using System.Net;
using System.Net.Http;
using System.Text;
using mobileAppClient.odmsAPI;

namespace mobileAppClient.Google
{
    public static class GoogleServices
    {
        private static readonly string redirect_uri = "http://csse-s302g3.canterbury.ac.nz/oauth2redirect";
        private static readonly string client_id = "990254303378-hompkeqv6gthfgaut6j0bipdu6bf9ef0.apps.googleusercontent.com";
        private static readonly string client_secret = "yHw2OvqSYK4ocE0SH5-AHfJc";

        public static string GetLoginAPIRequest()
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

        public static async void GetUserProfile(string code)
        {
            HttpClient client = ServerConfig.Instance.client;

            HttpContent content = new StringContent("");
            HttpResponseMessage response = null;

            response = await client.PostAsync(GetTokenAddr(code), content);
            string token = await response.Content.ReadAsStringAsync();
            Console.WriteLine(response.StatusCode);
            Console.WriteLine(token);
        }
    }
}
