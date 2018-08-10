using System;
using System.Collections.Generic;
using mobileAppClient.odmsAPI;

using Xamarin.Forms;

namespace mobileAppClient
{
    /*
     * Class to handle all functionality regarding the single medication page for 
     * showing the details of a single medication of a user.
     */ 
    public partial class SingleMedicationPage : ContentPage
    {
        DrugAutoFillActiveIngredientsAPI drugAutoFillActiveIngredientsAPI;


        /*
         * Constructor used to create a new medication
         */ 
        public SingleMedicationPage()
        {
            Console.WriteLine("Gday");
            InitializeComponent();
            AddMedicationLayout.IsVisible = true;
            UserViewLayout.IsVisible = false;
            this.Title = "Add New Medication";
            drugAutoFillActiveIngredientsAPI = new DrugAutoFillActiveIngredientsAPI();

        }

        /*
         * Constructor which initialises the entries of the medications listview.
         */ 
        public SingleMedicationPage(Medication medication)
        {
            InitializeComponent();
            AddMedicationLayout.IsVisible = false;
            UserViewLayout.IsVisible = true;
            this.Title = "Viewing Medication";
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


        async void Handle_SearchButtonPressed(object sender, System.EventArgs e)
        {
            // Update the current search param
            string searchQuery = InputValidation.Trim(MedicationSearchBar.Text);
            List<string> medications = await drugAutoFillActiveIngredientsAPI.autocomplete(searchQuery);
            Console.WriteLine("Hello");
        }
    }
}
