using System;
using System.Collections.Generic;
using System.Net;
using System.Windows.Input;
using mobileAppClient.Models.CustomObjects;
using mobileAppClient.odmsAPI;
using Xamarin.Forms;

namespace mobileAppClient
{
    /*
     * Class to handle all functionality regarding the medications page for 
     * showing all of a users current and historic medications. 
     */ 
    public partial class MedicationsPage : ContentPage
    {
        private bool isClinicianAccessing;
        public CustomObservableCollection<Medication> observableMedicationList;

        /*
         * Event handler to handle when a user switches between current and historic medications
         * resetting the sorting and changing the listview items.
         */ 
        void Handle_MedicationChanged(object sender, SegmentedControl.FormsPlugin.Abstractions.ValueChangedEventArgs e)
        {
            switch (e.NewValue)
            {
                case 0:
                    if (UserController.Instance.LoggedInUser.currentMedications.Count == 0)
                    {
                        NoDataLabel.IsVisible = true;
                        MedicationsList.IsVisible = false;
                    }
                    else
                    {
                        NoDataLabel.IsVisible = false;
                        MedicationsList.IsVisible = true;
                    }
                    observableMedicationList.Clear();
                    observableMedicationList.AddRange(UserController.Instance.LoggedInUser.currentMedications);
                    break;
                case 1:
                    if (UserController.Instance.LoggedInUser.historicMedications.Count == 0)
                    {
                        NoDataLabel.IsVisible = true;
                        MedicationsList.IsVisible = false;
                    }
                    else
                    {
                        NoDataLabel.IsVisible = false;
                        MedicationsList.IsVisible = true;
                    }

                    observableMedicationList.Clear();
                    observableMedicationList.AddRange(UserController.Instance.LoggedInUser.historicMedications);
                    break;
            }
        }

        /*
         * Constructor which sets the detail strings for each medication and also sets
         * the visibility of the no data label and compare medications button.
         */
        public MedicationsPage()
        {
            InitializeComponent();
            observableMedicationList = new CustomObservableCollection<Medication>();
            MedicationsList.ItemsSource = observableMedicationList;
            CheckIfClinicianAccessing();

            //FOR SOME REASON IT DOESNT WORK IF I HAVE THESE IN THE CONSTRUCTORS??

            foreach (Medication item in UserController.Instance.LoggedInUser.currentMedications)
            {
                item.DetailString = item.history[0];
            }
            foreach (Medication item in UserController.Instance.LoggedInUser.historicMedications)
            {
                item.DetailString = item.history[item.history.Count - 1];            
            }

            if (UserController.Instance.LoggedInUser.currentMedications.Count == 0)
            {
                NoDataLabel.IsVisible = true;
                MedicationsList.IsVisible = false;
            }

            if(UserController.Instance.LoggedInUser.currentMedications.Count + UserController.Instance.LoggedInUser.historicMedications.Count < 2) {
                CompareButton.IsVisible = false;
            }

            observableMedicationList.AddRange(UserController.Instance.LoggedInUser.currentMedications);

            if (isClinicianAccessing)
            {
                AddMedicationButton.IsVisible = true;
            }


        }

        /**
         * Checks if a clinician is viewing the user
         */
        private void CheckIfClinicianAccessing()
        {
            isClinicianAccessing = ClinicianController.Instance.isLoggedIn();
        }

        /*
         * Handles when a single medication it tapped, sending a user to the single medication page 
         * of that given medication.
         */
        async void Handle_MedicationTapped(object sender, Xamarin.Forms.ItemTappedEventArgs e)
        {
            if (e == null)
            {
                return; //ItemSelected is called on deselection, which results in SelectedItem being set to null
            }
            var singleMedicationPage = new SingleMedicationPage((Medication)MedicationsList.SelectedItem);
            await Navigation.PushAsync(singleMedicationPage);
        }

        /*
         * Handles the compare button being pressed, opening up the compare medications page.
         */ 
        async void Handle_CompareButtonPressed(object sender, EventArgs e)
        {
            if (e == null)
            {
                return; //ItemSelected is called on deselection, which results in SelectedItem being set to null
            }
            var compareMedicationsPage = new CompareMedicationsPage();
            await Navigation.PushAsync(compareMedicationsPage);
        }

