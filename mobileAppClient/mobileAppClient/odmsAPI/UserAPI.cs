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
using System.IO;
using mobileAppClient.Models;

namespace mobileAppClient.odmsAPI
{
    /*
     * Holds methods that interface to the /user endpoint in the ODMS API
     */
    public class UserAPI
    {

        /*
         * Function which returns the HTTPStatusCode of doing a call to the server
         * to retrieve the user's profile photo.
         */
        public async Task<HttpStatusCode> GetUserPhoto()
        {
            if (!ServerConfig.Instance.IsConnectedToInternet())
            {
                return HttpStatusCode.ServiceUnavailable;
            }
            // Fetch the url and client from the server config class
            String url = ServerConfig.Instance.serverAddress;
            HttpClient client = ServerConfig.Instance.client;

            // Get the single userController instance
            UserController userController = UserController.Instance;

            //Create the request with token as a header
            var request = new HttpRequestMessage(new HttpMethod("GET"), url + "/users/" + userController.LoggedInUser.id + "/photo");
            request.Headers.Add("token", UserController.Instance.AuthToken);

            HttpResponseMessage response;

            try
            {
                response = await client.SendAsync(request);
            }
            catch (HttpRequestException)
            {
                return HttpStatusCode.ServiceUnavailable;
            }

            if (response.StatusCode == HttpStatusCode.OK)
            {

                var responseContent = await response.Content.ReadAsStringAsync();
                Photo receievedPhoto = new Photo(responseContent);
                userController.photoObject = receievedPhoto;

                byte[] bytes = Convert.FromBase64String(receievedPhoto.data);

                ImageSource source = ImageSource.FromStream(() => new MemoryStream(bytes));

                userController.ProfilePhotoSource = source;

                Console.WriteLine("Successfully received profile photo for user id " + userController.LoggedInUser.id);
                return HttpStatusCode.OK;
            }
            else
            {
                Console.WriteLine(String.Format("Failed to retrieve photo for user id ({0}) ({1})", userController.LoggedInUser.id, response.StatusCode));
                return response.StatusCode;
            }

        }

        /*
         * Function which returns the string of the photo based on the users id
         * to retrieve the user's profile photo.
         * Used for maps.
         */
        public async Task<Tuple<HttpStatusCode, string>> GetUserPhotoForMapObjects(int id)
        {
            if (!ServerConfig.Instance.IsConnectedToInternet())
            {
                return new Tuple<HttpStatusCode, string>(HttpStatusCode.ServiceUnavailable, "");
            }
            // Fetch the url and client from the server config class
            String url = ServerConfig.Instance.serverAddress;
            HttpClient client = ServerConfig.Instance.client;

            //Create the request with token as a header
            var request = new HttpRequestMessage(new HttpMethod("GET"), url + "/users/" + id + "/photo");
            request.Headers.Add("token", ClinicianController.Instance.AuthToken);

            HttpResponseMessage response;

            try
            {
                response = await client.SendAsync(request);
            }
            catch (HttpRequestException)
            {
                return new Tuple<HttpStatusCode, string>(HttpStatusCode.ServiceUnavailable, "");
            }

            if (response.StatusCode == HttpStatusCode.OK)
            {

                var responseContent = await response.Content.ReadAsStringAsync();

                Console.WriteLine("Successfully received profile photo for user id " + id);
                return new Tuple<HttpStatusCode, string>(HttpStatusCode.OK, responseContent);
            }
            else
            {
                Console.WriteLine(String.Format("Failed to retrieve photo for user id ({0}) ({1})", id, response.StatusCode));
                return new Tuple<HttpStatusCode, string>(response.StatusCode, "");
            }

        }

        /*
         * Returns the status of updating a user object to the server
         */
        public async Task<HttpStatusCode> UpdateUser(bool isClinicianMakingUpdate)
        {
            if (isClinicianMakingUpdate) {
                        return await UpdateUser(UserController.Instance.LoggedInUser,
                            ClinicianController.Instance.AuthToken);
            } else {
                        return await UpdateUser(UserController.Instance.LoggedInUser,
                            UserController.Instance.AuthToken);
            }
        }

