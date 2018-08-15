using System;
using System.Collections.Generic;

using Xamarin.Forms;

namespace mobileAppClient
{
    public partial class IncompleteFacebookDetailsPage : ContentPage
    {
        public IncompleteFacebookDetailsPage(FacebookProfile facebookProfile)
        {
            InitializeComponent();
            if(facebookProfile.Email == null) {
                emailInput.IsEnabled = true;
                emailInput.Text = "";
            } else {
                emailInput.Text = facebookProfile.Email;
                emailInput.IsEnabled = false;
            }
            if(facebookProfile.Birthday == null) {
                dobInput.IsEnabled = true;
            } else {
                dobInput.IsEnabled = false;
                dobInput.Date = DateTime.Parse(facebookProfile.Birthday);
            }

        }

        async void Handle_Clicked(object sender, System.EventArgs e)
        {
            string email = InputValidation.Trim(emailInput.Text);
            string dateOfBirth = dobInput.Date.ToShortDateString();

            if (!InputValidation.IsValidEmail(email))
            {
                await DisplayAlert("",
                    "Email is required",
                    "OK");
                return;
            }
            UserController.Instance.FacebookEmail = email;
            UserController.Instance.FacebookDateOfBirth = dateOfBirth;
            await Navigation.PopModalAsync();
        }

    }
}
