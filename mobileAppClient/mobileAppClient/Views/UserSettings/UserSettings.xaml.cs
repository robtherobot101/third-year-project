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
            passwordInput.IsEnabled = false;

            confirmPasswordInput.IsEnabled = false;
            UsernameEntry.IsEnabled = false;
            ConfirmTeam300LoginMethodChanged.IsEnabled = false;

            if (Device.RuntimePlatform == Device.iOS)
            {
                GoogleAccountType.IsEnabled = false;
            }
            //updateButtons();
            UserController.Instance.userSettingsController = this;
        }

        protected async override void OnAppearing()
        {
            updateButtons();
        }

        public async void updateButtons()
        {
            String result = await new LoginAPI().getAccountType(UserController.Instance.LoggedInUser.id);
            if (result.Equals("facebook"))
            {
                AccountSettingsLabel.TextColor = Color.Gray;
                AccountSettings.IsEnabled = false;

                FacebookAccountTypeLabel.TextColor = Color.Gray;
                FacebookAccountType.IsEnabled = false;

                GoogleAccountTypeLabel.TextColor = Color.Black;
                GoogleAccountType.IsEnabled = true;

                RegularAccountTypeLabel.TextColor = Color.Black;
                RegularAccountType.IsEnabled = true;
            }
            else if (result.Equals("google"))
            {
                AccountSettingsLabel.TextColor = Color.Gray;
                AccountSettings.IsEnabled = false;


                FacebookAccountTypeLabel.TextColor = Color.Black;
                FacebookAccountType.IsEnabled = true;

                GoogleAccountTypeLabel.TextColor = Color.Gray;
                GoogleAccountType.IsEnabled = false;

                RegularAccountTypeLabel.TextColor = Color.Black;
                RegularAccountType.IsEnabled = true;
            }
            else
            {
                AccountSettingsLabel.TextColor = Color.Black;
                AccountSettings.IsEnabled = true;

                FacebookAccountTypeLabel.TextColor = Color.Black;
                FacebookAccountType.IsEnabled = true;

                GoogleAccountTypeLabel.TextColor = Color.Black;
                GoogleAccountType.IsEnabled = true;

                RegularAccountTypeLabel.TextColor = Color.Gray;
                RegularAccountType.IsEnabled = false;
            }

            // Google login does not work on iOS
            if (Device.RuntimePlatform == Device.iOS)
            {
                GoogleAccountType.IsEnabled = false;
                GoogleAccountTypeLabel.TextColor = Color.Gray;
            }
        }

        /*
         * Navigates to the profile photo settings page.
         */
        async void Handle_PhotoSettingsTapped(object sender, System.EventArgs e)
        {
            await Navigation.PushAsync(new PhotoSettingsPage(false));

        }

        /*
         * Launches Facebook authentication and attempts to switch the 
         * login method for the the logged in user.
         */
        async void Handle_FacebookAccountTypeTapped(object sender, System.EventArgs e)
        {
            await Navigation.PushModalAsync(new NavigationPage(new FacebookPage(UserController.Instance.LoggedInUser.id)));
            //updateButtons();
        }

        /*
         * Launches Google authentication and attempts to switch the 
         * login method for the the logged in user.
         */
        void Handle_GoogleAccountTypeTapped(object sender, System.EventArgs e)
        {
            // Opens the Google login
            Device.OpenUri(new Uri(GoogleServices.ChangeToGoogleLoginAddr()));
            updateButtons();
        }

        /*
         * Navigates to a new page which allows the user to change
         * thier username, email, and password.
         */
        async void Handle_AccountSettingsTapped(object sender, System.EventArgs e)
        {
            await Navigation.PushAsync(new AccountSettingsPage());
        }

        /*
         * Enables fields which allow the user to enter a new username and
         * password and a confirm button which attempts to switch the login method over 
         * to a regular login type
         */
        void Handle_RegularAccountType(object sender, System.EventArgs e)
        {
            passwordInput.IsEnabled = true;
            confirmPasswordInput.IsEnabled = true;
            UsernameEntry.IsEnabled = true;
            ConfirmTeam300LoginMethodChanged.IsEnabled = true;

        }

        /*
         * Takes the username and password given by the suer
         * and attempts to switch over the login method to the regular type.
         */
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
            else if (UsernameEntry.Text == "")
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

            passwordInput.IsVisible = false;
            confirmPasswordInput.IsVisible = false;
            UsernameEntry.IsVisible = false;
            ConfirmTeam300LoginMethodChanged.IsVisible = false;
            updateButtons();
        }

        /*
         * Catches the Google login redirect after successful Google authentication.
         * Attempts to switch login method for the logged in user to Google
         */
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
            updateButtons();
        }
    }
}
