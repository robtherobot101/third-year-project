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


namespace mobileAppClient.odmsAPI
{
    /*
     * Holds methods that interface to the /user endpoint in the ODMS API
     */
    class UserAPI
    {

        /*
         * Function which returns the HTTPStatusCode of doing a call to the server
         * to retrieve the user's profile photo.
         */ 
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

            //Create the request with token as a header
            var request = new HttpRequestMessage(new HttpMethod("GET"), url + "/users/" + userController.LoggedInUser.id + "/photo");
            request.Headers.Add("token", UserController.Instance.AuthToken);

            HttpResponseMessage response;

            try
            {
                response = await client.SendAsync(request);
            }
            catch (HttpRequestException e)
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

            String registerUserRequestBody = JsonConvert.SerializeObject(UserController.Instance.LoggedInUser);
            HttpContent body = new StringContent(registerUserRequestBody);

            Console.WriteLine(registerUserRequestBody);

            int userId = UserController.Instance.LoggedInUser.id;

            Console.WriteLine(UserController.Instance.AuthToken);

            var request = new HttpRequestMessage(new HttpMethod("PATCH"), url + "/users/" + userId);
            request.Content = body;
            request.Headers.Add("token", UserController.Instance.AuthToken);

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

        /*
         * Function which returns the HTTPStatusCode of doing a call to the server
         * to retrieve the user's profile photo.
         */
        public async Task<HttpStatusCode> UpdateUserPhoto()
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
            catch (HttpRequestException ex)
            {
                return HttpStatusCode.ServiceUnavailable;
            }

        }
    }
}
