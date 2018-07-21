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
            FirstNameInput.Text = loggedInUser.Name[0];
            MiddleNameInput.Text = loggedInUser.Name[1];
            LastNameInput.Text = loggedInUser.Name[2];

            PrefFirstNameInput.Text = loggedInUser.PreferredName[0];
            PrefMiddleNameInput.Text = loggedInUser.PreferredName[1];
            PrefLastNameInput.Text = loggedInUser.PreferredName[2];

            BirthGenderInput.SelectedItem = GenderExtensions.ToPickerString(loggedInUser.Gender);
            GenderIdentityInput.SelectedItem = GenderExtensions.ToPickerString(loggedInUser.GenderIdentity);

            AddressInput.Text = loggedInUser.CurrentAddress;
            RegionInput.Text = loggedInUser.Region;

            dobInput.Date = loggedInUser.DateOfBirth.ToDateTime();
            // Check if the user is dead
            if (loggedInUser.DateOfDeath != null)
            {
                dodInput.Date = loggedInUser.DateOfDeath.ToDateTime();
            }
            
            HeightInput.Text = loggedInUser.Height.ToString();
            WeightInput.Text = loggedInUser.Weight.ToString();

            BloodPressureInput.Text = loggedInUser.BloodPressure;

            BloodTypeInput.SelectedItem = BloodTypeExtensions.ToPickerString(loggedInUser.BloodType);
            SmokerStatusInput.SelectedItem = FirstCharToUpper(loggedInUser.SmokerStatus);
            AlcoholConsumptionInput.SelectedItem = FirstCharToUpper(loggedInUser.AlcoholConsumption);
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
            loggedInUser.Name[0] = FirstNameInput.Text;
            loggedInUser.Name[1] = MiddleNameInput.Text;
            loggedInUser.Name[2] = LastNameInput.Text;

            loggedInUser.PreferredName[0] = PrefFirstNameInput.Text;
            loggedInUser.PreferredName[1] =
            PrefLastNameInput.Text = loggedInUser.PreferredName[2];

            BirthGenderInput.SelectedItem = GenderExtensions.ToPickerString(loggedInUser.Gender);
            GenderIdentityInput.SelectedItem = GenderExtensions.ToPickerString(loggedInUser.GenderIdentity);

            AddressInput.Text = loggedInUser.CurrentAddress;
            RegionInput.Text = loggedInUser.Region;

            dobInput.Date = loggedInUser.DateOfBirth.ToDateTime();
            // Check if the user is dead
            if (loggedInUser.DateOfDeath != null)
            {
                dodInput.Date = loggedInUser.DateOfDeath.ToDateTime();
            }

            HeightInput.Text = loggedInUser.Height.ToString();
            WeightInput.Text = loggedInUser.Weight.ToString();

            BloodPressureInput.Text = loggedInUser.BloodPressure;

            BloodTypeInput.SelectedItem = BloodTypeExtensions.ToPickerString(loggedInUser.BloodType);
            SmokerStatusInput.SelectedItem = FirstCharToUpper(loggedInUser.SmokerStatus);
            AlcoholConsumptionInput.SelectedItem = FirstCharToUpper(loggedInUser.AlcoholConsumption);
        }
    }

    
}
