using System;
using System.Collections.Generic;

using Xamarin.Forms;

namespace mobileAppClient
{
    public partial class SingleMedicationPage : ContentPage
    {
        public SingleMedicationPage(Medication medication)
        {
            InitializeComponent();
            NameEntry.Text = medication.Name;
            ActiveIngredientsEntry.Text = String.Join(", ", medication.ActiveIngredients);
            HistoryEntry.Text = String.Join(", ", medication.History);
            IDEntry.Text = medication.Id.ToString();
        }

        async void BackButtonClicked(object sender, EventArgs args)
        {
            await Navigation.PopModalAsync();
        }
    }
}
