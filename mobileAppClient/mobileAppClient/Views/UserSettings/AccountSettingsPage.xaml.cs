using mobileAppClient.odmsAPI;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Text;
using System.Threading.Tasks;

using Xamarin.Forms;
using Xamarin.Forms.Xaml;

namespace mobileAppClient.Views.UserSettings
{
	[XamlCompilation(XamlCompilationOptions.Compile)]
	public partial class AccountSettingsPage : ContentPage
	{
        private bool changingPassword = false;
		public AccountSettingsPage()
		{
			InitializeComponent();
            passwordInput.IsEnabled = false;
            confirmPasswordInput.IsEnabled = false;

            UsernameEntry.Text = UserController.Instance.LoggedInUser.username;
            EmailEntry.Text = UserController.Instance.LoggedInUser.email;
        }

        /*
         * Toggles the state of the password inputs and whether to post them
         * to the change account endpoint when the confirm button is pressed
         */
        void Handle_ChangePasswordTapped(object sender, System.EventArgs e)
        {
            passwordInput.IsEnabled = !passwordInput.IsEnabled;
            confirmPasswordInput.IsEnabled = !confirmPasswordInput.IsEnabled;
            changingPassword = !changingPassword;
        }

        /*
         * Makes a call to the api which updates the logged in user with the 
         * given atttributes. Password is only updated when changingPassword is true
         */
        async void Handle_ConfirmButtonClicked(object sender, System.EventArgs e)
        {
            if (passwordInput.Text == "")
            {
                await DisplayAlert("",
                    "Password must not be empty",
                    "OK");
            }
            else if (passwordInput.Text != confirmPasswordInput.Text)
            {
                await DisplayAlert("",
                    "Passwords do not match",
                    "OK");
            }
            else
            {
                if(changingPassword)
                {
                    UserController.Instance.LoggedInUser.password = passwordInput.Text;
                }
                UserController.Instance.LoggedInUser.username = UsernameEntry.Text;
                UserController.Instance.LoggedInUser.email = EmailEntry.Text;
                HttpStatusCode status = await new UserAPI().updateAccountSettings(UserController.Instance.LoggedInUser, UserController.Instance.AuthToken, changingPassword);

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