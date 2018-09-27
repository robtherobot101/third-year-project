using System;
using System.Collections.Generic;

using Xamarin.Forms;
using System.Globalization;
using System.Windows.Input;
using mobileAppClient.odmsAPI;
using System.Net;
using System.Threading.Tasks;

namespace mobileAppClient
{
    /*
     * Class to handle all functionality regarding the single disease page for 
     * showing the details of a single disease of a user.
     */ 
    public partial class SingleDiseasePage : ContentPage
    {
        private Disease currentDisease;

        private DiseasesPage diseasesPageController;

        /*
         * Used when viewing -> editing an existing disease
         * Constructor which initialises the entries of the diseases listview.
         */
        public SingleDiseasePage(DiseasesPage diseasesPageController, Disease disease)
        {
            InitializeComponent();
            this.diseasesPageController = diseasesPageController;
            this.currentDisease = disease;
            Title = "View Disease";
            
            NameEntry.Text = disease.name;
            DiagnosisDateEntry.Date = disease.diagnosisDate.ToDateTime();

            if (disease.isChronic)
            {
                ChronicEntry.IsToggled = true;
            }
            else
            {
                ChronicEntry.IsToggled = false;
            }

            if (disease.isCured)
            {
                CuredEntry.IsToggled = true;
            }
            else
            {
                CuredEntry.IsToggled = false;
            }

            if (diseasesPageController.isClinicianAccessing)
            {
                EditDiseaseButton.IsVisible = true;
            }
        }

        /*
         * Used when adding a new disease
         * Constructor which initialises the entries of the diseases listview.
         */
        public SingleDiseasePage(DiseasesPage diseasesPageController)
        {
            InitializeComponent();
            this.diseasesPageController = diseasesPageController;
            Title = "Add Disease";

            DiagnosisDateEntry.MaximumDate = DateTime.Today;
            ChronicEntry.IsToggled = false;
            CuredEntry.IsToggled = false;

            NameEntry.IsEnabled = true;
            DiagnosisDateEntry.IsEnabled = true;
            ChronicEntry.IsEnabled = true;
            CuredEntry.IsEnabled = true;

            AddDiseaseButton.IsVisible = true;
        }

        /*
         * Sets the state of the cured switch
         */
        private void ChronicCheck(object sender, ToggledEventArgs e)
        {
            if (ChronicEntry.IsToggled && CuredEntry.IsToggled)
            {
                CuredEntry.IsToggled = false;
            } 
        }

        /*
         * Sets the state of the cured switch
         */
        private void CuredCheck(object sender, ToggledEventArgs e)
        {
            if (ChronicEntry.IsToggled && CuredEntry.IsToggled)
            {
                ChronicEntry.IsToggled = false;
            }
        }
        
        /*
         * Adds the user defined disease
         */
        private async void AddDiseaseButton_OnClicked(object sender, EventArgs e)
        {
            // Check inputs
            if (!InputValidation.IsValidTextInput(NameEntry.Text, true, false))
            {
                await DisplayAlert("", "Disease name cannot be empty.", "OK");
                return;
            }
            else if (ChronicEntry.IsToggled && CuredEntry.IsToggled)
            {
                await DisplayAlert("", "Disease cannot be both cured and chronic.", "OK");
                return;
            }

            // Create the disease
            Disease diseaseToAdd = new Disease(NameEntry.Text, new CustomDate(DiagnosisDateEntry.Date), 
                ChronicEntry.IsToggled, CuredEntry.IsToggled);

            // Add the disease
            if (diseaseToAdd.isCured)
            {
                UserController.Instance.LoggedInUser.curedDiseases.Add(diseaseToAdd);
                diseasesPageController.refreshDiseases(1);
            }
            else
            {
                UserController.Instance.LoggedInUser.currentDiseases.Add(diseaseToAdd);
                diseasesPageController.refreshDiseases(0);
            }

            await uploadUser();
        }

        /*
         * Enables editing of the disease
         */
        private void EditDiseaseButton_OnClicked(object sender, EventArgs e)
        {
            Title = "Edit Disease";
            DiagnosisDateEntry.MaximumDate = DateTime.Today;
            NameEntry.IsEnabled = true;
            DiagnosisDateEntry.IsEnabled = true;
            ChronicEntry.IsEnabled = true;
            CuredEntry.IsEnabled = true;

            EditDiseaseButton.IsVisible = false;
            SaveDiseaseButton.IsVisible = true;
        }

        /*
         * Saves the changes to the disease
         */
        private async void SaveDiseaseButton_OnClicked(object sender, EventArgs e)
        {
            Console.WriteLine("Saving disease...");
            if (!InputValidation.IsValidTextInput(NameEntry.Text, true, false))
            {
                await DisplayAlert("", "Disease name cannot be empty.", "OK");
            }
            else if (ChronicEntry.IsToggled && CuredEntry.IsToggled)
            {
                await DisplayAlert("", "Disease cannot be both cured and chronic.", "OK");
            }
            else
            {
                // Remove old disease
                if (currentDisease.isCured)
                {
                    UserController.Instance.LoggedInUser.curedDiseases.Remove(currentDisease);
                }
                else
                {
                    UserController.Instance.LoggedInUser.currentDiseases.Remove(currentDisease);
                }
                
                // Create new disease
                Disease diseaseToAdd = new Disease(NameEntry.Text, new CustomDate(DiagnosisDateEntry.Date),
                    ChronicEntry.IsToggled, CuredEntry.IsToggled);

                // Add the disease
                if (diseaseToAdd.isCured)
                {
                    UserController.Instance.LoggedInUser.curedDiseases.Add(diseaseToAdd);
                    diseasesPageController.refreshDiseases(1);
                } else
                {
                    UserController.Instance.LoggedInUser.currentDiseases.Add(diseaseToAdd);
                    diseasesPageController.refreshDiseases(0);
                }

                // Send update
                await uploadUser();
            }
        }

        /*
         * Posts the user to the server
         */
        private async Task uploadUser()
        {
            UserAPI userAPI = new UserAPI();
            HttpStatusCode userUpdated = await userAPI.UpdateUser(true);
            switch (userUpdated)
            {
                case HttpStatusCode.Created:
                    await DisplayAlert("",
                        "User successfully updated",
                        "OK");
                    break;
                case HttpStatusCode.BadRequest:
                    await DisplayAlert("",
                        "User update failed (400)",
                        "OK");
                    break;
                case HttpStatusCode.ServiceUnavailable:
                    await DisplayAlert("",
                        "Server unavailable, check connection",
                        "OK");
                    break;
                case HttpStatusCode.InternalServerError:
                    await DisplayAlert("",
                        "Server error, please try again",
                        "OK");
                    break;
            }
            await diseasesPageController.Navigation.PopAsync();
        }
    }
}
