using System;
using System.Collections.Generic;

using Xamarin.Forms;

namespace mobileAppClient
{
    public partial class CompareMedicationsPage : ContentPage
    {
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
            Console.WriteLine("Hey");
        }

        async void BackButtonClicked(object sender, EventArgs args)
        {
            await Navigation.PopModalAsync();
        }
    }
}
