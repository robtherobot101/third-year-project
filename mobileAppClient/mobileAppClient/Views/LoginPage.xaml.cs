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

        async void LoginButtonClicked(object sender, EventArgs args)
        {
            if(!Uri.IsWellFormedUriString(serverInput.Text, UriKind.Absolute))
            {
                await DisplayAlert("Error", "Invalid server address. Please enter the URL in the form http://domain.tld/path/to/api", "OK");
                return;
            }
            RequestTester rt = new RequestTester(serverInput.Text);
            //if (!rt.connect().Equals("1"))
            //{
            //    await DisplayAlert("Error", "Could not connect to server", "OK");
            //    return;
            //}
            bool result = await rt.LoginUser(usernameEmailInput.Text, passwordInput.Text);
            if (!result)
            {
                // Display alert on failed login
                await DisplayAlert("",
                    "Incorrect username/password",
                    "OK");
            } else
            {
                // Pop away login screen on successful login
                await Navigation.PopModalAsync();
            }
        }

        async void RegisterButtonClicked(object sender, EventArgs args)
        {
            await Navigation.PopModalAsync();
        }
    }
}