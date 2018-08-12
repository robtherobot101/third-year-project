using mobileAppClient.odmsAPI;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Text;
using System.Threading.Tasks;

using Xamarin.Forms;
using Xamarin.Forms.Xaml;

namespace mobileAppClient
{
	[XamlCompilation(XamlCompilationOptions.Compile)]
    /*
     * Class to handle all functionality regarding the login page for 
     * logging in as an existing user or wishing to register.
     */ 
	public partial class LoginPage : ContentPage
	{
        private bool loginClicked;

		public LoginPage ()
		{
			InitializeComponent ();
            loginClicked = false;

		}

        /*
         * Called when the Sign Up button is pressed
         */
        async void SignUpButtonClicked(Object sender, EventArgs args)
        {
            var registerPage = new NavigationPage(new RegisterPage(this));
            await Navigation.PushModalAsync(registerPage);
        }

        /*
         * Called when the Login button is pressed
         */
        async void LoginButtonClicked(object sender, EventArgs args)
        {
            // Prevents multiple presses of the login button
            if (loginClicked)
            {
                return;
            } else
            {
                loginClicked = true;
            }

            string givenUsernameEmail = InputValidation.Trim(usernameEmailInput.Text);
            string givenPassword = InputValidation.Trim(passwordInput.Text);


            if (!InputValidation.IsValidTextInput(givenUsernameEmail, true, false) || !InputValidation.IsValidTextInput(givenPassword, true, false))
            {
                await DisplayAlert("",
                    "Please enter a valid username/email and password",
                    "OK");
                loginClicked = false;
                return;
            }

            LoginAPI loginAPI = new LoginAPI();
            HttpStatusCode statusCode = await loginAPI.LoginUser(givenUsernameEmail, givenPassword);
            switch(statusCode)
            {
                case HttpStatusCode.OK:
                    // Pop away login screen on successful login
                    UserAPI userAPI = new UserAPI();

                    // Fetch photo only on user login
                    if (!ClinicianController.Instance.isLoggedIn())
                    {
                        HttpStatusCode httpStatusCode = await userAPI.GetUserPhoto();
                    }
                    UserController.Instance.mainPageController.updateMenuPhoto();
                    await Navigation.PopModalAsync();
                    break;
                case HttpStatusCode.Unauthorized:
                    await DisplayAlert(
                        "Failed to Login",
                        "Incorrect username/password",
                        "OK");
                    break;
                case HttpStatusCode.ServiceUnavailable:
                    await DisplayAlert(
                        "Failed to Login",
                        "Server unavailable, check connection",
                        "OK");
                    break;
                case HttpStatusCode.InternalServerError:
                    await DisplayAlert(
                        "Failed to Login",
                        "Server error",
                        "OK");
                    break;
            }
            loginClicked = false;


        }

        /*
         * Function used to Stops the back button from working and 
         * opening the main view without a logged in user
         */
        protected override bool OnBackButtonPressed()
        {
            return true;
        }

        async void Handle_LoginWithFacebookClicked(object sender, System.EventArgs e)
        {

            if (!await ServerConfig.Instance.IsConnectedToInternet())
            {
                await DisplayAlert(
                "Failed to Login",
                "Server unavailable, check connection",
                "OK");
                return;
            }

            await Navigation.PushModalAsync(new NavigationPage(new FacebookPage(this)));

        }

        async void Handle_LoginWithGoogleClicked(object sender, System.EventArgs e)
        {
            if (!await ServerConfig.Instance.IsConnectedToInternet())
            {
                await DisplayAlert(
                    "Failed to Login",
                    "Server unavailable, check connection",
                    "OK");
                return;
            }

            await Navigation.PushModalAsync(new NavigationPage(new GooglePage(this)));
        }
    }
}