        public async Task<HttpStatusCode> UpdateUser(User user, String token)
        {
            if (!ServerConfig.Instance.IsConnectedToInternet())
            {
                return HttpStatusCode.ServiceUnavailable;
            }
            // Fetch the url and client from the server config class
            String url = ServerConfig.Instance.serverAddress;
            HttpClient client = ServerConfig.Instance.client;

            //User History Items are not currently configured thus must send as an empty list.
            //UserController.Instance.LoggedInUser.userHistory = new List<HistoryItem>();

            String registerUserRequestBody = JsonConvert.SerializeObject(user);
            HttpContent body = new StringContent(registerUserRequestBody);

            Console.WriteLine(registerUserRequestBody);

            var request = new HttpRequestMessage(new HttpMethod("PATCH"), url + "/users/" + user.id);
            request.Content = body;

            request.Headers.Add("token", token);

            try
            {
                var response = await client.SendAsync(request);
                Console.Write("Update user response: " + response);

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
            catch (HttpRequestException)
            {
                return HttpStatusCode.ServiceUnavailable;
            }
        }

        /*
         * Function which returns the HTTPStatusCode of doing a call to the server
         * to retrieve the user's profile photo.
         */
        public async Task<HttpStatusCode> UpdateUserPhoto()
        {
            if (!ServerConfig.Instance.IsConnectedToInternet())
            {
                return HttpStatusCode.ServiceUnavailable;
            }
            // Fetch the url and client from the server config class
            String url = ServerConfig.Instance.serverAddress;
            HttpClient client = ServerConfig.Instance.client;

            // Get the single userController instance
            UserController userController = UserController.Instance;

            //Create the request with token as a header
            String uploadPhotoBody = JsonConvert.SerializeObject(UserController.Instance.photoObject);
            HttpContent body = new StringContent(uploadPhotoBody);

            int userId = UserController.Instance.LoggedInUser.id;

            var request = new HttpRequestMessage(new HttpMethod("PATCH"), url + "/users/" + userId + "/photo");
            request.Content = body;
            request.Headers.Add("token", UserController.Instance.AuthToken);

            try
            {
                var response = await client.SendAsync(request);

                if (response.StatusCode == HttpStatusCode.OK)
                {
                    Console.WriteLine("Success on editing user photo");
                    return HttpStatusCode.OK;
                }
                else
                {
                    Console.WriteLine(String.Format("Failed update user photo ({0})", response.StatusCode));
                    return response.StatusCode;
                }
            }
            catch (HttpRequestException)
            {
                return HttpStatusCode.ServiceUnavailable;
            }

        }

        /*
         * Function which returns the HTTPStatusCode of doing a call to the server
         * to delete the user's profile photo.
         */
        public async Task<HttpStatusCode> DeleteUserPhoto()
        {
            if (!ServerConfig.Instance.IsConnectedToInternet())
            {
                return HttpStatusCode.ServiceUnavailable;
            }
            // Fetch the url and client from the server config class
            String url = ServerConfig.Instance.serverAddress;
            HttpClient client = ServerConfig.Instance.client;

            // Get the single userController instance
            UserController userController = UserController.Instance;

            //Create the request with token as a header
            var request = new HttpRequestMessage(new HttpMethod("DELETE"), url + "/users/" + userController.LoggedInUser.id + "/photo");
            request.Headers.Add("token", UserController.Instance.AuthToken);

            HttpResponseMessage response;

            try
            {
                response = await client.SendAsync(request);
            }
            catch (HttpRequestException)
            {
                return HttpStatusCode.ServiceUnavailable;
            }

            if (response.StatusCode == HttpStatusCode.OK)
            {

                var responseContent = await response.Content.ReadAsStringAsync();
                Photo receievedPhoto = new Photo(responseContent);
                userController.photoObject = null;
                userController.ProfilePhotoSource = null;

                Console.WriteLine("Successfully deleted profile photo for user id " + userController.LoggedInUser.id);
                return HttpStatusCode.OK;
            }
            else
            {
                Console.WriteLine(String.Format("Failed to delete photo for user id ({0}) ({1})", userController.LoggedInUser.id, response.StatusCode));
                return response.StatusCode;
            }

        }

        /// <summary>
        /// Fetches a list of users from startIndex, with a max length of count
        /// </summary>
        /// <param name="startIndex"></param>
        /// <param name="count"></param>
        /// <returns>
        /// Tuple containing the HTTP return code and the list of User objects
        /// </returns>
        public async Task<Tuple<HttpStatusCode, List<User>>> GetUsers(int startIndex, int count, string searchQuery)
        {
            // Check internet connection
            List<User> resultUsers = new List<User>();
            if (!ServerConfig.Instance.IsConnectedToInternet())
            {
                return new Tuple<HttpStatusCode, List<User>>(HttpStatusCode.ServiceUnavailable, resultUsers);
            }

            // Fetch the url and client from the server config class
            String url = ServerConfig.Instance.serverAddress;
            HttpClient client = ServerConfig.Instance.client;

            String queries = null;

            queries = String.Format("?startIndex={0}&count={1}", startIndex, count);
            queries += processSearchQuery(searchQuery);

            HttpResponseMessage response;
            var request = new HttpRequestMessage(new HttpMethod("GET"), url + "/users" + queries);
            request.Headers.Add("token", ClinicianController.Instance.AuthToken);

            try
            {
                response = await client.SendAsync(request);
            }
            catch (HttpRequestException)
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

        /// <summary>
        /// Parses and checks the string search query and adapts the query for region or name
        /// </summary>
        /// <param name="query"></param>
        /// <returns></returns>
        private string processSearchQuery(string query)
        {
            if (string.IsNullOrEmpty(query))
            {
                return "";
            }

            query = query.ToLower();
            List<string> team300RegionList = new List<string>(new string[] { "auckland", "northland", "waikato", "bay of plenty", "bop"});

            if (team300RegionList.Contains(query))
            {
                return String.Format("&region={0}", query);
            } else
            {
                return String.Format("&name={0}", query);
            }

        }

        /// <summary>
        /// Gets the amount of users currently stored in the DB
        /// </summary>
        /// <returns>
        /// Number of users in DB
        /// </returns>
        public async Task<Tuple<HttpStatusCode, int>> GetUserCount()
        {
            if (!ServerConfig.Instance.IsConnectedToInternet())
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
            catch (HttpRequestException)
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

        /// <summary>
        /// Check if the username OR email already exists in the DB
        /// </summary>
        /// <param name="usernameEmail"></param>
        /// <returns>
        /// Tuple containing the HTTP status code and if the username/email is unique
        /// </returns>
        public async Task<Tuple<HttpStatusCode, bool>> isUniqueUsernameEmail(string usernameEmail)
        {
            bool isUnique = false;
            // Check internet connection
            List<User> resultUsers = new List<User>();
            if (!ServerConfig.Instance.IsConnectedToInternet())
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
            catch (HttpRequestException)
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

        /// <summary>
        /// Check if the username OR email already exists in the DB
        /// </summary>
        /// <param name="usernameEmail"></param>
        /// <returns>
        /// Tuple containing the HTTP status code and if the username/email is unique
        /// </returns>
        public async Task<Tuple<HttpStatusCode, bool>> isUniqueApiId(string api_id)
        {
            bool isUnique = false;
            // Check internet connection
            List<User> resultUsers = new List<User>();
            if (!ServerConfig.Instance.IsConnectedToInternet())
            {
                return new Tuple<HttpStatusCode, bool>(HttpStatusCode.ServiceUnavailable, isUnique);
            }

            // Fetch the url and client from the server config class
            String url = ServerConfig.Instance.serverAddress;
            HttpClient client = ServerConfig.Instance.client;

            string queries = String.Format("?api_id={0}", api_id);

            HttpResponseMessage response;
            var request = new HttpRequestMessage(new HttpMethod("GET"), url + "/unique" + queries);

            try
            {
                response = await client.SendAsync(request);
            }
            catch (HttpRequestException)
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

        /// <summary>
        /// Fetches a list of custom organ objects used for the map view
        /// </summary>
        /// <returns>
        /// Tuple containing the HTTP return code and the list of Custom Organ objects
        /// </returns>
        public async Task<Tuple<HttpStatusCode, List<CustomMapObject>>> GetOrgansForMap()
        {
            // Check internet connection
            List<CustomMapObject> resultMapObjects = new List<CustomMapObject>();
            if (!ServerConfig.Instance.IsConnectedToInternet())
            {
                return new Tuple<HttpStatusCode, List<CustomMapObject>>(HttpStatusCode.ServiceUnavailable, resultMapObjects);
            }

            // Fetch the url and client from the server config class
            String url = ServerConfig.Instance.serverAddress;
            HttpClient client = ServerConfig.Instance.client;

            HttpResponseMessage response;
            var request = new HttpRequestMessage(new HttpMethod("GET"), url + "/mapObjects");
            request.Headers.Add("token", ClinicianController.Instance.AuthToken);

            try
            {
                response = await client.SendAsync(request);
            }
            catch (HttpRequestException)
            {
                return new Tuple<HttpStatusCode, List<CustomMapObject>>(HttpStatusCode.ServiceUnavailable, resultMapObjects);
            }

            if (response.StatusCode != HttpStatusCode.OK)
            {
                return new Tuple<HttpStatusCode, List<CustomMapObject>>(response.StatusCode, resultMapObjects);
            }

            string responseContent = await response.Content.ReadAsStringAsync();
            resultMapObjects = JsonConvert.DeserializeObject<List<CustomMapObject>>(responseContent);
            return new Tuple<HttpStatusCode, List<CustomMapObject>>(HttpStatusCode.OK, resultMapObjects);
        }

        /// <summary>
        /// Fetches a single user with a given id
        /// </summary>
        /// <returns>
        /// Tuple containing the HTTP return code and the User object
        /// </returns>
        public async Task<Tuple<HttpStatusCode, User>> GetSingleUser(string id)
        {
            // Check internet connection
            if (!ServerConfig.Instance.IsConnectedToInternet())
            {
                return new Tuple<HttpStatusCode, User>(HttpStatusCode.ServiceUnavailable, null);
            }

            // Fetch the url and client from the server config class
            String url = ServerConfig.Instance.serverAddress;
            HttpClient client = ServerConfig.Instance.client;

            HttpResponseMessage response;
            var request = new HttpRequestMessage(new HttpMethod("GET"), url + "/users/" + id);
            request.Headers.Add("token", ClinicianController.Instance.AuthToken);

            try
            {
                response = await client.SendAsync(request);
            }
            catch (HttpRequestException)
            {
                return new Tuple<HttpStatusCode, User>(HttpStatusCode.ServiceUnavailable, null);
            }

            if (response.StatusCode != HttpStatusCode.OK)
            {
                return new Tuple<HttpStatusCode, User>(response.StatusCode, null);
            }

            string responseContent = await response.Content.ReadAsStringAsync();
            User resultUser = JsonConvert.DeserializeObject<User>(responseContent);
            return new Tuple<HttpStatusCode, User>(HttpStatusCode.OK, resultUser);
        }



        public async Task<User> getUser(int userId, string token)
        {
            String url = ServerConfig.Instance.serverAddress;
            HttpClient client = ServerConfig.Instance.client;

            var request = new HttpRequestMessage(new HttpMethod("GET"), url + "/users/" + userId);
            request.Headers.Add("token", token);

            var response = await client.SendAsync(request);
            string body = await response.Content.ReadAsStringAsync();

            try
            {
                return JsonConvert.DeserializeObject<User>(body);
            }
            catch (JsonSerializationException)
            {
                return null;
            }
        }

        public async Task<HttpStatusCode> updateAccountSettings(User user, string token, bool setPassword)
        {
            String url = ServerConfig.Instance.serverAddress;
            HttpClient client = ServerConfig.Instance.client;

            Newtonsoft.Json.Linq.JObject o;
            if (setPassword)
            {
                o = Newtonsoft.Json.Linq.JObject.FromObject(new
                {
                    username = user.username,
                    email = user.email,
                    password = user.password
                });
            } else
            {
                o = Newtonsoft.Json.Linq.JObject.FromObject(new
                {
                    username = user.username,
                    email = user.email
                });
            }

            String itemRequestBody = JsonConvert.SerializeObject(o);
            HttpContent body = new StringContent(itemRequestBody);

            var request = new HttpRequestMessage(new HttpMethod("PATCH"), url + "/users/" + user.id + "/account" );
            request.Content = body;

            request.Headers.Add("token", token);

            var response = await client.SendAsync(request);
            return response.StatusCode;
        }
    }
}
