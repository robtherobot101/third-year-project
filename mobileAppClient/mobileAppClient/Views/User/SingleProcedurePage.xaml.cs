using System;
using System.Collections.Generic;
using System.Globalization;
using System.Linq;
using System.Net;
using System.Threading.Tasks;
using mobileAppClient.Models.CustomObjects;
using mobileAppClient.odmsAPI;
using Xamarin.Forms;

namespace mobileAppClient
{
    /*
     * Class to handle all functionality regarding the single procedure page for 
     * showing the details of a single procedure of a user.
     */ 
    public partial class SingleProcedurePage : ContentPage
    {
        DateTimeFormatInfo dateTimeFormat = new DateTimeFormatInfo();

        private CustomObservableCollection<String> organsAffected;

        private CustomObservableCollection<String> organsAvailable;
        /*
         * Constructor which initialises the entries of the procedures listview.
         */ 
        public SingleProcedurePage(Procedure procedure)
        {
            Title = "Edit Procedure";
            InitializeComponent();
            affectedOrganStack.IsVisible = false;

            organsAffected = new CustomObservableCollection<string>();
            organsAffectedList.ItemsSource = organsAffected;

            SummaryEntry.Text = procedure.Summary;
            DescriptionEntry.Text = procedure.Description;
            DateDueEntry.Date = procedure.Date.ToDateTime();

            organsAffected.AddRange(procedure.OrgansAffected);
        }

        /*
         * Used when adding a new procedure
         * Constructor which initialises the entries of the procedures listview.
         */
        public SingleProcedurePage()
        {
            Title = "Add New Procedure";
            InitializeComponent();
            organsAvailable = new CustomObservableCollection<string>
            {
                "Liver",
                "Kidney",
                "Pancreas",
                "Heart",
                "Lung",
                "Intestine",
                "Cornea",
                "Middle Ear",
                "Skin",
                "Bone Marrow",
                "Connective Tissue"
            };
            organsAffected = new CustomObservableCollection<string>();

            organsAffectedList.ItemsSource = organsAffected;
            NewAffectedOrganPicker.ItemsSource = organsAvailable;

            DateDueEntry.MaximumDate = DateTime.Today;
            NewAffectedOrganPicker.SelectedIndex = 0;

            SummaryEntry.IsEnabled = true;
            DescriptionEntry.IsEnabled = true;
            DateDueEntry.IsEnabled = true;

            SummaryEntry.Placeholder = "Summary";
            DescriptionEntry.Placeholder = "Description";
            DateDueEntry.Date = DateTime.Today;
        }

        private void AddAffectedOrganClicked(object sender, EventArgs e)
        {
            if (NewAffectedOrganPicker.SelectedItem == null)
            {
                // List is empty
                return;
            }
            organsAffected.Add(NewAffectedOrganPicker.SelectedItem.ToString());
            organsAvailable.Remove(NewAffectedOrganPicker.SelectedItem.ToString());
        }

        private async void AddProcedureClicked(object sender, EventArgs e)
        {
            string summaryInput = InputValidation.Trim(SummaryEntry.Text);
            string descriptionInput = InputValidation.Trim(DescriptionEntry.Text);
            if (!await checkInputs(summaryInput, descriptionInput))
            {
                return;
            }

            Procedure newProcedure = new Procedure(summaryInput, descriptionInput, new CustomDate(DateDueEntry.Date),
                organsAffected.ToList());

            UserController.Instance.LoggedInUser.pendingProcedures.Add(newProcedure);

            UserAPI userAPI = new UserAPI();
            HttpStatusCode userUpdated = await userAPI.UpdateUser(true);

            switch (userUpdated)
            {
                case HttpStatusCode.Created:
                    await DisplayAlert("",
                        "User procedure successfully saved",
                        "OK");
                    break;
                case HttpStatusCode.BadRequest:
                    await DisplayAlert("",
                        "User procedure save failed (400)",
                        "OK");
                    break;
                case HttpStatusCode.ServiceUnavailable:
                    await DisplayAlert("",
                        "Server unavailable, check connection",
                        "OK");
                    break;
                case HttpStatusCode.InternalServerError:
                    await DisplayAlert("",
                        "Server error, please try again",
                        "OK");
                    break;
            }
        }

        private async Task<bool> checkInputs(string summary, string description)
        {
            string summaryInput = InputValidation.Trim(SummaryEntry.Text);
            string descriptionInput = InputValidation.Trim(DescriptionEntry.Text);

            if (!InputValidation.IsValidTextInput(summaryInput, true, false))
            {
                await DisplayAlert("", "Please enter a valid summary", "OK");
                return false;
            }

            if (!InputValidation.IsValidTextInput(descriptionInput, true, false))
            {
                await DisplayAlert("", "Please enter a valid description", "OK");
                return false;
            }

            return true;
        }
    }
}
