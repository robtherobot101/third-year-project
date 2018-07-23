using System;
using System.Collections.Generic;
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
            string retrievedDrugInteractions = await drugInteractionAPI.RetrieveDrugInteractions(selectedItem1.Text, selectedItem2.Text);
            await DisplayAlert("", 
                               retrievedDrugInteractions,
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
