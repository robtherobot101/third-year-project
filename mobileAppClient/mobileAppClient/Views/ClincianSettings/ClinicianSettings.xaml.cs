using System;
using System.IO;
using System.Net;
using mobileAppClient.odmsAPI;
using mobileAppClient.Views.UserSettings;
using Xamarin.Forms;

namespace mobileAppClient
{
    public partial class ClinicianSettings : ContentPage
    {
        private bool changingPassword = false;
        public ClinicianSettings()
        {
            InitializeComponent();
            passwordInput.IsVisible = false;
            confirmPasswordInput.IsVisible = false;

            UsernameEntry.Text = UserController.Instance.LoggedInUser.username;
        }

        async void Handle_ChangePasswordTapped(object sender, System.EventArgs e)
        {
            passwordInput.IsVisible = !passwordInput.IsVisible;
            confirmPasswordInput.IsVisible = !confirmPasswordInput.IsVisible;
            changingPassword = true;
        }

        async void Handle_ConfirmButtonClicked(object sender, System.EventArgs e)
        {
            if (passwordInput.Text != confirmPasswordInput.Text)
            {
                await DisplayAlert("",
                    "Passwords do not match",
                    "OK");
            }
            else
            {
                if (changingPassword)
                {
                    UserController.Instance.LoggedInUser.password = passwordInput.Text;
                }
                UserController.Instance.LoggedInUser.username = UsernameEntry.Text;
                UserController.Instance.LoggedInUser.email = EmailEntry.Text;
                HttpStatusCode status = await new UserAPI().updateAccountSettings(UserController.Instance.LoggedInUser, UserController.Instance.AuthToken);

                switch (status)
                {
                    case HttpStatusCode.Created:
                        await DisplayAlert("",
                        "User account settings successfully updated",
                        "OK");
                        await Navigation.PopAsync();
                        break;
                    case HttpStatusCode.BadRequest:
                        await DisplayAlert("",
                        "User account settings update failed (400)",
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
}