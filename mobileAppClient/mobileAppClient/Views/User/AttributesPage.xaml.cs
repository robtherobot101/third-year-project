using mobileAppClient.Google;
using mobileAppClient.odmsAPI;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.ComponentModel;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using System.Timers;
using Xamarin.Forms;
using Xamarin.Forms.Maps;

namespace mobileAppClient
{
    /*
     * Class to handle all functionality regarding the attributes page for 
     * user details being shown.
     */
    public partial class AttributesPage : ContentPage
    {
        // Whether the date of death input is visible

        private bool isClinicianEditing;

        Boolean updatingAutoComplete;
        Boolean AutoCompleteitemTapped;
        Color defaultCellBackgroundColor;
        public AttributesPage()
        {
            InitializeComponent();
            if (ClinicianController.Instance.isLoggedIn())
            {
                hasDiedSwitch.IsEnabled = true;
                isClinicianEditing = true;
            }
            else
            {
                hasDiedSwitch.IsEnabled = false;
                isClinicianEditing = false;
            }

            hasDiedSwitch.On = UserController.Instance.LoggedInUser.dateOfDeath != null;

            NHIInput.IsEnabled = isClinicianEditing;

            dobInput.MaximumDate = DateTime.Today;

            FillFields();

            defaultCellBackgroundColor = StreetAutoCompeleteTableCell.View.BackgroundColor;

            StreetAutoCompeleteTableCell.IsEnabled = false;

            DODCityAutoCompleteTableCell.IsEnabled = false;

            updatingAutoComplete = false;
        }

