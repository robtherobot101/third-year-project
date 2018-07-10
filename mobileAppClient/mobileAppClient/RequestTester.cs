using System;
using System.Net;
using System.Net.Http;
using System.IO;
using Newtonsoft.Json;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace mobileAppClient
{
    public class RequestTester
    {
        private String url;
        public RequestTester(String url)
        {
            this.url = url;
        }

        static HttpClient client = new HttpClient();

        public String connect()
        {
            HttpWebRequest req = (HttpWebRequest)WebRequest.Create(url + "/hello");
            try
            {
                WebResponse res = req.GetResponse();
                StreamReader reader = new StreamReader(res.GetResponseStream());
                JsonTextReader jreader = new JsonTextReader(reader);
                if (!jreader.Read())
                {
                    return "0";
                }
                System.Diagnostics.Debug.WriteLine(jreader.TokenType);
                String s = (String) jreader.Value;
                return s;
            }
            catch (WebException e)
            {
                return "0";
            }
        }
        public User LiveGetRequestTest() {
            Console.WriteLine("--------------GET SINGLE USER REQUEST-----------------------");
            HttpWebRequest myReq = (HttpWebRequest)WebRequest.Create(url + "/users/1");

            // Get the response.  
            WebResponse response = null;
            try
            {
                response = myReq.GetResponse();
                Console.WriteLine("------------------CONNECTION TO SERVER SUCCESSFUL.-----------------------");
                // Display the status.  
                Console.WriteLine(((HttpWebResponse)response).StatusDescription);
                // Get the stream containing content returned by the server.  
                Stream dataStream = response.GetResponseStream();
                // Open the stream using a StreamReader for easy access.  
                StreamReader reader = new StreamReader(dataStream);
                // Read the content.  
                string responseFromServer = reader.ReadToEnd();

                Console.WriteLine("--------------RESPONSE-----------------------");
                // Display the content.  
                Console.WriteLine(responseFromServer);

                Console.WriteLine("---------------END OF RESPONSE----------------------");

                User user = JsonConvert.DeserializeObject<User>(responseFromServer);

                // Clean up the streams and the response.  
                reader.Close();
                response.Close();
                return user;
            }
            catch (Exception e)
            {
                Console.WriteLine("------------------CONNECTION TO SERVER FAILED.-----------------------");
                Console.WriteLine(e.StackTrace);
                return null;
            }
        }

        public void MockUserCreation() {
            Console.WriteLine("------------------CONNECTION TO MOCK SERVER SUCCESSFUL.-----------------------");
            UserController uc = UserController.Instance;
            User mockUser = new User("abc@123.com");
            mockUser.Email = "Andy's Mock User!";
            uc.LoggedInUser = mockUser;

            Console.WriteLine("Logged in as " + uc.LoggedInUser.Email);
          
        }


    }
}
