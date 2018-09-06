using mobileAppClient.Models;
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
        /**
         * Returns response status code of the attempted login 
         */
        public async Task<HttpStatusCode> LoginUser(String usernameEmail, String password)
        {
            if (!await ServerConfig.Instance.IsConnectedToInternet())
            {
                return HttpStatusCode.ServiceUnavailable;
            }

            // Get the profile controller instances
            UserController userController = UserController.Instance;
            ClinicianController clinicianController = ClinicianController.Instance;

            // Fetch the url and client from the server config class
            String url = ServerConfig.Instance.serverAddress;
            HttpClient client = ServerConfig.Instance.client;
            
            String queries = null;

            queries = $"?identifier={usernameEmail}&password={password}";

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
                string responseContent = await response.Content.ReadAsStringAsync();


                if (IsClinician(responseContent))
                {
                    Clinician loggedInClinician = JsonConvert.DeserializeObject<Clinician>(responseContent);
                    string authToken = response.Headers.GetValues("token").FirstOrDefault();

                    ClinicianController.Instance.Login(loggedInClinician, authToken);

                    Console.WriteLine("Logged in as (CLINICIAN)" + string.Join(String.Empty, clinicianController.LoggedInClinician.name));

                    // Created code to signifiy clinician login internally
                    return HttpStatusCode.OK;
                }
                else if (IsUser(responseContent))
                {
                    // Login as the user
                    User loggedInUser = JsonConvert.DeserializeObject<User>(responseContent);
                    if (loggedInUser.dateOfDeath == null)
                    {
                        string authToken = response.Headers.GetValues("token").FirstOrDefault();
                        UserController.Instance.Login(loggedInUser, authToken);

                        Console.WriteLine("Logged in as (USER)" + String.Join(String.Empty, userController.LoggedInUser.name));

                        // OK code to signifiy user login internally
                        return HttpStatusCode.OK;
                    }
                    else
                    {
                        return HttpStatusCode.Conflict;
                    }
                }
                else
                {
                    // Must've attempted login as admin -> deny
                    return HttpStatusCode.BadRequest;
                }
            }
            else
            {
                Console.WriteLine(String.Format("Failed login (Server {0})", response.StatusCode));
                return response.StatusCode;
            }
        }

        /*
         * Returns whether or not the logout operation was successful
         */
        public async Task<HttpStatusCode> Logout(bool isClinician)
        {
            if (!await ServerConfig.Instance.IsConnectedToInternet())
            {
                return HttpStatusCode.ServiceUnavailable;
            }

            // Fetch the url and client from the server config class
            String url = ServerConfig.Instance.serverAddress;
            HttpClient client = ServerConfig.Instance.client;

            HttpContent content = new StringContent("");
            HttpResponseMessage response;

            var request = new HttpRequestMessage(new HttpMethod("POST"), url + "/logout");
            if (isClinician) {
                request.Headers.Add("token", ClinicianController.Instance.AuthToken);
            } else
            {
                request.Headers.Add("token", UserController.Instance.AuthToken);
            }


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
                if (isClinician)
                {
                    ClinicianController.Instance.Logout();
                } else
                {
                    UserController.Instance.Logout();
                }
            }

            return response.StatusCode;
        }

        // Stub method for testing, todo
        public HttpStatusCode RegisterUser(string v1, string v2, string email, string username, string v3, DateTime dob)
        {
            return HttpStatusCode.Created;
        }

        /*
         * Returns true if the JSON string can be determined as a user object
         */
        private bool IsUser(string jsonBody)
        {
            User user;
            try
            {
                user = JsonConvert.DeserializeObject<User>(jsonBody);
            }
            catch (JsonSerializationException)
            {
                return false;
            }
            catch (JsonReaderException)
            {
                return false;
            }

            return true;
        }

        /*
         * Returns true if the JSON string can be determined as a clinician object
         */
        private bool IsClinician(string jsonBody)
        {
            Clinician clinician;
            try
            {
                clinician = JsonConvert.DeserializeObject<Clinician>(jsonBody);
            }
            catch (JsonSerializationException)
            {
                return false;
            }
            catch (JsonReaderException)
            {
                return false;
            }

            if (!clinician.accountType.Equals("CLINICIAN"))
            {
                return false;
            }
            return true;
        }

        /*
         * Returns response status code of the attempted user registration
         */
        public async Task<HttpStatusCode> RegisterUser(User user)
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
            String registerUserRequestBody = JsonConvert.SerializeObject(user);

            HttpContent body = new StringContent(registerUserRequestBody);
            var response = await client.PostAsync(url + "/users", body);

            if (response.StatusCode == HttpStatusCode.Created)
            {
                Console.WriteLine("Success on creating user");
            }
            else
            {
                Console.WriteLine($"Failed register ({response.StatusCode})");
            }
            return response.StatusCode;
        }
    }
}
