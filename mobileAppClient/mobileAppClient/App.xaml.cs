using System;
using Xamarin.Forms;
using Xamarin.Forms.Xaml;
using System.Net;
using System.IO;
using mobileAppClient.Google;
using Newtonsoft.Json;
using mobileAppClient.odmsAPI;
using mobileAppClient.Notifications;

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

            MainPage = new LoginPage();

            //MainPage = new MainPage(false);
        }

        public static async void ProcessGoogleLogin(string code)
        {
            await GoogleServices.GetUserProfile(code);
        }

        protected override void OnStart()
        {
            // Handle when your app starts

            // Visual Studio App Center initialisation 
            VSAppCenter.Setup();
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