        /*
         * Called on page initialisation, populates fields with the current data from the logged in user
         */
        private void FillFields()
        {
            User loggedInUser = UserController.Instance.LoggedInUser;
            // Name
            FirstNameInput.Text = loggedInUser.name[0];
            MiddleNameInput.Text = "";
            LastNameInput.Text = "";

            // If the user has at least a last name
            if (loggedInUser.name.Count > 1)
            {
                // Set the last name to the last element in the name array
                LastNameInput.Text = loggedInUser.name.Last();
                // Set the middle name to everything in between the first and last element
                MiddleNameInput.Text = String.Join(" ", loggedInUser.name.GetRange(1, loggedInUser.name.Count - 2).ToArray());
            }

            NHIInput.Text = loggedInUser.nhi;

            // Preferred Name
            PrefFirstNameInput.Text = loggedInUser.preferredName[0];
            PrefMiddleNameInput.Text = "";
            PrefLastNameInput.Text = "";

            // If the user has at least a last name
            if (loggedInUser.preferredName.Count > 1)
            {
                // Set the last name to the last element in the name array
                PrefLastNameInput.Text = loggedInUser.preferredName.Last();
                // Set the middle name to everything in between the first and last element
                PrefMiddleNameInput.Text = String.Join(" ", loggedInUser.preferredName.GetRange(1, loggedInUser.preferredName.Count - 2));
            }


            BirthGenderInput.SelectedItem = FirstCharToUpper(loggedInUser.gender);
            GenderIdentityInput.SelectedItem = FirstCharToUpper(loggedInUser.genderIdentity);

            if(loggedInUser.currentAddress != null)
            {
                DisplayAddress(loggedInUser.currentAddress);
            }

            RegionInput.SelectedItem = loggedInUser.region;
            CountryInput.SelectedItem = loggedInUser.country;

            dobInput.Date = loggedInUser.dateOfBirth.ToDateTime();

            // Check if the user is dead
            if (hasDiedSwitch.On)
            {
                dodInput.Date = loggedInUser.dateOfDeath.date.ToDateTime();
                Console.WriteLine(loggedInUser.dateOfDeath.ToDateTimeWithSeconds().TimeOfDay);
                todInput.Time = loggedInUser.dateOfDeath.ToDateTimeWithSeconds().TimeOfDay;

                DODCityInput.Text = loggedInUser.cityOfDeath;

                DODRegionInput.SelectedItem = loggedInUser.regionOfDeath;
                DODCountryInput.SelectedItem = loggedInUser.countryOfDeath;


                dodInput.IsEnabled = true;
                todInput.IsEnabled = true;
                DODCountryInput.IsEnabled = true;
                DODRegionInput.IsEnabled = true;

                DODCityInput.IsEnabled = true;
                DODCityInput.Text = UserController.Instance.LoggedInUser.cityOfDeath;


            } else
            {
                dodInput.IsEnabled = false;
                todInput.IsEnabled = false;
                DODCountryInput.IsEnabled = false;
                DODRegionInput.IsEnabled = false;

                DODCityInput.IsEnabled = false;
                DODCityInput.Text = "";
            }

            HeightInput.Text = loggedInUser.height.ToString();
            WeightInput.Text = loggedInUser.weight.ToString();

            BloodPressureInput.Text = loggedInUser.bloodPressure;
            BloodTypeInput.SelectedItem = BloodTypeExtensions.ToString(BloodTypeExtensions.ToBloodTypeJSON(loggedInUser.bloodType));
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


            String addressLine1 = AddressInput.Text;
            String addressLine2 = AddressLine2Input.Text;
            List<String> addressLines = new List<String>();
            if(addressLine1 != "")
            {
                addressLines.Add(addressLine1);
            }

            if (addressLine2 != "")
            {
                addressLines.Add(addressLine2);
            }

            string givenAddress = InputValidation.Trim(String.Join(", ", addressLines));

            string givenRegion = InputValidation.Trim(RegionInput.SelectedItem == null ? "" : RegionInput.SelectedItem.ToString());

            string givenNHINumber = InputValidation.Trim(NHIInput.Text);

            string givenHeight = InputValidation.Trim(HeightInput.Text);
            string givenWeight = InputValidation.Trim(WeightInput.Text);
            string givenBloodPressure = InputValidation.Trim(BloodPressureInput.Text);

            // Birth names
            if (!InputValidation.IsValidTextInput(givenFirstName, false, false))
            {
                await DisplayAlert("", "Please enter a valid first name", "OK");
                return;
            }

            if (!InputValidation.IsValidTextInput(givenMiddleName, false, true))
            {
                await DisplayAlert("", "Please enter a valid middle name", "OK");
                return;
            }

            if (!InputValidation.IsValidTextInput(givenLastName, false, false))
            {
                await DisplayAlert("", "Please enter a valid last name", "OK");
                return;
            }

            // Preferred names
            if (!InputValidation.IsValidTextInput(givenPrefFirstName, false, true))
            {
                await DisplayAlert("", "Please enter a valid preferred first name", "OK");
                return;
            }

            if (!InputValidation.IsValidTextInput(givenPrefMiddleName, false, true))
            {
                await DisplayAlert("", "Please enter a valid preferred middle name", "OK");
                return;
            }

            if (!InputValidation.IsValidTextInput(givenPrefLastName, false, true))
            {
                await DisplayAlert("", "Please enter a valid preferred last name", "OK");
                return;
            }


            if (!InputValidation.IsValidNhiInput(givenNHINumber))
            {
                await DisplayAlert("", "Please enter a valid NHI number", "OK");
                return;
            }



            // Physical attributes
            if (!InputValidation.IsValidNumericInput(givenWeight, 0, 500, true))
            {
                await DisplayAlert("", "Please enter a valid weight in kg", "OK");
                return;
            }

            if (!InputValidation.IsValidNumericInput(givenHeight, 0, 300, true))
            {
                await DisplayAlert("", "Please enter a valid height in cm", "OK");
                return;
            }

            if (!InputValidation.IsValidBloodPressure(givenBloodPressure))
            {
                await DisplayAlert("", "Please enter a valid blood pressure eg. 120/80", "OK");
                return;
            }

            //if (loggedInUser.dateOfDeath.ToDateTime() < loggedInUser.dateOfBirth.ToDateTime())
            //{
            //    await DisplayAlert("",
            //    "Please enter a valid date of death",
            //    "OK");
            //    return;
            //}

            // Set user attributes to the new fields
            List<string> name = new List<string>();
            name.Add(givenFirstName);
            name.AddRange(givenMiddleName.Split(' '));
            name.Add(givenLastName);
            loggedInUser.name = name;

            List<string> prefName = new List<string>();
            prefName.Add(givenPrefFirstName);
            prefName.AddRange(givenPrefMiddleName.Split(' '));
            prefName.Add(givenPrefLastName);
            loggedInUser.preferredName = prefName;

            loggedInUser.gender = BirthGenderInput.SelectedItem.ToString().ToUpper();
            loggedInUser.genderIdentity = GenderIdentityInput.SelectedItem.ToString().ToUpper();

            loggedInUser.currentAddress = givenAddress;
            loggedInUser.region = givenRegion;

            loggedInUser.dateOfBirth = new CustomDate(dobInput.Date);

            if (hasDiedSwitch.On)
            {
                loggedInUser.dateOfDeath = new CustomDateTime(dodInput.Date.Add(todInput.Time));
                loggedInUser.countryOfDeath = DODCountryInput.SelectedItem == null ? "" : DODCountryInput.SelectedItem.ToString();
                loggedInUser.regionOfDeath = DODRegionInput.SelectedItem == null ? "" : DODRegionInput.SelectedItem.ToString();
                loggedInUser.cityOfDeath = DODCityInput.Text;
            }
            else
            {
                loggedInUser.dateOfDeath = null;
                loggedInUser.countryOfDeath = "";
                loggedInUser.regionOfDeath = "";
                loggedInUser.cityOfDeath = "";
            }

            // Don't worry about conversion exceptions -> this was checked with InputValidation
            loggedInUser.height = Convert.ToDouble(givenHeight);
            loggedInUser.weight = Convert.ToDouble(givenWeight);
            loggedInUser.bloodPressure = givenBloodPressure;
            loggedInUser.bloodType = BloodTypeExtensions.ToBloodType(BloodTypeInput.SelectedItem.ToString().ToUpper()).ToString();
            loggedInUser.smokerStatus = SmokerStatusInput.SelectedItem.ToString().ToUpper();
            loggedInUser.alcoholConsumption = AlcoholConsumptionInput.SelectedItem.ToString().ToUpper();

            UserAPI userAPI = new UserAPI();
            HttpStatusCode userUpdated = await userAPI.UpdateUser(isClinicianEditing);

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

        /*
         * Updates the state of the death detail fields when the death switch is changed
         */
        private void dateOfDeathSwitchChanged(object sender, ToggledEventArgs e)
        {
            dodInput.IsEnabled = e.Value;
            todInput.IsEnabled = e.Value;
            DODCountryInput.IsEnabled = e.Value;
            DODRegionInput.IsEnabled = e.Value;

            DODCityInput.IsEnabled = e.Value;

            if (e.Value)
            {
                DODCountryInput.SelectedItem = UserController.Instance.LoggedInUser.country;
                DODRegionInput.SelectedItem = UserController.Instance.LoggedInUser.region;

            } else
            {
                DODCountryInput.SelectedItem = UserController.Instance.LoggedInUser.country;
                DODRegionInput.SelectedItem = UserController.Instance.LoggedInUser.region;
            }

            DODCityInput.Text = "";

            dateOfDeathCombo.ForceLayout();
            timeOfDeathCombo.ForceLayout();
        }

        /*
         * Fills the address entry with the value in the autocomplete field when it is tapped
         */
        void Handle_StreetAutoCompleteItemTapped(object sender, ItemTappedEventArgs e)
        {
            if(StreetAutoCompleteStAddr.Text != null || StreetAutoCompleteStAddr.Text != "")
            {
                AutoCompleteitemTapped = true;
                DisplayAddress(StreetAutoCompleteStAddr.Text);
                StreetAutoCompleteStAddr.Text = "";
                StreetAutoCompleteLocation.Text = "";
                StreetAutoCompeleteTableCell.IsEnabled = false;
                StreetAutoCompeleteTableCell.View.BackgroundColor = defaultCellBackgroundColor;
            }
        }


        /*
         * Queries the google places autocomplete api and populates the autocomplete cell
         * when the address field is changed
         */
        async void Handle_StreetAutoCompleteValueChanged(object sender, PropertyChangedEventArgs args)
        {
            if(args.PropertyName != EntryCell.TextProperty.PropertyName)
            {
                return;
            }

            if (updatingAutoComplete == false && AutoCompleteitemTapped == false)
            {
                updatingAutoComplete = true;

                List<String> tokens = new List<String>();
                foreach (Object item in new List<Object>() { CountryInput.SelectedItem, RegionInput.SelectedItem, ((EntryCell)sender).Text })
                {
                    if(item != null && item.ToString() != "")
                    {
                        tokens.Add(item.ToString());
                    }
                }

                List<String> autoCompleteTypes = new List<String>() { "street_address", "intersection", "route" };
                List<Tuple<String, String>> data = await new GooglePlacesAPI().AddressAutocomplete(tokens, autoCompleteTypes);

                if (data.Count == 0)
                {
                    StreetAutoCompleteStAddr.Text = "";
                    StreetAutoCompleteLocation.Text = "";
                    StreetAutoCompeleteTableCell.IsEnabled = false;
                    StreetAutoCompeleteTableCell.View.BackgroundColor = defaultCellBackgroundColor;

                }
                else
                {
                    StreetAutoCompleteStAddr.Text = data[0].Item1;
                    StreetAutoCompleteLocation.Text = data[0].Item2;
                    StreetAutoCompeleteTableCell.IsEnabled = true;
                    StreetAutoCompeleteTableCell.View.BackgroundColor = Color.LightBlue;
                }
                updatingAutoComplete = false;
            }

            if (AutoCompleteitemTapped == true)
            {
                AutoCompleteitemTapped = false;
            }
        }

        /*
         * Fills the city of death entry with the value in the autocomplete field when it is tapped
         */
        void Handle_DODCityAutoCompleteItemTapped(object sender, ItemTappedEventArgs e)
        {
            if(DODCityAutoCompleteLabel.Text != "" && hasDiedSwitch.On)
            {
                AutoCompleteitemTapped = true;
                DODCityInput.Text = DODCityAutoCompleteLabel.Text;

                DODCityAutoCompleteLabel.Text = "";

                DODCityAutoCompleteTableCell.IsEnabled = false;
                DODCityAutoCompleteTableCell.View.BackgroundColor = defaultCellBackgroundColor;
            }
        }

        /*
         * Breaks the given address into two lines and displays it in two fields
         */
        public void DisplayAddress(String address)
        {
            List<String> tokens = address.Split(new string[] { ", " }, StringSplitOptions.RemoveEmptyEntries).ToList();
            String line1 = tokens.Count > 0 ? tokens[0] : "";
            String line2 = tokens.Count > 0 ? String.Join(", ", tokens.GetRange(1, tokens.Count - 1)) : "";
            AddressInput.Text = line1;
            AddressLine2Input.Text = line2;
        }

        /*
         * Queries the google places autocomplete api and populates the autocomplete cell
         * when the address field is changed
         */
        async void Handle_DODCityAutoCompleteValueChanged(object sender, PropertyChangedEventArgs args)
        {
            if (args.PropertyName != EntryCell.TextProperty.PropertyName)
            {
                return;
            }

            if (updatingAutoComplete == false && AutoCompleteitemTapped == false)
            {
                updatingAutoComplete = true;

                List<String> tokens = new List<String>();
                foreach (Object item in new List<Object>() { DODCountryInput.SelectedItem, DODRegionInput.SelectedItem, DODCityInput.Text })
                {
                    if (item != null && item.ToString() != "")
                    {
                        tokens.Add(item.ToString());
                    }
                }

                List<Tuple<String,String>> data = await new GooglePlacesAPI().CityAutocomplete(tokens, new List<String>() { "locality" });

                if (data.Count == 0)
                {
                    DODCityAutoCompleteLabel.Text = "";


                    DODCityAutoCompleteTableCell.IsEnabled = false;
                    DODCityAutoCompleteTableCell.View.BackgroundColor = defaultCellBackgroundColor;
                }
                else
                {
                    DODCityAutoCompleteLabel.Text = data[0].Item1;

                    DODCityAutoCompleteTableCell.IsEnabled = true;
                    DODCityAutoCompleteTableCell.View.BackgroundColor = Color.LightBlue;
                }
                updatingAutoComplete = false;
            }

            if (AutoCompleteitemTapped == true)
            {
                AutoCompleteitemTapped = false;
            }
        }
    }
}
