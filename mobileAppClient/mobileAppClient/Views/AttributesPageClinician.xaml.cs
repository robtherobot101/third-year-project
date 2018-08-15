using mobileAppClient.odmsAPI;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Text;
using System.Threading.Tasks;
using Xamarin.Forms;


namespace mobileAppClient
{
    /*
     * Class to handle all functionality regarding the attributes page for 
     * user details being shown.
     */
    public partial class AttributesPageClinician : ContentPage
    {

        public AttributesPageClinician()
        {
            InitializeComponent();
            FillFields();
        }

        /*
         * Called on page initialisation, populates fields with the current data from the logged in user
         */
        private void FillFields()
        {
            Models.Clinician loggedInClinician = ClinicianController.Instance.LoggedInClinician;
            NameInput.Text = loggedInClinician.name;
     
            AddressInput.Text = loggedInClinician.workAddress;
            RegionInput.Text = loggedInClinician.region;
        }

        /*
         * Converts any given string into having an uppercase first char with lowercase for the rest
         * - Used for Pickers
         */
        private string FirstCharToUpper(string input)
        {
            if (String.IsNullOrEmpty(input))
                return "";
            input = input.ToLower();
            return input.First().ToString().ToUpper() + input.Substring(1);
        }

        /*
         * Called when the Save button is pressed, reads + validates input fields and pushes changes to the API
         */
        private async void SaveClicked(object sender, EventArgs e)
        {
            Models.Clinician loggedInClinician = ClinicianController.Instance.LoggedInClinician;

            string name = InputValidation.Trim(NameInput.Text);

            string givenAddress = InputValidation.Trim(AddressInput.Text);
            string givenRegion = InputValidation.Trim(RegionInput.Text);

            // Birth names
            if (!InputValidation.IsValidTextInput(name, false, false)) {
                await DisplayAlert("", "Please enter a valid name", "OK");
                return;
            }  

            // Address
            if (!InputValidation.IsValidTextInput(givenAddress, true, true))
            {
                await DisplayAlert("", "Please enter a valid address", "OK");
                return;
            }

            // Set user attributes to the new fields
            loggedInClinician.name = name;

            loggedInClinician.workAddress = givenAddress;
            loggedInClinician.region = givenRegion;

            ClinicianAPI clinicianAPI = new ClinicianAPI();
            HttpStatusCode clinicianUpdated = await clinicianAPI.UpdateClinician();

            switch (clinicianUpdated)
            {
                case HttpStatusCode.Created:
                    await DisplayAlert("",
                    "Clinician details successfully updated",
                    "OK");
                    break;
                case HttpStatusCode.BadRequest:
                    await DisplayAlert("",
                    "Clinician details update failed (400)",
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
