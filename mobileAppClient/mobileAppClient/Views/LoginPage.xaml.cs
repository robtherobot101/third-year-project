using mobileAppClient.odmsAPI;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
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
        private FacebookServices facebookServices;

		public LoginPage ()
		{
			InitializeComponent ();
            loginClicked = false;
            facebookServices = new FacebookServices();

		}

        private async void WebViewOnNavigated(object sender, WebNavigatedEventArgs e)
        {

            var accessToken = facebookServices.ExtractAccessTokenFromUrl(e.Url);

            if (accessToken != "")
            {
                FacebookProfile facebookProfile = await facebookServices.GetFacebookProfileAsync(accessToken);
                string password = "password";

                User inputUser = new User();
                inputUser.name = new List<string> { facebookProfile.FirstName, "", facebookProfile.LastName };
                inputUser.preferredName = new List<string> { facebookProfile.FirstName, "", facebookProfile.LastName };
                inputUser.email = facebookProfile.Email;
                inputUser.username = facebookProfile.Email;
                inputUser.password = password;
                inputUser.dateOfBirth = new CustomDate(DateTime.Parse(facebookProfile.Birthday));
                inputUser.creationTime = new CustomDateTime(DateTime.Now);
                inputUser.gender = facebookProfile.Gender.ToUpper();
                inputUser.region = facebookProfile.Location.Name;
                //Server requires to initialise the organs and user history items on creation
                inputUser.organs = new List<string>();
                inputUser.userHistory = new List<HistoryItem>();

                LoginAPI loginAPI = new LoginAPI();
                HttpStatusCode registerUserResult = await loginAPI.RegisterUser(inputUser);

                switch (registerUserResult)
                {
                    case HttpStatusCode.Created:
                        HttpStatusCode loginUserResult = await loginAPI.LoginUser(facebookProfile.Email, password);
                        switch (loginUserResult) {
                            case HttpStatusCode.OK:
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
                        break;
                    case HttpStatusCode.ServiceUnavailable:
                        await DisplayAlert(
                            "Failed to Register",
                            "Server unavailable, check connection",
                            "OK");
                        break;
                    case HttpStatusCode.InternalServerError:
                        await DisplayAlert(
                            "Failed to Register",
                            "Username/Email may be taken",
                            "OK");
                        break;
                }

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
                    HttpStatusCode httpStatusCode = await userAPI.GetUserPhoto();
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

        void Handle_LoginWithFacebookClicked(object sender, System.EventArgs e)
        {

            var webView = new WebView
            {
                Source = facebookServices.apiRequest,
                HeightRequest = 1
            };

            webView.Navigated += WebViewOnNavigated;

            Content = webView;
        }
        
        void Handle_Clicked(object sender, System.EventArgs e)
        {
            Console.WriteLine("Not implemented yet");
        }
    }
}