using System;
using System.Collections.Generic;
using System.Net;
using mobileAppClient.odmsAPI;

using Xamarin.Forms;

namespace mobileAppClient
{
    /*
     * Class to handle all functionality regarding the comparing of medications page for 
     * comparing two individual medications of a user.
     */ 
    public partial class CompareMedicationsPage : ContentPage
    {
        int i = 1;

        /*
         * Constructor which initialises the listview to contain all past and current
         * medications of the user.
         */
        public CompareMedicationsPage()
        {
            InitializeComponent();
            List<Medication> medicationItems = new List<Medication>();
            foreach (Medication item in UserController.Instance.LoggedInUser.currentMedications)
            {
                medicationItems.Add(item);
            }
            foreach (Medication item in UserController.Instance.LoggedInUser.historicMedications)
            {
                medicationItems.Add(item);
            }
            MedicationsList.ItemsSource = medicationItems;
        }

        /*
         * Event Handler to handle the selection of a single medication
         * to populate the correct selected medication field.
         */ 
        void Handle_ItemTapped(object sender, Xamarin.Forms.ItemTappedEventArgs e)
        {
            Medication selectedMedication = (Medication)MedicationsList.SelectedItem;

            if(selectedItem1.Text.Equals("")) {
                selectedItem1.Text = selectedMedication.Name;
            } else if (selectedItem2.Text.Equals("")) {
                selectedItem2.Text = selectedMedication.Name;
            } else {
                Console.WriteLine(i);
                i++;
                if (i % 2 == 0)
                {
                    selectedItem1.Text = selectedMedication.Name;
                }
                else
                {
                    selectedItem2.Text = selectedMedication.Name;
                }
            }
        }

        /*
         * Event handler to handle when a user selects medications and 
         * wishes to compare them, sending a call off to the interactions API.
         */ 
        async void Handle_ComparePressed(object sender, System.EventArgs e)
        {
            Console.WriteLine("Compare pressed!");
            DrugInteractionAPI drugInteractionAPI = new DrugInteractionAPI();
            User loggedInUser = UserController.Instance.LoggedInUser;

            DrugInteractionResult retrievedDrugInteractions = await drugInteractionAPI.RetrieveDrugInteractions(selectedItem1.Text, selectedItem2.Text);
            if (!retrievedDrugInteractions.gotInteractions)
            {
                switch (retrievedDrugInteractions.resultStatusCode)
                {
                    case HttpStatusCode.Accepted:
                        await DisplayAlert("Failed to get Interactions",
                        "Interactions yet to be researched",
                        "OK");
                        return;
                    case HttpStatusCode.NotFound:
                        await DisplayAlert("Failed to get Interactions",
                        "One or both of the drugs are invalid",
                        "OK");
                        return;
                    case HttpStatusCode.BadGateway:
                        await DisplayAlert("Failed to get Interactions",
                        "The Drug Interactions API is currently down",
                                           "OK");
                        return;
                    case HttpStatusCode.BadRequest:
                        await DisplayAlert("Failed to get Interactions",
                        "Please ensure you are connected to the internet",
                        "OK");
                        return;
                }
            }



            string interactionsBody = String.Format("{0}y/o {1}\n\n{2}\n\n{3}\n\n{4}", 
                Convert.ToInt32(loggedInUser.getAge()), 
                loggedInUser.gender, 
                string.Join("\r\n", retrievedDrugInteractions.genderInteractions),
                string.Join("\r\n", retrievedDrugInteractions.ageInteractions), 
                string.Join("\r\n", retrievedDrugInteractions.durationInteractions));

            await DisplayAlert(String.Format("Interactions between {0} and {1}", selectedItem1.Text, selectedItem2.Text),
                                interactionsBody,
                                "OK");
        }

        /*
         * Resets the selected item 1 field to be blank.
         */ 
        void Handle_Clear1Pressed(object sender, System.EventArgs e)
        {
            selectedItem1.Text = "";
        }

        /*
         * Resets the selected item 2 field to be blank.
         */ 
        void Handle_Clear2Pressed(object sender, System.EventArgs e)
        {
            selectedItem2.Text = "";
        }

    }
}
