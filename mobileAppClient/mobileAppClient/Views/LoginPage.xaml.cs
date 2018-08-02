﻿using mobileAppClient.odmsAPI;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
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
                    // Pop away login screen on successful user login
                    UserController.Instance.Login();
                    await Navigation.PopModalAsync();
                    break;
                case HttpStatusCode.Created:
                    // Pop away login screen on successful user login
                    // ClinicianController.Instance.Login();
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
    }
}