        public void refreshMedicationsListView() {
            NoDataLabel.IsVisible = false; 
            MedicationsList.IsVisible = true;
            observableMedicationList.Clear();
            observableMedicationList.AddRange(UserController.Instance.LoggedInUser.currentMedications);
            if(UserController.Instance.LoggedInUser.currentMedications.Count + UserController.Instance.LoggedInUser.historicMedications.Count >= 2) {
                CompareButton.IsVisible = true;
            }
        }

        async void Handle_MoveToClicked(object sender, EventArgs e)
        {
            if(!isClinicianAccessing) {
                await DisplayAlert("Unauthorized",
                    "Only a clinician can update a user's medications",
                    "OK");
                return;
            }

            var mi = ((MenuItem)sender);
            Medication selectedMedication = mi.CommandParameter as Medication;
            String status = SegControl.SelectedSegment == 0 ? "historic" : "current";
 

            bool answer = await DisplayAlert("Are you sure?", "Do you want to move " + selectedMedication.name + " to " + UserController.Instance.LoggedInUser.FullName + "'s " + status + " medications?", "Yes", "No");
            if (answer == true)
            {
                if (SegControl.SelectedSegment == 0)
                {
                    UserController.Instance.LoggedInUser.currentMedications.Remove(selectedMedication);
                    selectedMedication.history.Add("Stopped taking on " + DateTime.Now.ToShortDateString() + ", " + DateTime.Now.ToString("HH:mm:ss"));
                    selectedMedication.DetailString = selectedMedication.history[selectedMedication.history.Count - 1];
                    UserController.Instance.LoggedInUser.historicMedications.Add(selectedMedication);
                    observableMedicationList.Clear();
                    observableMedicationList.AddRange(UserController.Instance.LoggedInUser.currentMedications);
                }
                else
                {
                    UserController.Instance.LoggedInUser.historicMedications.Remove(selectedMedication);
                    selectedMedication.history.Add("Started taking on " + DateTime.Now.ToShortDateString() + ", " + DateTime.Now.ToString("HH:mm:ss"));
                    selectedMedication.DetailString = selectedMedication.history[selectedMedication.history.Count - 1];
                    UserController.Instance.LoggedInUser.currentMedications.Add(selectedMedication);
                    observableMedicationList.Clear();
                    observableMedicationList.AddRange(UserController.Instance.LoggedInUser.historicMedications);
                }
                //update the User
                UserAPI userAPI = new UserAPI();
                HttpStatusCode userUpdated = await userAPI.UpdateUser(true);

                switch (userUpdated)
                {
                    case HttpStatusCode.Created:
                        await DisplayAlert("",
                        "User medications successfully updated",
                        "OK");
                        await Navigation.PopAsync();
                        break;
                    case HttpStatusCode.BadRequest:
                        await DisplayAlert("",
                        "User medications update failed (400)",
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

        async void Handle_DeleteClicked(object sender, EventArgs e)
        {
            if (!isClinicianAccessing)
            {
                await DisplayAlert("Unauthorized",
                    "Only a clinician can update a user's medications",
                    "OK");
                return;
            }


            var mi = ((MenuItem)sender);
            Medication selectedMedication = mi.CommandParameter as Medication;
            bool answer = await DisplayAlert("Are you sure?", "Do you want to remove " + selectedMedication.name + " from " + UserController.Instance.LoggedInUser.FullName + "?", "Yes", "No");
            if(answer == true) {
                if(SegControl.SelectedSegment == 0) {
                    UserController.Instance.LoggedInUser.currentMedications.Remove(selectedMedication);
                    observableMedicationList.Clear();
                    observableMedicationList.AddRange(UserController.Instance.LoggedInUser.currentMedications);
                } else {
                    UserController.Instance.LoggedInUser.historicMedications.Remove(selectedMedication);
                    observableMedicationList.Clear();
                    observableMedicationList.AddRange(UserController.Instance.LoggedInUser.historicMedications);
                }
                UserAPI userAPI = new UserAPI();
                HttpStatusCode userUpdated = await userAPI.UpdateUser(true);

                switch (userUpdated)
                {
                    case HttpStatusCode.Created:
                        await DisplayAlert("",
                        "User medications successfully updated",
                        "OK");
                        await Navigation.PopAsync();
                        break;
                    case HttpStatusCode.BadRequest:
                        await DisplayAlert("",
                        "User medications update failed (400)",
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

        private async void AddMedicationButton_OnClicked(object sender, EventArgs e)
        {
            await Navigation.PushAsync(new SingleMedicationPage(this, SegControl.SelectedSegment));
        }
    }
}
