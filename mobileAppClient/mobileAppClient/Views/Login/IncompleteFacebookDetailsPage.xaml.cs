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
            if (!InputValidation.IsValidEmail(email))
            {
                await DisplayAlert("",
                    "A valid email is required",
                    "OK");
                return;
            }
            string NHI = InputValidation.Trim(NHIInput.Text);
            if (!InputValidation.IsValidNhiInput(NHI)) {
                await DisplayAlert("",
                    "A valid NHI is required - NHI must start with one of every third letter of the alphabet",
                    "OK");
                return;
            }
            string dateOfBirth = dobInput.Date.ToShortDateString();
            UserController.Instance.FacebookEmail = email;
            UserController.Instance.FacebookDateOfBirth = dateOfBirth;
            UserController.Instance.NHI = NHI;
            await Navigation.PopModalAsync();
        }

    }
}
