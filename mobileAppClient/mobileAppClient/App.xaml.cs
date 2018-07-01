using System;
using Xamarin.Forms;
using Xamarin.Forms.Xaml;

[assembly: XamlCompilation(XamlCompilationOptions.Compile)]
namespace mobileAppClient
{
    public partial class App : Application
    {
        public App()
        {
            InitializeComponent();

            RequestTester request = new RequestTester();
            User user = request.LiveGetRequestTest();
<<<<<<< HEAD
            Console.WriteLine(user.Email);
=======
            Console.WriteLine(user.AlcoholConsumption);
            Console.WriteLine(user.SmokerStatus);
    

>>>>>>> b780a7b... Added in all user attributes apart from: - Medications - Diseases - Procedures - Waiting List Items #implement
            MainPage = new MainPage();
        }

        protected override void OnStart()
        {
            // Handle when your app starts
        }

        protected override void OnSleep()
        {
            // Handle when your app sleeps
        }

        protected override void OnResume()
        {
            // Handle when your app resumes
        }
    }
}
