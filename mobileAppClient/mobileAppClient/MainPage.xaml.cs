using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Xamarin.Forms;


namespace mobileAppClient
{
    public partial class MainPage : ContentPage
    {

        public MainPage()
        {
            InitializeComponent();
            RequestTester tester = new RequestTester();
            User user = tester.mockGetRequestTest();
            Console.WriteLine(user.Email);
        }
    }
}
