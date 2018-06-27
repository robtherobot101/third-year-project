using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Xamarin.Forms;
using System.Net;
using System.IO;
using Newtonsoft.Json;


namespace mobileAppClient
{
    public partial class MainPage : ContentPage
    {
        public MainPage()
        {
            InitializeComponent();
            HttpWebRequest myReq = null;
            try
            {
                myReq = (HttpWebRequest)WebRequest.Create("http://localhost:6976/api/v1/users/1");
                Console.WriteLine("------------------CONNECTION TO SERVER SUCCESSFUL.-----------------------");
            } catch (Exception e) {
                Console.WriteLine("------------------CONNECTION TO SERVER FAILED.-----------------------");
                Console.WriteLine(e.StackTrace);
            }
            // Get the response.  
            WebResponse response = myReq.GetResponse();  
            // Display the status.  
            Console.WriteLine (((HttpWebResponse)response).StatusDescription);
            // Get the stream containing content returned by the server.  
            Stream dataStream = response.GetResponseStream();  
            // Open the stream using a StreamReader for easy access.  
            StreamReader reader = new StreamReader(dataStream);  
            // Read the content.  
            string responseFromServer = reader.ReadToEnd();

            Console.WriteLine("-------------------------------------");
            // Display the content.  
            Console.WriteLine(responseFromServer);  

            Console.WriteLine("-------------------------------------");

            User user = JsonConvert.DeserializeObject<User>(responseFromServer);
            Console.WriteLine(user.email);

            // Clean up the streams and the response.  
            reader.Close();  
            response.Close(); 

        }
    }
}
