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
    class LoginAPI
    {
        /*
         * Returns true or false depending on the success of the attempted login 
         */
        public async Task<bool> LoginUser(String usernameEmail, String password, String address)
        {
            // Set the server address from the field on login
            ServerConfig.Instance.serverAddress = address;

            // Get the single userController instance
            UserController userController = UserController.Instance;

            // Fetch the url and client from the server config class
            String url = ServerConfig.Instance.serverAddress;
            HttpClient client = ServerConfig.Instance.client;
            
            String queries = null;
            if (usernameEmail == "" || password == "")
            {
                // No valid identification provided -> return non-successful
                return false;
            }

            queries = String.Format("?usernameEmail={0}&password={1}", usernameEmail, password);

            var content = new StringContent("");

            var response = await client.PostAsync(url + "/login" + queries, content);

            if (response.StatusCode == HttpStatusCode.OK)
            {

                var responseContent = await response.Content.ReadAsStringAsync();
                User user = JsonConvert.DeserializeObject<User>(responseContent);
                userController.LoggedInUser = user;
                Console.WriteLine("Logged in as " + userController.LoggedInUser.Name.ToString());
                return true;
            }
            else
            {
                Console.WriteLine(String.Format("Failed login ({0})", response.StatusCode));
                return false;
            }
        }
    }
}
