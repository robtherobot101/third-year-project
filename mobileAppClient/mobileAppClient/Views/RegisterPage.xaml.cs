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
            InitializeComponent();
            parentLoginPage = loginPage;
            dobInput.MaximumDate = DateTime.Today;
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

            // Check if a username and valid email is entered
            else if (!InputValidation.IsValidEmail(givenEmail))
            {
                await DisplayAlert("",
                    "Valid email is required",
                    "OK");
                return;
            }

            else if (!InputValidation.IsValidTextInput(givenUsername, true, false))
            {
                await DisplayAlert("",
                    "Username is required",
                    "OK");
                return;
            }

            else if (!InputValidation.IsValidTextInput(givenPassword, true, false))
            {
                await DisplayAlert("",
                    "Please enter a password",
                    "OK");
                return;
            }
            // DOB validation is through constraints on the DatePicker in the XAML

            // Check uniqueness
            Tuple<bool, bool, bool> uniquenessResult = await isUsernameEmailUnique();

            if (!uniquenessResult.Item1 || !uniquenessResult.Item2)
            {
                // Username + email taken
                await DisplayAlert(
                        "",
                        "Email/username is taken",
                        "OK");
            }

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
                        "Username/Email may be taken",
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
        /*
         * <bool, bool, bool> = <isUsernameUnique, isEmailUnique, wasApiCallSuccessful?>
         */
        async Task<Tuple<bool, bool, bool>> isUsernameEmailUnique()
        {
            UserAPI userAPI = new UserAPI();

            // Get validity of username
            Tuple<HttpStatusCode, bool> isUniqueUsernameResult = await userAPI.isUniqueUsernameEmail(usernameInput.Text);
            if (isUniqueUsernameResult.Item1 != HttpStatusCode.OK)
            {
                await DisplayAlert(
                        "Server Failed",
                        "Failed to check username",
                        "OK");
                return new Tuple<bool, bool, bool>(false, false, false);
            }

            // Get validity of email
            Tuple<HttpStatusCode, bool> isUniqueEmailResult = await userAPI.isUniqueUsernameEmail(emailInput.Text);
            if (isUniqueEmailResult.Item1 != HttpStatusCode.OK)
            {
                await DisplayAlert(
                        "Server Failed",
                        "Failed to check email",
                        "OK");
                return new Tuple<bool, bool, bool>(false, false, false);
            }

            return new Tuple<bool, bool, bool>(isUniqueUsernameResult.Item2, isUniqueEmailResult.Item2, true);
        }

        async void CheckCredentials(object sender, EventArgs e)
        {
            string givenEmail = InputValidation.Trim(emailInput.Text);
            string givenUsername = InputValidation.Trim(usernameInput.Text);

            // Check if a username and valid email is entered
            if (!InputValidation.IsValidEmail(givenEmail))
            {
                await DisplayAlert("",
                    "Please enter a valid email",
                    "OK");
                return;
            }

            else if (!InputValidation.IsValidTextInput(givenUsername, true, false))
            {
                await DisplayAlert("",
                    "Please enter a valid username",
                    "OK");
                return;
            }

            Tuple<bool, bool, bool> uniquenessResult = await isUsernameEmailUnique();
            if (!uniquenessResult.Item3)
            {
                return;
            }
            if (!uniquenessResult.Item1 || !uniquenessResult.Item2)
            {
                // Username + email taken
                await DisplayAlert(
                        "",
                        "Email/username is taken",
                        "OK");
            } 
            else
            {
                // Both available
                await DisplayAlert(
                        "",
                        "Email/username are available",
                        "OK");
            }
        }

        async void Handle_BackClicked(object sender, EventArgs e)
        {
            await Navigation.PopModalAsync();
        }
    }
}