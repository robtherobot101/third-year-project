using mobileAppClient.odmsAPI;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

using Xamarin.Forms;
using Xamarin.Forms.Xaml;

namespace mobileAppClient
{
	[XamlCompilation(XamlCompilationOptions.Compile)]
	public partial class RegisterPage : ContentPage
	{
        LoginPage parentLoginPage;

		public RegisterPage (LoginPage loginPage)
		{
            parentLoginPage = loginPage;
			InitializeComponent ();
		}

        async void SignUpButtonClicked(Object sender, EventArgs args)
        {
            // Check for valid inputs
            if (firstNameInput.Text == null)
            {
                await DisplayAlert("",
                    "Please enter a first name",
                    "OK");
                return;
            }
            else if (lastNameInput.Text == null)
            {
                await DisplayAlert("",
                    "Please enter a last name",
                    "OK");
                return;
            } 
            else if (passwordInput.Text == null)
            {
                await DisplayAlert("",
                    "Please enter a password",
                    "OK");
                return;
            }

            // Check if a username and valid email is entered
            else if (emailInput.Text == null || !IsValidEmail(emailInput.Text) || usernameInput.Text == null)
            {
                await DisplayAlert("",
                    "Password and email is required",
                    "OK");
                return;
            }

            LoginAPI loginAPI = new LoginAPI();
            bool successfullyRegisteredUser = await loginAPI.RegisterUser(firstNameInput.Text, lastNameInput.Text, emailInput.Text, 
                usernameInput.Text, passwordInput.Text, dobInput.Date);

            if (successfullyRegisteredUser)
            {
                // Attempt login with the newly created account
                bool loginSuccessful;
                if (usernameInput.Text != null)
                {
                    loginSuccessful = await loginAPI.LoginUser(usernameInput.Text, passwordInput.Text);
                } else
                {
                    loginSuccessful = await loginAPI.LoginUser(emailInput.Text, passwordInput.Text);
                }

                if (loginSuccessful)
                {                    
                    UserController.Instance.Login();
                    await DisplayAlert("",
                        "Account successfully created",
                        "OK");

                    // Dismiss the register modal dialog
                    await Navigation.PopModalAsync();

                    // Dismiss the login modal dialog
                    await parentLoginPage.Navigation.PopModalAsync();
                }
                else
                {
                    // Display alert on failed login
                    await DisplayAlert("",
                        "Failed to login",
                        "OK");
                    await Navigation.PopModalAsync();
                }
            } else
            {
                await DisplayAlert("",
                    "Failed to create account",
                    "OK");
            }
        }

        /*
         * Checks the validity of a given email. test@xamarin is valid for example
         */
        private bool IsValidEmail(string email)
        {
            try
            {
                var addr = new System.Net.Mail.MailAddress(email);
                return addr.Address == email;
            }
            catch
            {
                return false;
            }
        }
    }
}