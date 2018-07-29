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
     * Holds methods that interface to the /login and registration endpoints in the ODMS API
     */
    public class LoginAPI
    {
        /*
         * Returns response status code of the attempted login 
         */
        public async Task<HttpStatusCode> LoginUser(String usernameEmail, String password)
        {
            if (!await ServerConfig.Instance.IsConnectedToInternet())
            {
                return HttpStatusCode.ServiceUnavailable;
            }

            // Get the single userController instance
            UserController userController = UserController.Instance;

            // Fetch the url and client from the server config class
            String url = ServerConfig.Instance.serverAddress;
            HttpClient client = ServerConfig.Instance.client;
            
            String queries = null;

            queries = String.Format("?usernameEmail={0}&password={1}", usernameEmail, password);

            HttpContent content = new StringContent("");
            HttpResponseMessage response;

            try
            {
                response = await client.PostAsync(url + "/login" + queries, content);
            } catch (HttpRequestException e)
            {
                return HttpStatusCode.ServiceUnavailable;
            }

            if (response.StatusCode == HttpStatusCode.OK)
            {
                var responseContent = await response.Content.ReadAsStringAsync();

                User user;
                // If the profile received is not a user, return 401 to the Login screen
                try {
                     user = JsonConvert.DeserializeObject<User>(responseContent);
                } catch (JsonSerializationException jse)
                {
                    return HttpStatusCode.Unauthorized;
                }
                
                userController.LoggedInUser = user;
                IEnumerable<string> headerValues = response.Headers.GetValues("token");
                var token = headerValues.FirstOrDefault();
                userController.AuthToken = token;
                Console.WriteLine("Logged in as " + String.Join(String.Empty, userController.LoggedInUser.name));
                return HttpStatusCode.OK;
            }
            else
            {
                Console.WriteLine(String.Format("Failed login ({0})", response.StatusCode));
                return response.StatusCode;
            }
        }

        /*
         * Returns response status code of the attempted user registration
         */
        public async Task<HttpStatusCode> RegisterUser(String firstName, String lastName, String email,
            String username, String password, DateTime dateOfBirthRaw)
        {
            if (!await ServerConfig.Instance.IsConnectedToInternet())
            {
                return HttpStatusCode.ServiceUnavailable;
            }
   
            // Get the single userController instance
            UserController userController = UserController.Instance;

            // Fetch the url and client from the server config class
            String url = ServerConfig.Instance.serverAddress;
            HttpClient client = ServerConfig.Instance.client;
            String registerUserRequestBody;

            RegisterRequest registerRequest = new RegisterRequest();
            
            registerRequest.name[0] = firstName;
            registerRequest.name[1] = "";
            registerRequest.name[2] = lastName;


            // Apply preferredName as the inputted names
            registerRequest.preferredName[0] = firstName;
            registerRequest.preferredName[1] = "";
            registerRequest.preferredName[2] = lastName;

            registerRequest.password = password;

            registerRequest.dateOfBirth = new CustomDate(dateOfBirthRaw);
            registerRequest.creationTime = new CustomDateTime(DateTime.Now);

            registerRequest.username = username;
            registerRequest.email = email;
           
            // Additional parameters on serialization needed to remove null email/username
            registerUserRequestBody = JsonConvert.SerializeObject(registerRequest,
                            Newtonsoft.Json.Formatting.None,
                            new JsonSerializerSettings
                            {
                                NullValueHandling = NullValueHandling.Ignore
                            });
            HttpContent body = new StringContent(registerUserRequestBody);
            Console.WriteLine(registerUserRequestBody);
            var response = await client.PostAsync(url + "/users", body);

            if (response.StatusCode == HttpStatusCode.Created)
            {
                Console.WriteLine("Success on creating user");
            }
            else
            {
                Console.WriteLine(String.Format("Failed register ({0})", response.StatusCode));
            }
            return response.StatusCode;
        }
    }
}
