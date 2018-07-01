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
            Console.WriteLine(usernameEmailInput.Text);
            Console.WriteLine(passwordInput.Text);

            //await Navigation.PopModalAsync();
        }

        async void RegisterButtonClicked(object sender, EventArgs args)
        {
            await Navigation.PopModalAsync();
        }
    }
}