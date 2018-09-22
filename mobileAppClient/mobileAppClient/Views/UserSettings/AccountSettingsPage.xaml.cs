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
			InitializeComponent ();
            passwordInput.IsVisible = false;
            confirmPasswordInput.IsVisible = false;

            ChangePasswordMenuItemViewCell.Height = dummyCell.Height;
            Console.WriteLine("RenderHeight: " + dummyCell.RenderHeight);
            Console.WriteLine("Height: " + dummyCell.Height);
            UsernameEntry.Text = UserController.Instance.LoggedInUser.username;
            EmailEntry.Text = UserController.Instance.LoggedInUser.email;
            Console.WriteLine("Got hereSSSSSSSSh!");
        }

        async void Handle_ChangePasswordTapped(object sender, System.EventArgs e)
        {
            passwordInput.IsVisible = !passwordInput.IsVisible;
            confirmPasswordInput.IsVisible = !confirmPasswordInput.IsVisible;
            changingPassword = true;
        }

        async void Handle_ConfirmButtonClicked(object sender, System.EventArgs e)
        {
            if(passwordInput.Text != confirmPasswordInput.Text)
            {
                await DisplayAlert("",
                    "Passwords do not match",
                    "OK");
            } else
            {
                if(changingPassword)
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