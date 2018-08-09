using System;
using System.Collections.Generic;

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
                    MedicationsList.ItemsSource = UserController.Instance.LoggedInUser.currentMedications;
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
                    MedicationsList.ItemsSource = UserController.Instance.LoggedInUser.historicMedications;
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
            checkIfClincianAccessing();

            //FOR SOME REASON IT DOESNT WORK IF I HAVE THESE IN THE CONSTRUCTORS??

            foreach (Medication item in UserController.Instance.LoggedInUser.currentMedications)
            {
                item.DetailString = item.History[0];
            }
            foreach (Medication item in UserController.Instance.LoggedInUser.historicMedications)
            {
                item.DetailString = item.History[item.History.Count - 1];            
            }

            if (UserController.Instance.LoggedInUser.currentMedications.Count == 0)
            {
                NoDataLabel.IsVisible = true;
                MedicationsList.IsVisible = false;
            }

            if(UserController.Instance.LoggedInUser.currentMedications.Count + UserController.Instance.LoggedInUser.historicMedications.Count < 2) {
                CompareButton.IsVisible = false;
            }

            MedicationsList.ItemsSource = UserController.Instance.LoggedInUser.currentMedications;
        }

        /**
         * Checks if a clinician is viewing the user
         */
        private void checkIfClincianAccessing()
        {
            if (ClinicianController.Instance.isLoggedIn())
            {
                isClinicianAccessing = true;
            }
            else
            {
                isClinicianAccessing = false;
            }
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
        async void Handle_CompareButtonPressed(object sender, System.EventArgs e)
        {
            if (e == null)
            {
                return; //ItemSelected is called on deselection, which results in SelectedItem being set to null
            }
            var compareMedicationsPage = new CompareMedicationsPage();
            await Navigation.PushAsync(compareMedicationsPage);
        }
    }
}
