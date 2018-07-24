using mobileAppClient.odmsAPI;
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
     * Class to handle all functionality regarding the register page for 
     * registering a new user with the application.
     */ 
    public partial class RegisterPage : ContentPage
    {
        // Tracks the login page that called this register page, used for closing the login modal after a successful registration
        LoginPage parentLoginPage;

        public RegisterPage(LoginPage loginPage)
        {
            parentLoginPage = loginPage;
            dobInput.MaximumDate = DateTime.Now;
            InitializeComponent();
        }

        /*
         * Called when the Sign Up button is clicked
         */
        async void SignUpButtonClicked(Object sender, EventArgs args)
        {
            string givenFirstName = InputValidation.Trim(firstNameInput.Text);
            string givenLastName = InputValidation.Trim(lastNameInput.Text);
            string givenPassword = InputValidation.Trim(passwordInput.Text);
            string givenEmail = InputValidation.Trim(emailInput.Text);
            string givenUsername = InputValidation.Trim(usernameInput.Text);

            // Check for valid inputs
            if (!InputValidation.IsValidTextInput(givenFirstName, false, false))
            {
                await DisplayAlert("",
                    "Please enter a valid first name",
                    "OK");
                return;
            }
            else if (!InputValidation.IsValidTextInput(givenLastName, false, false))
            {
                await DisplayAlert("",
                    "Please enter a valid last name",
                    "OK");
                return;
            }
            else if (!InputValidation.IsValidTextInput(givenPassword, false, false))
            {
                await DisplayAlert("",
                    "Please enter a password",
                    "OK");
                return;
            }

            // Check if a username and valid email is entered
            else if (!InputValidation.IsValidEmail(givenEmail) || !InputValidation.IsValidTextInput(givenUsername, true, false))
            {
                await DisplayAlert("",
                    "Password and email is required",
                    "OK");
                return;
            }

            // DOB validation is through constraints on the DatePicker in the XAML

            LoginAPI loginAPI = new LoginAPI();
            HttpStatusCode registerUserResult = await loginAPI.RegisterUser(givenFirstName, givenLastName, givenEmail,
                givenUsername, givenPassword, dobInput.Date);

            switch (registerUserResult)
            {
                case HttpStatusCode.Created:
                    await LoginRegisteredUser(givenUsername, givenEmail, givenPassword);
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
                        "Server error",
                        "OK");
                    break;
            }
        }

        /*
         * Function which sends an API call with the inputted username/email and password 
         * to return a user object from the login call.
         */ 
        private async Task LoginRegisteredUser(string givenUsername, string givenEmail, string givenPassword)
        {
            LoginAPI loginAPI = new LoginAPI();

            // Attempt login with the newly created account
            HttpStatusCode loginResult;
            if (usernameInput.Text != null)
            {
                loginResult = await loginAPI.LoginUser(givenUsername, givenPassword);
            }
            else
            {
                loginResult = await loginAPI.LoginUser(givenEmail, givenPassword);
            }

            switch (loginResult)
            {
                case HttpStatusCode.OK:
                    // Pop away login screen on successful login
                    UserController.Instance.Login();
                    await DisplayAlert("",
                        "Account successfully created",
                        "OK");

                    // Dismiss the register modal dialog
                    await Navigation.PopModalAsync();

                    // Dismiss the login modal dialog
                    await parentLoginPage.Navigation.PopModalAsync();
                    break;

                case HttpStatusCode.Unauthorized:
                    await DisplayAlert(
                        "Failed to Login",
                        "Incorrent username/password",
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
    }
}