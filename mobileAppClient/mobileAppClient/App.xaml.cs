using System;
using Xamarin.Forms;
using Xamarin.Forms.Xaml;
using System.Net;
using System.IO;
using mobileAppClient.Google;
using Newtonsoft.Json;
using mobileAppClient.odmsAPI;

[assembly: XamlCompilation(XamlCompilationOptions.Compile)]
namespace mobileAppClient
{
    /*
     * Class which acts as the main entry point of the application.
     */ 
    public partial class App : Application
    {

        //Static variables for the app
        public static string DefaultImageId = "default_image";
        public static string ImageIdToSave = null;

        /*
         * Open the app to have a the main page xaml as the bottom of 
         * the stack of views.
         */ 
        public App()
        {
            InitializeComponent();
            // Ensure config is set
            ServerConfig serverConfig = ServerConfig.Instance;
            UserController userController = UserController.Instance;
            MainPage = new MainPage(false);
        }

        public static async void ProcessGoogleLogin(string code)
        {
            await GoogleServices.GetUserProfile(code);
        }

        protected override void OnStart()
        {
            // Handle when your app starts
        }

        async protected override void OnSleep()
        {
            // Handle when your app sleeps
            if (ClinicianController.Instance.LoggedInClinician != null)
            {
                LoginAPI loginAPI = new LoginAPI();
                await loginAPI.Logout(true);
            } else
            {
                LoginAPI loginAPI = new LoginAPI();
                await loginAPI.Logout(false);
            }
        }

        protected override void OnResume()
        {
            // Handle when your app resumes
        }
    }
}
