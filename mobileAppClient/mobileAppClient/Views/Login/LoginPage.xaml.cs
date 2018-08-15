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
using mobileAppClient.Google;
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
	    private bool _IsLoading;
	    public bool IsLoading
	    {
	        get { return _IsLoading; }
	        set
	        {
	            _IsLoading = value;
	            if (_IsLoading == true)
	            {
	                LoadingIndicator.IsVisible = true;
	                LoadingIndicator.IsRunning = true;
	            }
	            else
	            {
	                LoadingIndicator.IsVisible = false;
	                LoadingIndicator.IsRunning = false;
	            }
	        }
	    }
        private bool loginClicked;

		public LoginPage ()
		{
			InitializeComponent ();
            loginClicked = false;

		    IsLoading = false;
		    UserController.Instance.loginPageController = this;
            //Temporary fix for the google login not working on iOS
            if(Device.RuntimePlatform == Device.iOS) {
                GoogleButton.IsVisible = false;
            }
            if(Device.RuntimePlatform == Device.Android) {
                FacebookButton.Image = null;
            }
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
            IsLoading = true;
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
                    // Fetch photo only on user login
                    if (!ClinicianController.Instance.isLoggedIn())
                    {
                        UserAPI userAPI = new UserAPI();
                        HttpStatusCode httpStatusCode = await userAPI.GetUserPhoto();
                        UserController.Instance.mainPageController.updateMenuPhoto();
                    }

                    IsLoading = false;
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
                case HttpStatusCode.Conflict:
                    await DisplayAlert(
                        "Failed to Login",
                        "User is deceased. Please consult a Registered Clinician",
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

            IsLoading = true;
            Device.OpenUri(new Uri(GoogleServices.GetLoginAddr()));
        }

	    public async Task Handle_RedirectUriCaught(string code)
	    {
	        Tuple<User, string> parsedUser = await GoogleServices.GetUserProfile(code);

	        await LoginAsGoogleUser(parsedUser);
	    }

	    private async Task LoginAsGoogleUser(Tuple<User, string> parsedUser)
	    {
	        User googleUser = parsedUser.Item1;
	        string profileImageURL = parsedUser.Item2;
	        string password = "password";

            UserAPI userAPI = new UserAPI();
            LoginAPI loginAPI = new LoginAPI();

            //Do a check to see if user is already in the database - if they are then skip the register and go to login if not just login
            Tuple<HttpStatusCode, bool> isUniqueEmailResult = await userAPI.isUniqueUsernameEmail(googleUser.email);
            if (isUniqueEmailResult.Item1 != HttpStatusCode.OK)
            {
                Console.WriteLine("Failed to connect to server for checking of unique email");
            }

            if (isUniqueEmailResult.Item2 == false)
            {
                HttpStatusCode statusCode = await loginAPI.LoginUser(googleUser.email, password);
                switch (statusCode)
                {
                    case HttpStatusCode.OK:
                        // Pop away login screen on successful login
                        HttpStatusCode httpStatusCode = await userAPI.GetUserPhoto();
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
            }
            else
            {
                await Navigation.PushModalAsync(new GooglePage(this, googleUser, profileImageURL));
            }
	        IsLoading = false;
        }
    }
}