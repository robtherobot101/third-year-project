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
    public class GoogleServices
    {
        private static HttpClient client;
        private static readonly string REDIRECT_URI = "https://csse-s302g3.canterbury.ac.nz/oauth2redirect";
        private static readonly string REDIRECT_URI_CHANGE_LOGIN = "https://csse-s302g3.canterbury.ac.nz/oauth2redirectChangeLogin";
        private static readonly string CLIENT_ID = "990254303378-hompkeqv6gthfgaut6j0bipdu6bf9ef0.apps.googleusercontent.com";
        private static readonly string CLIENT_SECRET = "yHw2OvqSYK4ocE0SH5-AHfJc";

        /*
         * Makes a call to the Google login page
         */
        public static string GetLoginAddr() //login
        {
            return
                $"https://accounts.google.com/o/oauth2/v2/auth?response_type=code&scope=email" +
                $"&redirect_uri={REDIRECT_URI}" +
                $"&client_id={CLIENT_ID}";
        }

        
        public static string ChangeToGoogleLoginAddr()
        {
            return
                $"https://accounts.google.com/o/oauth2/v2/auth?response_type=code&scope=email" +
                $"&redirect_uri={REDIRECT_URI_CHANGE_LOGIN}" +
                $"&client_id={CLIENT_ID}";
        }


        private static string GetTokenAddr(string code, string redirect_uri) 
        {
            return $"https://www.googleapis.com/oauth2/v4/token?grant_type=authorization_code" +
                   $"&code={code}" +
                   $"&client_id={CLIENT_ID}" +
                   $"&client_secret={CLIENT_SECRET}" +
                   $"&redirect_uri={redirect_uri}";
        }

        private static string GetProfileAddr(string token)
        {
            return $"https://www.googleapis.com/plus/v1/people/me" +
                   $"?access_token={token}";
        }

        /*
         * Makes an api call and returns a user access token
         */
        private static async Task<string> GetUserToken(string code, string redirect_uri)
        {
            client = ServerConfig.Instance.client;

            HttpContent content = new StringContent("");
            HttpResponseMessage response = null;

            response = await client.PostAsync(GetTokenAddr(code, redirect_uri), content);
            string rawToken = await response.Content.ReadAsStringAsync();

            GoogleTokenResponse tokenData = JsonConvert.DeserializeObject<GoogleTokenResponse>(rawToken);

            return tokenData.access_token;
        }

        /// <summary>
        /// Returns the User object and profile image URL of the given authorization code
        /// </summary>
        /// <param name="code"></param>
        /// <returns></returns>
        public static async Task<Tuple<User, string, string>> GetUserProfile(string code)
        {
            client = ServerConfig.Instance.client;

            HttpResponseMessage response = null;

            string access_token = await GetUserToken(code, REDIRECT_URI);

            response = await client.GetAsync(GetProfileAddr(access_token));

            if (response.StatusCode == HttpStatusCode.OK)
            {

            }
            string rawProfile = await response.Content.ReadAsStringAsync();
            Console.WriteLine(rawProfile);

            GoogleTokenResponse foundProfile = JsonConvert.DeserializeObject<GoogleTokenResponse>(rawProfile);

            string firstName = foundProfile.name.givenName;
            string lastName = foundProfile.name.familyName;
            string email = foundProfile.emails[0].value;
            string id = foundProfile.id;

            // TODO update image
            string imageURL = foundProfile.image.url;

            imageURL = imageURL.Substring(0, imageURL.Length - 6);
            imageURL += "?sz=1000";

            return new Tuple<User, string, string>(new User(
                firstName, lastName, email), imageURL, foundProfile.id);
        }

        /// <summary>
        /// Returns the User object and profile image URL of the given authorization code
        /// </summary>
        /// <param name="code"></param>
        /// <returns></returns>
        public static async Task<string> GetUserId(string code)
        {
            client = ServerConfig.Instance.client;

            HttpResponseMessage response = null;

            string access_token = await GetUserToken(code, REDIRECT_URI_CHANGE_LOGIN);

            response = await client.GetAsync(GetProfileAddr(access_token));

            if (response.StatusCode == HttpStatusCode.OK)
            {

            }
            string rawProfile = await response.Content.ReadAsStringAsync();
            Console.WriteLine(rawProfile);

            GoogleTokenResponse foundProfile = JsonConvert.DeserializeObject<GoogleTokenResponse>(rawProfile);
            return foundProfile.id;
        }
    }
}
