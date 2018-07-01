using System;
using System.Net;
using System.IO;
using Newtonsoft.Json;


namespace mobileAppClient
{
    public class RequestTester
    {
        public RequestTester()
        {
        }

        public User LiveGetRequestTest() {
            Console.WriteLine("--------------GET SINGLE USER REQUEST-----------------------");
            HttpWebRequest myReq = (HttpWebRequest)WebRequest.Create("http://csse-s302g3.canterbury.ac.nz:80/api/v1/users/1");

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

        public User mockGetRequestTest() {
            Console.WriteLine("------------------CONNECTION TO MOCK SERVER SUCCESSFUL.-----------------------");
            User mockUser = new User("abc@123.com");
           
            Console.WriteLine(" TO MOCK S");
            return mockUser;
        }
    }
}
