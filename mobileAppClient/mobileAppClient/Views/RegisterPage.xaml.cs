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
		public RegisterPage ()
		{
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

            // Check if a username OR email is entered
            else if (emailInput.Text == null && usernameInput.Text == null)
            {
                await DisplayAlert("",
                    "Password or email is required",
                    "OK");
                return;
            }

            // If an email is submitted, check its format
            if (emailInput.Text != null && !IsValidEmail(emailInput.Text))
            {
                await DisplayAlert("",
                    "Valid email is required",
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
                    
                    // Pop away login screen on successful login
                    UserController.Instance.Login();
                    await DisplayAlert("",
                        "Account successfully created",
                        "OK");
                    await Navigation.PopModalAsync();
                    await Navigation.PopModalAsync();
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