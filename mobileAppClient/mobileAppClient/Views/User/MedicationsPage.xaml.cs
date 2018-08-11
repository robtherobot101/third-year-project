using System;
using System.Collections.Generic;
using System.Windows.Input;
using mobileAppClient.Models.CustomObjects;
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

            observableMedicationList.AddRange(UserController.Instance.LoggedInUser.currentMedications);

            if(isClinicianAccessing) {
                var addItem = new ToolbarItem
                {
                    Command = addNewMedication,
                    Icon = "add_icon.png"
                };
                this.ToolbarItems.Add(addItem);

            }


        }

        public ICommand addNewMedication
        {
            get
            {
                return new Command(async () =>
                {
                    await Navigation.PushAsync(new SingleMedicationPage(this));
                });
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
        async void Handle_CompareButtonPressed(object sender, System.EventArgs e)
        {
            if (e == null)
            {
                return; //ItemSelected is called on deselection, which results in SelectedItem being set to null
            }
            var compareMedicationsPage = new CompareMedicationsPage();
            await Navigation.PushAsync(compareMedicationsPage);
        }

        public void refreshMedicationsListView() {
            observableMedicationList.Clear();
            observableMedicationList.AddRange(UserController.Instance.LoggedInUser.currentMedications);
        }

        async void Handle_MoveToClicked(object sender, System.EventArgs e)
        {
            var mi = ((MenuItem)sender);
            Medication selectedMedication = mi.CommandParameter as Medication;
            String status = SegControl.SelectedSegment == 0 ? "historic" : "current";
 

            bool answer = await DisplayAlert("Are you sure?", "Do you want to move " + selectedMedication.Name + " to " + UserController.Instance.LoggedInUser.FullName + "'s " + status + " medications?", "Yes", "No");
            if (answer == true)
            {
                if (SegControl.SelectedSegment == 0)
                {
                    UserController.Instance.LoggedInUser.currentMedications.Remove(selectedMedication);
                    selectedMedication.History.Add("Stopped taking on " + DateTime.Now.ToString());
                    selectedMedication.DetailString = selectedMedication.History[selectedMedication.History.Count - 1];
                    UserController.Instance.LoggedInUser.historicMedications.Add(selectedMedication);
                    observableMedicationList.Clear();
                    observableMedicationList.AddRange(UserController.Instance.LoggedInUser.currentMedications);
                }
                else
                {
                    UserController.Instance.LoggedInUser.historicMedications.Remove(selectedMedication);
                    selectedMedication.History.Add("Started taking on " + DateTime.Now.ToString());
                    selectedMedication.DetailString = selectedMedication.History[selectedMedication.History.Count - 1];
                    UserController.Instance.LoggedInUser.currentMedications.Add(selectedMedication);
                    observableMedicationList.Clear();
                    observableMedicationList.AddRange(UserController.Instance.LoggedInUser.historicMedications);
                }
                //updateUser
            }

        }

        async void Handle_DeleteClicked(object sender, System.EventArgs e)
        {
            var mi = ((MenuItem)sender);
            Medication selectedMedication = mi.CommandParameter as Medication;
            bool answer = await DisplayAlert("Are you sure?", "Do you want to remove " + selectedMedication.Name + " from " + UserController.Instance.LoggedInUser.FullName + "?", "Yes", "No");
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
                //updateUser
            }


        }
    }
}
