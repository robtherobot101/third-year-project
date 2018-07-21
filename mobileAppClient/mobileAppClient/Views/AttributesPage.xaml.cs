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
            fillFields();
        }

        private void fillFields()
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
            //SmokerStatusInput.SelectedItem = FirstCharToUpper(loggedInUser.smokerStatus);
            //AlcoholConsumptionInput.SelectedItem = FirstCharToUpper(loggedInUser.alcoholConsumption);
        }

        public string FirstCharToUpper(string input)
        {
            if (String.IsNullOrEmpty(input))
                throw new ArgumentException("Incorrect string argument provided");
            input = input.ToLower();
            return input.First().ToString().ToUpper() + input.Substring(1);
        }

        private void SaveClicked(object sender, EventArgs e)
        {
            User loggedInUser = UserController.Instance.LoggedInUser;
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
