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
	public partial class LoginPage : ContentPage
	{
		public LoginPage ()
		{
			InitializeComponent ();
		}

        async void SignUpButtonClicked(Object sender, EventArgs args)
        {
            var registerPage = new NavigationPage(new RegisterPage());
            await Navigation.PushModalAsync(registerPage);
            Console.WriteLine("HEHEHREERERE************************************************");
        }

        async void LoginButtonClicked(object sender, EventArgs args)
        {

            if (usernameEmailInput.Text == null || passwordInput.Text == null)
            {
                await DisplayAlert("",
                    "Please enter a username/email and password",
                    "OK");
                return;
            }

            LoginAPI loginAPI = new LoginAPI();
            bool result = await Task.Run(() => loginAPI.LoginUser(usernameEmailInput.Text, passwordInput.Text));

            if (!result)
            {
                // Display alert on failed login
                await DisplayAlert("",
                    "Failed to login",
                    "OK");
            } else
            {
                // Pop away login screen on successful login
                UserController.Instance.Login();
                await Navigation.PopModalAsync();
            }
        }

        protected override bool OnBackButtonPressed()
        {
            // Stops the back button from working
            return true;
        }
    }
}