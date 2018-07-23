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
            IDEntry.Text = medication.Id.ToString();

            foreach(string item in medication.ActiveIngredients) 
            {
                TextCell cell = new TextCell();
                cell.Text = item;
                cell.TextColor = Color.Gray;
                activeIngredientsTableSection.Add(cell);
            }

            foreach (string item in medication.History)
            {
                TextCell cell = new TextCell();
                cell.Text = item;
                cell.TextColor = Color.Gray;
                historyTableSection.Add(cell);
            }
        }

        async void BackButtonClicked(object sender, EventArgs args)
        {
            await Navigation.PopModalAsync();
        }
    }
}
