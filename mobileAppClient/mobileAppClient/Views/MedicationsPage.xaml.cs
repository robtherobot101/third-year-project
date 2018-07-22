using System;
using System.Collections.Generic;

using Xamarin.Forms;

namespace mobileAppClient
{
    public partial class MedicationsPage : ContentPage
    {
        void Handle_MedicationChanged(object sender, SegmentedControl.FormsPlugin.Abstractions.ValueChangedEventArgs e)
        {
            switch (e.NewValue)
            {
                case 0:
                    MedicationsList.ItemsSource = UserController.Instance.LoggedInUser.currentMedications;
                    break;
                case 1:
                    MedicationsList.ItemsSource = UserController.Instance.LoggedInUser.historicMedications;
                    break;
            }
        }

        public MedicationsPage()
        {
            InitializeComponent();

            //FOR SOME REASON IT DOESNT WORK IF I HAVE THESE IN THE CONSTRUCTORS??

            foreach (Medication item in UserController.Instance.LoggedInUser.currentMedications)
            {
                item.DetailString = item.History[0];
            }
            foreach (Medication item in UserController.Instance.LoggedInUser.historicMedications)
            {
                item.DetailString = item.History[item.History.Count - 1];            
            }

            MedicationsList.ItemsSource = UserController.Instance.LoggedInUser.currentMedications;
        }

        async void Handle_MedicationTapped(object sender, Xamarin.Forms.ItemTappedEventArgs e)
        {
            if (e == null)
            {
                return; //ItemSelected is called on deselection, which results in SelectedItem being set to null
            }
            var singleMedicationPage = new SingleMedicationPage((Medication)MedicationsList.SelectedItem);
            await Navigation.PushModalAsync(singleMedicationPage);
        }

        async void Handle_CompareButtonPressed(object sender, System.EventArgs e)
        {
            if (e == null)
            {
                return; //ItemSelected is called on deselection, which results in SelectedItem being set to null
            }
            var compareMedicationsPage = new CompareMedicationsPage();
            await Navigation.PushModalAsync(compareMedicationsPage);
        }
    }
}
