using System;
using System.Collections.Generic;
using System.Net;
using mobileAppClient.odmsAPI;

using Xamarin.Forms;

namespace mobileAppClient
{
    public partial class CompareMedicationsPage : ContentPage
    {
        int i = 1;

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
                }
            }

            string interactionsBody = String.Format("{0}y/o {1}\n\n{2}\n\n{3}\n\n{4}", 
                Convert.ToInt32(loggedInUser.getAge()), 
                GenderExtensions.ToString(loggedInUser.gender), 
                string.Join("\r\n", retrievedDrugInteractions.genderInteractions),
                string.Join("\r\n", retrievedDrugInteractions.ageInteractions), 
                string.Join("\r\n", retrievedDrugInteractions.durationInteractions));

            await DisplayAlert(String.Format("Interactions between {0} and {1}", selectedItem1.Text, selectedItem2.Text),
                                interactionsBody,
                                "OK");
        }

        void Handle_Clear1Pressed(object sender, System.EventArgs e)
        {
            selectedItem1.Text = "";
        }

        void Handle_Clear2Pressed(object sender, System.EventArgs e)
        {
            selectedItem2.Text = "";
        }

        async void BackButtonClicked(object sender, EventArgs args)
        {
            await Navigation.PopModalAsync();
        }
    }
}
