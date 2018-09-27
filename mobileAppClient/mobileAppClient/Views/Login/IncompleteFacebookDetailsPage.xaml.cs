using System;
using System.Collections.Generic;

using Xamarin.Forms;
using System.Globalization;

namespace mobileAppClient
{
    /*
     * Provides a page where a new user can enter additional details
     * which were not provided by Facebook
     */
    public partial class IncompleteFacebookDetailsPage : ContentPage
    {

        private bool _IsLoading;
        public bool IsLoading
        {
            get { return _IsLoading; }
            set
            {
                _IsLoading = value;
                if (_IsLoading == true)
                {
                    ContinueButton.IsEnabled = false;
                    dobInput.IsEnabled = false;
                    emailInput.IsEnabled = false;
                    NHIInput.IsEnabled = false;
                }
                else
                {
                    ContinueButton.IsEnabled = true;
                    dobInput.IsEnabled = true;
                    emailInput.IsEnabled = true;
                    NHIInput.IsEnabled = true;
                }
            }
        }
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
                CultureInfo MyCultureInfo = new CultureInfo("en-US");
                dobInput.Date = DateTime.Parse(facebookProfile.Birthday, MyCultureInfo);
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
