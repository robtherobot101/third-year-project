using System;
using System.Net;
using mobileAppClient.odmsAPI;
using Xamarin.Forms;
using Xamarin.Forms.Xaml;

namespace mobileAppClient
{
    [XamlCompilation(XamlCompilationOptions.Compile)]
    public partial class ClinicianSettings : ContentPage
    {
        private bool changingPassword = true;
        public ClinicianSettings()
        {
            InitializeComponent();
            UsernameEntry.Text = ClinicianController.Instance.LoggedInClinician.username;
        }

        /*
         * Enables changing of password when the page appears
         */
        protected override void OnAppearing()
        {
            ChangePasswordSwitch.On = false;
        }

        /*
         * Changes whether or not the password can be changed
         * and changes the password inputs to match
         */
        void PasswordSwitchChanged(object sender, ToggledEventArgs e)
        {
            Console.WriteLine(e.Value);
            passwordInput.IsEnabled = e.Value;
            confirmPasswordInput.IsEnabled = e.Value;
            changingPassword = e.Value;
        }

        /*
         * Attempts to change the username And/Or password for the logged in clinician
         */
        async void Handle_ConfirmButtonClicked(object sender, System.EventArgs e)
        {
            if (passwordInput.Text == "")
            {
                await DisplayAlert("",
                    "Password must not be empty",
                    "OK");
            } 
            else if(passwordInput.Text != confirmPasswordInput.Text)
            {
                await DisplayAlert("",
                    "Passwords do not match",
                    "OK");
            }
            else
            {
                if (changingPassword)
                {
                    ClinicianController.Instance.LoggedInClinician.password = passwordInput.Text;
                } else
                {
                    ClinicianController.Instance.LoggedInClinician.password = null;
                }
                ClinicianController.Instance.LoggedInClinician.username = UsernameEntry.Text;
                HttpStatusCode status = await new ClinicianAPI().updateAccountSettings(ClinicianController.Instance.LoggedInClinician, ClinicianController.Instance.AuthToken, changingPassword);

                switch (status)
                {
                    case HttpStatusCode.Created:
                        await DisplayAlert("",
                        "Account settings successfully updated",
                        "OK");
                        await Navigation.PopAsync();
                        break;
                    case HttpStatusCode.BadRequest:
                        await DisplayAlert("",
                        "Account settings update failed (400)",
                        "OK");
                        break;
                    case HttpStatusCode.ServiceUnavailable:
                        await DisplayAlert("",
                        "Server unavailable, check connection",
                        "OK");
                        break;
                    case HttpStatusCode.Unauthorized:
                        await DisplayAlert("",
                        "Unauthorised to modify profile",
                        "OK");
                        break;
                    case HttpStatusCode.InternalServerError:
                        await DisplayAlert("",
                        "Server error, please try again (500)",
                        "OK");
                        break;
                }
            }
        }
    }
}