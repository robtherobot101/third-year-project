using System;
using System.Collections.Generic;

using Xamarin.Forms;

namespace mobileAppClient
{
    /*
     * Class to handle all functionality regarding the single medication page for 
     * showing the details of a single medication of a user.
     */ 
    public partial class SingleMedicationPage : ContentPage
    {
        /*
         * Constructor which initialises the entries of the medications listview.
         */ 
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

        /*
         * Handles the back button being clicked, returning the user to 
         * the medications page.
         */ 
        async void BackButtonClicked(object sender, EventArgs args)
        {
            await Navigation.PopModalAsync();
        }
    }
}
