using System;
using System.Collections.Generic;
using mobileAppClient.odmsAPI;
using mobileAppClient.Models.CustomObjects;

using Xamarin.Forms;
using System.Net;

namespace mobileAppClient
{
    /*
     * Class to handle all functionality regarding the single medication page for 
     * showing the details of a single medication of a user.
     */ 
    public partial class SingleMedicationPage : ContentPage
    {
        DrugAutoFillActiveIngredientsAPI drugAutoFillActiveIngredientsAPI;
        private bool _IsLoading;
        private CustomObservableCollection<string> observableMedicationList;
        public MedicationsPage parentMedicationsPage;


        public bool IsLoading
        {
            get { return _IsLoading; }
            set
            {
                _IsLoading = value;
                if (_IsLoading == true)
                {
                    LoadingIndicator.IsVisible = true;
                    LoadingIndicator.IsRunning = true;
                }
                else
                {
                    LoadingIndicator.IsVisible = false;
                    LoadingIndicator.IsRunning = false;
                }
            }
        }


        /*
         * Constructor used to create a new medication
         */ 
        public SingleMedicationPage(MedicationsPage medicationsPage)
        {
            InitializeComponent();
            AddMedicationLayout.IsVisible = true;
            UserViewLayout.IsVisible = false;
            this.Title = "Add New Medication";
            drugAutoFillActiveIngredientsAPI = new DrugAutoFillActiveIngredientsAPI();
            observableMedicationList = new CustomObservableCollection<string>();
            MedicationsList.ItemsSource = observableMedicationList;
            this.parentMedicationsPage = medicationsPage;


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
            IsLoading = true;
            observableMedicationList.Clear();
            string searchQuery = InputValidation.Trim(MedicationSearchBar.Text);
            MedicationResponseObject medicationsReturned = await drugAutoFillActiveIngredientsAPI.autocomplete(searchQuery);
            observableMedicationList.AddRange(medicationsReturned.suggestions);

            IsLoading = false;
        }

        async void Handle_MedicationTapped(object sender, Xamarin.Forms.ItemTappedEventArgs e) {
            if (e == null)
            {
                return; //ItemSelected is called on deselection, which results in SelectedItem being set to null
            }
            bool answer = await DisplayAlert("Are you sure?", "Do you want to assign " + MedicationsList.SelectedItem + " to " + UserController.Instance.LoggedInUser.FullName + "?", "Yes", "No");
            if(answer == true) {
                //Do active ingredients call
                MedicationResponseObject medicationsReturned = await drugAutoFillActiveIngredientsAPI.activeIngredients(MedicationsList.SelectedItem.ToString());

                //Create new medication object

                Medication newMedication = new Medication();
                newMedication.Name = MedicationsList.SelectedItem.ToString();
                newMedication.ActiveIngredients = medicationsReturned.activeIngredients;
                newMedication.History = new List<string>();
                newMedication.History.Add("Started taking on " + DateTime.Now.ToString());
                newMedication.DetailString = newMedication.History[0];

                //Add it to the user's current medications

                UserController.Instance.LoggedInUser.currentMedications.Add(newMedication);
                User currentUser = UserController.Instance.LoggedInUser;
                parentMedicationsPage.refreshMedicationsListView();

                await Navigation.PopAsync();
                //Save user object
                //UserAPI userAPI = new UserAPI();
                //HttpStatusCode userUpdated = await userAPI.UpdateUser(true);

                //switch (userUpdated)
                //{
                //    case HttpStatusCode.Created:
                //        await DisplayAlert("",
                //        "User medications successfully updated",
                //        "OK");
                //        await Navigation.PopAsync();
                //        break;
                //    case HttpStatusCode.BadRequest:
                //        await DisplayAlert("",
                //        "User medications update failed (400)",
                //        "OK");
                //        break;
                //    case HttpStatusCode.ServiceUnavailable:
                //        await DisplayAlert("",
                //        "Server unavailable, check connection",
                //        "OK");
                //        break;
                //    case HttpStatusCode.Unauthorized:
                //        await DisplayAlert("",
                //        "Unauthorised to modify profile",
                //        "OK");
                //        break;
                //    case HttpStatusCode.InternalServerError:
                //        await DisplayAlert("",
                //        "Server error, please try again (500)",
                //        "OK");
                //        break;
                //}


            }
        }


    }
}
