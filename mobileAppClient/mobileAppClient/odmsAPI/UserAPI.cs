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
    /*
     * Holds methods that interface to the /user endpoint in the ODMS API
     */
    class UserAPI
    {

        public async Task<HttpStatusCode> GetUserPhoto()
        {
            if (!await ServerConfig.Instance.IsConnectedToInternet())
            {
                return HttpStatusCode.ServiceUnavailable;
            }
            // Fetch the url and client from the server config class
            String url = ServerConfig.Instance.serverAddress;
            HttpClient client = ServerConfig.Instance.client;

            // Get the single userController instance
            UserController userController = UserController.Instance;

            HttpResponseMessage response;

            try
            {
                response = await client.GetAsync(url + "/users/" + userController.LoggedInUser.id + "/photo");
            }
            catch (HttpRequestException e)
            {
                return HttpStatusCode.ServiceUnavailable;
            }

            if (response.StatusCode == HttpStatusCode.OK)
            {
                var responseContent = await response.Content.ReadAsStringAsync();

                Console.WriteLine(responseContent);

                //User user;
                //// If the profile received is not a user, return 401 to the Login screen
                //try
                //{
                //    user = JsonConvert.DeserializeObject<User>(responseContent);
                //}
                //catch (JsonSerializationException jse)
                //{
                //    return HttpStatusCode.Unauthorized;
                //}

                //userController.LoggedInUser = user;
                //IEnumerable<string> headerValues = response.Headers.GetValues("token");
                //var token = headerValues.FirstOrDefault();
                //userController.AuthToken = token;
                //Console.WriteLine("Logged in as " + String.Join(String.Empty, userController.LoggedInUser.name));
                return HttpStatusCode.OK;
            }
            else
            {
                Console.WriteLine(String.Format("Failed to retrieve photo for user id ({0}) ({1})", userController.LoggedInUser.id, response.StatusCode));
                return response.StatusCode;
            }
            
        }

        /*
         * Returns the status of updating a user object to the server
         */
        public async Task<HttpStatusCode> UpdateUser()
        {
            if (!await ServerConfig.Instance.IsConnectedToInternet())
            {
                return HttpStatusCode.ServiceUnavailable;
            }
            // Fetch the url and client from the server config class
            String url = ServerConfig.Instance.serverAddress;
            HttpClient client = ServerConfig.Instance.client;

            //User History Items are not currently configured thus must send as an empty list.
            //UserController.Instance.LoggedInUser.userHistory = new List<HistoryItem>();

            String registerUserRequestBody = JsonConvert.SerializeObject(UserController.Instance.LoggedInUser);
            HttpContent body = new StringContent(registerUserRequestBody);

            Console.WriteLine(registerUserRequestBody);

            int userId = UserController.Instance.LoggedInUser.id;

            Console.WriteLine(UserController.Instance.AuthToken);

            var request = new HttpRequestMessage(new HttpMethod("PATCH"), url + "/users/" + userId);
            request.Content = body;
            request.Headers.Add("token", UserController.Instance.AuthToken);

            Console.WriteLine(request);

            try
            {
                var response = await client.SendAsync(request);

                if (response.StatusCode == HttpStatusCode.Created)
                {
                    Console.WriteLine("Success on editing user");
                    return HttpStatusCode.Created;
                }
                else
                {
                    Console.WriteLine(String.Format("Failed update user ({0})", response.StatusCode));
                    return response.StatusCode;
                }
            }
            catch (HttpRequestException ex)
            {
                Console.WriteLine(ex);
                return HttpStatusCode.ServiceUnavailable;
            }
        }
    }
}
