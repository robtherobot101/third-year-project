using System;
using System.IO;
using System.Net;
using System.Threading.Tasks;
using mobileAppClient.Google;
using mobileAppClient.odmsAPI;
using mobileAppClient.Views.UserSettings;
using Xamarin.Forms;

namespace mobileAppClient
{
    public partial class UserSettings : ContentPage
    {
        
        public UserSettings()
        {
            InitializeComponent();
            UserController.Instance.userSettingsController = this;
        }

        async void Handle_PhotoSettingsTapped(object sender, System.EventArgs e)
        {
            await Navigation.PushAsync(new PhotoSettingsPage(false));
        }

        async void Handle_FacebookSettingsTapped(object sender, System.EventArgs e)
        {
            //Do a thing
            await Navigation.PushModalAsync(new NavigationPage(new FacebookPage(UserController.Instance.LoggedInUser.id)));
            
        }

        void Handle_GoogleSettingsTapped(object sender, System.EventArgs e)
        {
            // Opens the Google login
            Device.OpenUri(new Uri(GoogleServices.ChangeToGoogleLoginAddr()));
        }

        async void Handle_AccountSettingsTapped(object sender, System.EventArgs e)
        {
            await Navigation.PushAsync(new AccountSettingsPage());
        }

        public async Task Handle_RedirectUriCaught(string code)
        {
            string id = await GoogleServices.GetUserId(code);
            LoginAPI loginApi = new LoginAPI();

            HttpStatusCode responseCode = await loginApi.GoogleRegisterUser(UserController.Instance.LoggedInUser.id, id);

            switch (responseCode)
            {
                case HttpStatusCode.OK:
                    await DisplayAlert("",
                        "Login method changed to Google",
                        "OK");
                    break;
                case HttpStatusCode.BadRequest:
                    await DisplayAlert("",
                        "Failed to change login method (400)",
                        "OK");
                    break;
                case HttpStatusCode.ServiceUnavailable:
                    await DisplayAlert("",
                        "Server unavailable, check connection",
                        "OK");
                    break;
                case HttpStatusCode.Unauthorized:
                    await DisplayAlert("",
                        "Unauthorised to modify profile",
                        "OK");
                    break;
                case HttpStatusCode.InternalServerError:
                    await DisplayAlert("",
                        "Server error, please try again (500)",
                        "OK");
                    break;
            }
        }
    }
}
