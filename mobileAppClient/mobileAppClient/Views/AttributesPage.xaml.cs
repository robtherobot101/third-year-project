using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Xamarin.Forms;


namespace mobileAppClient
{
    public partial class AttributesPage : ContentPage
    {

        public AttributesPage()
        {
            InitializeComponent();
            FillFields();
        }

        /*
         * Called on page initialisation, populates fields with the current data from the logged in user
         */
        private void FillFields()
        {
            User loggedInUser = UserController.Instance.LoggedInUser;
            FirstNameInput.Text = loggedInUser.name[0];
            MiddleNameInput.Text = loggedInUser.name[1];
            LastNameInput.Text = loggedInUser.name[2];

            PrefFirstNameInput.Text = loggedInUser.preferredName[0];
            PrefMiddleNameInput.Text = loggedInUser.preferredName[1];
            PrefLastNameInput.Text = loggedInUser.preferredName[2];

            BirthGenderInput.SelectedItem = GenderExtensions.ToPickerString(loggedInUser.gender);
            GenderIdentityInput.SelectedItem = GenderExtensions.ToPickerString(loggedInUser.genderIdentity);

            AddressInput.Text = loggedInUser.currentAddress;
            RegionInput.Text = loggedInUser.region;

            dobInput.Date = loggedInUser.dateOfBirth.ToDateTime();
            // Check if the user is dead
            if (loggedInUser.dateOfDeath != null)
            {
                dodInput.Date = loggedInUser.dateOfDeath.ToDateTime();
            }

            HeightInput.Text = loggedInUser.height.ToString();
            WeightInput.Text = loggedInUser.weight.ToString();

            BloodPressureInput.Text = loggedInUser.bloodPressure;

            BloodTypeInput.SelectedItem = BloodTypeExtensions.ToPickerString(loggedInUser.bloodType);
            SmokerStatusInput.SelectedItem = FirstCharToUpper(loggedInUser.smokerStatus);
            AlcoholConsumptionInput.SelectedItem = FirstCharToUpper(loggedInUser.alcoholConsumption);
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
            User loggedInUser = UserController.Instance.LoggedInUser;

            if (!InputValidation.IsValidTextInput(FirstNameInput.Text, false)) {
                await DisplayAlert("", "Please enter a valid first name", "OK");
            } 

            if (!InputValidation.IsValidTextInput(MiddleNameInput.Text, false)) {
                await DisplayAlert("", "Please enter a valid middle name", "OK");
            }

            if (!InputValidation.IsValidTextInput(LastNameInput.Text, false))
            {
                await DisplayAlert("", "Please enter a valid last name", "OK");
            }

            if (!InputValidation.IsValidTextInput(PrefFirstNameInput.Text, false))
            {
                await DisplayAlert("", "Please enter a valid preferred first name", "OK");
            }

            if (!InputValidation.IsValidTextInput(PrefMiddleNameInput.Text, false))
            {
                await DisplayAlert("", "Please enter a valid preferred middle name", "OK");
            }

            if (!InputValidation.IsValidTextInput(LastNameInput.Text, false))
            {
                await DisplayAlert("", "Please enter a valid preferred last name", "OK");
            }

            loggedInUser.name[0] = FirstNameInput.Text;
            loggedInUser.name[1] = MiddleNameInput.Text;
            loggedInUser.name[2] = LastNameInput.Text;

            loggedInUser.preferredName[0] = PrefFirstNameInput.Text;
            loggedInUser.preferredName[1] =
            PrefLastNameInput.Text = loggedInUser.preferredName[2];

            BirthGenderInput.SelectedItem = GenderExtensions.ToPickerString(loggedInUser.gender);
            GenderIdentityInput.SelectedItem = GenderExtensions.ToPickerString(loggedInUser.genderIdentity);

            AddressInput.Text = loggedInUser.currentAddress;
            RegionInput.Text = loggedInUser.region;

            dobInput.Date = loggedInUser.dateOfBirth.ToDateTime();
            // Check if the user is dead
            if (loggedInUser.dateOfDeath != null)
            {
                dodInput.Date = loggedInUser.dateOfDeath.ToDateTime();
            }

            HeightInput.Text = loggedInUser.height.ToString();
            WeightInput.Text = loggedInUser.weight.ToString();

            BloodPressureInput.Text = loggedInUser.bloodPressure;

            BloodTypeInput.SelectedItem = BloodTypeExtensions.ToPickerString(loggedInUser.bloodType);
            SmokerStatusInput.SelectedItem = FirstCharToUpper(loggedInUser.smokerStatus);
            AlcoholConsumptionInput.SelectedItem = FirstCharToUpper(loggedInUser.alcoholConsumption);
        }
    }
}
