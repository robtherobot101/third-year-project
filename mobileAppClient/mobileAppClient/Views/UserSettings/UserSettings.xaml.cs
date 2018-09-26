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
            passwordInput.IsVisible = false;
            confirmPasswordInput.IsVisible = false;
            UsernameEntry.IsVisible = false;
            ConfirmTeam300LoginMethodChanged.IsVisible = false;



            UserController.Instance.userSettingsController = this;
        }

        protected async override void OnAppearing()
        {
            String result = await new LoginAPI().getAccountType(UserController.Instance.LoggedInUser.id);
            if (result.Equals("facebook"))
            {
                AccountSettings.IsEnabled = false;
                AccountSettings.View.IsVisible = false;
                FacebookAccountType.IsEnabled = false;
                FacebookAccountType.View.IsVisible = false;
                GoogleAccountType.IsEnabled = true;
                GoogleAccountType.View.IsVisible = true;
                RegularAccountType.IsEnabled = true;
                RegularAccountType.View.IsVisible = true;
            }
            else if (result.Equals("google"))
            {
                AccountSettings.IsEnabled = false;
                AccountSettings.View.IsVisible = false;
                GoogleAccountType.IsEnabled = false;
                GoogleAccountType.View.IsVisible = false;
                FacebookAccountType.IsEnabled = true;
                FacebookAccountType.View.IsVisible = true;
                RegularAccountType.IsEnabled = true;
                RegularAccountType.View.IsVisible = false;
            }
            else
            {
                RegularAccountType.IsEnabled = false;
                RegularAccountType.View.IsVisible = false;
                AccountSettings.IsEnabled = true;
                AccountSettings.View.IsVisible = true;
                GoogleAccountType.IsEnabled = true;
                GoogleAccountType.View.IsVisible = true;
                FacebookAccountType.IsEnabled = true;
                FacebookAccountType.View.IsVisible = true;
            }
        }

        async void Handle_PhotoSettingsTapped(object sender, System.EventArgs e)
        {
            await Navigation.PushAsync(new PhotoSettingsPage(false));
        }

        async void Handle_FacebookAccountTypeTapped(object sender, System.EventArgs e)
        {
            //Do a thing
            await Navigation.PushModalAsync(new NavigationPage(new FacebookPage(UserController.Instance.LoggedInUser.id)));
            OnAppearing();
        }

        void Handle_GoogleAccountTypeTapped(object sender, System.EventArgs e)
        {
            // Opens the Google login
            Device.OpenUri(new Uri(GoogleServices.ChangeToGoogleLoginAddr()));
        }

        async void Handle_AccountSettingsTapped(object sender, System.EventArgs e)
        {
            await Navigation.PushAsync(new AccountSettingsPage());
        }


        void Handle_RegularAccountType(object sender, System.EventArgs e)
        {
            passwordInput.IsVisible = true;
            confirmPasswordInput.IsVisible = true;
            UsernameEntry.IsVisible = true;
            ConfirmTeam300LoginMethodChanged.IsVisible = true;
            
        }

        async void Handle_ConfirmTeam300LoginMethodChanged(object sender, System.EventArgs e)
        {
            if (passwordInput.Text == "")
            {
                await DisplayAlert("",
                    "Password must not be empty",
                    "OK");
                return;
            }
            else if (passwordInput.Text != confirmPasswordInput.Text)
            {
                await DisplayAlert("",
                    "Passwords do not match",
                    "OK");
                return;
            }
            else if(UsernameEntry.Text == "")
            {
                await DisplayAlert("",
                                    "Username must not be empty",
                                    "OK");
                return;
            }

            HttpStatusCode result = await new LoginAPI().Team300RegisterUser(UserController.Instance.LoggedInUser.id, passwordInput.Text, UsernameEntry.Text);

            switch (result)
            {
                case HttpStatusCode.OK:
                    await DisplayAlert("",
                        "Login method changed to regular login",
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

            OnAppearing();
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
            OnAppearing();
        }
    }
}
