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
                //ViewCell cell = new ViewCell();
                StackLayout tmpLayout = new StackLayout
                {
                    Orientation = StackOrientation.Vertical,
                    Padding = new Thickness(15, 0, 0, 0),
                    Children = {
                    new Label
                        {
                            Text = item,
                            TextColor = Color.Gray,
                            FontSize = 15
                        }
                    }
                };

                ViewCell viewCell = new ViewCell
                {
                    View = tmpLayout
                };
                historyTableSection.Add(viewCell);

                
            }
        }

        async void BackButtonClicked(object sender, EventArgs args)
        {
            await Navigation.PopModalAsync();
        }
    }
}
