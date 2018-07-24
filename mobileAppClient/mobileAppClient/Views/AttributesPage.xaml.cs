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

            BirthGenderInput.SelectedItem = GenderExtensions.ToString(loggedInUser.gender);
            GenderIdentityInput.SelectedItem = GenderExtensions.ToString(loggedInUser.genderIdentity);

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

            BloodTypeInput.SelectedItem = BloodTypeExtensions.ToString(loggedInUser.bloodType);
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

            string givenFirstName = InputValidation.Trim(FirstNameInput.Text);
            string givenMiddleName = InputValidation.Trim(MiddleNameInput.Text);
            string givenLastName = InputValidation.Trim(LastNameInput.Text);

            string givenPrefFirstName = InputValidation.Trim(PrefFirstNameInput.Text);
            string givenPrefMiddleName = InputValidation.Trim(PrefMiddleNameInput.Text);
            string givenPrefLastName = InputValidation.Trim(PrefLastNameInput.Text);

            string givenAddress = InputValidation.Trim(AddressInput.Text);
            string givenRegion = InputValidation.Trim(RegionInput.Text);

            string givenHeight = InputValidation.Trim(HeightInput.Text);
            string givenWeight = InputValidation.Trim(WeightInput.Text);
            string givenBloodPressure = InputValidation.Trim(BloodPressureInput.Text);

            // Birth names
            if (!InputValidation.IsValidTextInput(givenFirstName, false)) {
                await DisplayAlert("", "Please enter a valid first name", "OK");
                return;
            } 

            if (!InputValidation.IsValidTextInput(givenMiddleName, false)) {
                await DisplayAlert("", "Please enter a valid middle name", "OK");
                return;
            }

            if (!InputValidation.IsValidTextInput(givenLastName, false))
            {
                await DisplayAlert("", "Please enter a valid last name", "OK");
                return;
            }

            // Preferred names
            if (!InputValidation.IsValidTextInput(givenPrefFirstName, false))
            {
                await DisplayAlert("", "Please enter a valid preferred first name", "OK");
                return;
            }

            if (!InputValidation.IsValidTextInput(givenPrefMiddleName, false))
            {
                await DisplayAlert("", "Please enter a valid preferred middle name", "OK");
                return;
            }

            if (!InputValidation.IsValidTextInput(givenPrefLastName, false))
            {
                await DisplayAlert("", "Please enter a valid preferred last name", "OK");
                return;
            }

            // Address
            if (!InputValidation.IsValidTextInput(givenAddress, true))
            {
                await DisplayAlert("", "Please enter a valid address", "OK");
                return;
            }

            // Physical attributes
            if (!InputValidation.IsValidNumericInput(givenWeight, 1, 500))
            {
                await DisplayAlert("", "Please enter a valid weight in kg", "OK");
                return;
            }

            if (!InputValidation.IsValidNumericInput(givenHeight, 1, 300))
            {
                await DisplayAlert("", "Please enter a valid height in cm", "OK");
                return;
            }

            if (!InputValidation.IsValidBloodPressure(givenBloodPressure))
            {
                await DisplayAlert("", "Please enter a valid blood pressure eg. 120/80", "OK");
                return;
            }

            // Set user attributes to the new fields
            loggedInUser.name[0] = givenFirstName;
            loggedInUser.name[1] = givenMiddleName;
            loggedInUser.name[2] = givenLastName;

            loggedInUser.preferredName[0] = givenPrefFirstName;
            loggedInUser.preferredName[1] = givenPrefMiddleName;
            loggedInUser.preferredName[2] = givenPrefLastName;

            loggedInUser.gender = GenderExtensions.ToGender(BirthGenderInput.SelectedItem.ToString());
            loggedInUser.genderIdentity = GenderExtensions.ToGender(GenderIdentityInput.SelectedItem.ToString());

            loggedInUser.currentAddress = givenAddress;
            loggedInUser.region = givenRegion;

            // TODO check date of death if not changed
            loggedInUser.dateOfBirth = new CustomDate(dobInput.Date);
            loggedInUser.dateOfDeath = new CustomDate(dodInput.Date);

            // Don't worry about conversion exceptions -> this was checked with InputValidation
            loggedInUser.height = Convert.ToDouble(givenHeight);
            loggedInUser.weight = Convert.ToDouble(givenWeight);
            loggedInUser.bloodPressure = givenBloodPressure;

            loggedInUser.bloodType = BloodTypeExtensions.ToBloodType(BloodTypeInput.SelectedItem.ToString());
            loggedInUser.smokerStatus = SmokerStatusInput.SelectedItem.ToString().ToUpper();
            loggedInUser.alcoholConsumption = AlcoholConsumptionInput.SelectedItem.ToString().ToUpper();

            UserAPI userAPI = new UserAPI();
            HttpStatusCode userUpdated = await userAPI.UpdateUser();

            switch (userUpdated)
            {
                case HttpStatusCode.Created:
                    await DisplayAlert("",
                    "User details successfully updated",
                    "OK");
                    break;
                case HttpStatusCode.BadRequest:
                    await DisplayAlert("",
                    "User details update failed (400)",
                    "OK");
                    break;
                case HttpStatusCode.ServiceUnavailable:
                    await DisplayAlert("",
                    "Server unavailable, check connection",
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
