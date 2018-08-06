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
    public class UserAPI
    {
        /*
         * Returns the status of updating a user object to the server
         */
        public async Task<HttpStatusCode> UpdateUser(bool isClinician)
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

            if (isClinician)
            {
                request.Headers.Add("token", ClinicianController.Instance.AuthToken);
            } else
            {
                request.Headers.Add("token", UserController.Instance.AuthToken);

            }


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
                return HttpStatusCode.ServiceUnavailable;
            }
        }

        public async Task<Tuple<HttpStatusCode, List<User>>> GetUsers(int startIndex, int count)
        {
            // Check internet connection
            List<User> resultUsers = new List<User>();
            if (!await ServerConfig.Instance.IsConnectedToInternet())
            {
                return new Tuple<HttpStatusCode, List<User>>(HttpStatusCode.ServiceUnavailable, resultUsers);
            }

            // Fetch the url and client from the server config class
            String url = ServerConfig.Instance.serverAddress;
            HttpClient client = ServerConfig.Instance.client;

            String queries = null;

            queries = String.Format("?startIndex={0}&count={1}", startIndex, count);

            HttpResponseMessage response;
            var request = new HttpRequestMessage(new HttpMethod("GET"), url + "/users" + queries);
            request.Headers.Add("token", ClinicianController.Instance.AuthToken);

            try
            {
                response = await client.SendAsync(request);
            }
            catch (HttpRequestException e)
            {
                return new Tuple<HttpStatusCode, List<User>>(HttpStatusCode.ServiceUnavailable, resultUsers);
            }

            if (response.StatusCode != HttpStatusCode.OK)
            {
                return new Tuple<HttpStatusCode, List<User>>(response.StatusCode, resultUsers);
            }

            string responseContent = await response.Content.ReadAsStringAsync();
            resultUsers = JsonConvert.DeserializeObject<List<User>>(responseContent);
            return new Tuple<HttpStatusCode, List<User>>(HttpStatusCode.OK, resultUsers);
        }

        public async Task<Tuple<HttpStatusCode, int>> GetUserCount()
        {
            if (!await ServerConfig.Instance.IsConnectedToInternet())
            {
                return new Tuple<HttpStatusCode, int>(HttpStatusCode.ServiceUnavailable, 0);
            }

            // Fetch the url and client from the server config class
            String url = ServerConfig.Instance.serverAddress;
            HttpClient client = ServerConfig.Instance.client;

            HttpResponseMessage response;
            var request = new HttpRequestMessage(new HttpMethod("GET"), url + "/usercount");
            request.Headers.Add("token", ClinicianController.Instance.AuthToken);

            try
            {
                response = await client.SendAsync(request);
            }
            catch (HttpRequestException e)
            {
                return new Tuple<HttpStatusCode, int>(HttpStatusCode.ServiceUnavailable, 0);
            }

            if (response.StatusCode != HttpStatusCode.OK)
            {
                return new Tuple<HttpStatusCode, int>(response.StatusCode, 0);
            }

            string responseContent = await response.Content.ReadAsStringAsync();
            int userCount = Convert.ToInt32(responseContent);
            return new Tuple<HttpStatusCode, int>(HttpStatusCode.OK, userCount);
        }

        public async Task<Tuple<HttpStatusCode, bool>> isUniqueUsernameEmail(string usernameEmail)
        {
            bool isUnique = false;
            // Check internet connection
            List<User> resultUsers = new List<User>();
            if (!await ServerConfig.Instance.IsConnectedToInternet())
            {
                return new Tuple<HttpStatusCode, bool>(HttpStatusCode.ServiceUnavailable, isUnique);
            }

            // Fetch the url and client from the server config class
            String url = ServerConfig.Instance.serverAddress;
            HttpClient client = ServerConfig.Instance.client;

            string queries = String.Format("?usernameEmail={0}", usernameEmail);

            HttpResponseMessage response;
            var request = new HttpRequestMessage(new HttpMethod("GET"), url + "/unique" + queries);

            try
            {
                response = await client.SendAsync(request);
            }
            catch (HttpRequestException e)
            {
                return new Tuple<HttpStatusCode, bool>(HttpStatusCode.ServiceUnavailable, isUnique);
            }

            if (response.StatusCode != HttpStatusCode.OK)
            {
                return new Tuple<HttpStatusCode, bool>(response.StatusCode, isUnique);
            }
            
            string responseContent = await response.Content.ReadAsStringAsync();
            if (responseContent.Equals("true"))
            {
                isUnique = true;
            }
            return new Tuple<HttpStatusCode, bool>(HttpStatusCode.OK, isUnique);
        }
    }
}
