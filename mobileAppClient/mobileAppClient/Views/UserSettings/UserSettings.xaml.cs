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

            if(Device.RuntimePlatform == Device.iOS) {
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
                //AccountSettingsStackLayout.IsVisible = false;
                AccountSettings.IsEnabled = false;
                AccountSettingsLabel.TextColor = Color.Gray;
                //AccountSettings.Tapped -= Handle_AccountSettingsTapped;

                FacebookAccountTypeLabel.TextColor = Color.Gray;
                FacebookAccountType.IsEnabled = false;
                //FacebookAccountType.Tapped -= Handle_FacebookAccountTypeTapped;


                GoogleAccountTypeLabel.TextColor = Color.Black;
                GoogleAccountType.Tapped += Handle_GoogleAccountTypeTapped;

                RegularAccountTypeLabel.TextColor = Color.Black;
                RegularAccountType.Tapped += Handle_RegularAccountType;
            }
            else if (result.Equals("google"))
            {
                //AccountSettingsStackLayout.IsVisible = false;
                AccountSettings.IsEnabled = false;
                AccountSettingsLabel.TextColor = Color.Gray;
                //AccountSettings.Tapped -= Handle_AccountSettingsTapped;

                FacebookAccountTypeLabel.TextColor = Color.Black;
                //FacebookAccountType.Tapped += Handle_FacebookAccountTypeTapped;

                GoogleAccountTypeLabel.TextColor = Color.Gray;
                //GoogleAccountType.Tapped -= Handle_GoogleAccountTypeTapped;

                RegularAccountTypeLabel.TextColor = Color.Black;
                RegularAccountType.Tapped += Handle_RegularAccountType;
            }
            else
            {
                //AccountSettingsStackLayout.IsVisible = true;
                AccountSettings.IsEnabled = true;
                AccountSettingsLabel.TextColor = Color.Black;


                FacebookAccountTypeLabel.TextColor = Color.Black;
                //FacebookAccountType.Tapped += Handle_FacebookAccountTypeTapped;

                GoogleAccountTypeLabel.TextColor = Color.Black;
                GoogleAccountType.Tapped += Handle_GoogleAccountTypeTapped;

                RegularAccountTypeLabel.TextColor = Color.Gray;
                //RegularAccountType.Tapped -= Handle_RegularAccountType;
            }
            if (Device.RuntimePlatform == Device.iOS)
            {
                GoogleAccountType.IsEnabled = false;
                GoogleAccountTypeLabel.TextColor = Color.Gray;
            }
        }

        async void Handle_PhotoSettingsTapped(object sender, System.EventArgs e)
        {
            await Navigation.PushAsync(new PhotoSettingsPage(false));

        }

        async void Handle_FacebookAccountTypeTapped(object sender, System.EventArgs e)
        {
            await Navigation.PushModalAsync(new NavigationPage(new FacebookPage(UserController.Instance.LoggedInUser.id)));
            //updateButtons();
        }

        void Handle_GoogleAccountTypeTapped(object sender, System.EventArgs e)
        {
            // Opens the Google login
            Device.OpenUri(new Uri(GoogleServices.ChangeToGoogleLoginAddr()));
            updateButtons();
        }

        async void Handle_AccountSettingsTapped(object sender, System.EventArgs e)
        {
            await Navigation.PushAsync(new AccountSettingsPage());
        }


        void Handle_RegularAccountType(object sender, System.EventArgs e)
        {
            passwordInput.IsEnabled = true;
            confirmPasswordInput.IsEnabled = true;
            UsernameEntry.IsEnabled = true;
            ConfirmTeam300LoginMethodChanged.IsEnabled = true;
            
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

            passwordInput.IsVisible = false;
            confirmPasswordInput.IsVisible = false;
            UsernameEntry.IsVisible = false;
            ConfirmTeam300LoginMethodChanged.IsVisible = false;
            updateButtons();
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
            updateButtons();
        }
    }
}
