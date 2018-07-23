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
        /*
         * Returns the status of updating a user object to the server
         */
        public async Task<HttpStatusCode> UpdateUser()
        {
            // Fetch the url and client from the server config class
            String url = ServerConfig.Instance.serverAddress;
            HttpClient client = ServerConfig.Instance.client;

            String registerUserRequestBody = JsonConvert.SerializeObject(UserController.Instance.LoggedInUser);
            HttpContent body = new StringContent(registerUserRequestBody);

            Console.WriteLine(registerUserRequestBody);

            int userId = UserController.Instance.LoggedInUser.id;

            var request = new HttpRequestMessage(new HttpMethod("PATCH"), url + "/users/" + userId);
            request.Content = body;

            try
            {
                var response = await client.SendAsync(request);

                if (response.StatusCode == HttpStatusCode.Created)
                {
                    Console.WriteLine("Success on editing user organs");
                    return HttpStatusCode.Created;
                }
                else
                {
                    Console.WriteLine(String.Format("Failed update user organs ({0})", response.StatusCode));
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
