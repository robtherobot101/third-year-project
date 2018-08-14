using System;
using System.Collections.Generic;
using System.Globalization;
using System.Linq;
using System.Net;
using System.Threading.Tasks;
using mobileAppClient.Models;
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
        private Procedure currentProcedure;

        private CustomObservableCollection<String> organsAffected;

        private CustomObservableCollection<String> organsAvailable;

        private ProceduresPage proceduresPageController;
        /*
         * Constructor which initialises the entries of the procedures listview.
         */ 
        public SingleProcedurePage(Procedure procedure, ProceduresPage proceduresPageController)
        {
            Title = "View Procedure";
            InitializeComponent();
            affectedOrganStack.IsVisible = false;

            currentProcedure = procedure;
            this.proceduresPageController = proceduresPageController;

            organsAffected = new CustomObservableCollection<string>();
            organsAffectedList.ItemsSource = organsAffected;

            SummaryEntry.Text = procedure.summary;
            DescriptionEntry.Text = procedure.description;
            DateDueEntry.Date = procedure.date.ToDateTime();

            organsAffected.AddRange(procedure.organsAffected.ConvertAll(OrganExtensions.ToString));

            if (this.proceduresPageController.isClinicianAccessing)
            {
                EditProcedureButton.IsVisible = true;
            }            
        }

        /*
         * Used when adding a new procedure
         * Constructor which initialises the entries of the procedures listview.
         */
        public SingleProcedurePage(ProceduresPage proceduresPageController)
        {
            Title = "Add New Procedure";
            InitializeComponent();
            this.proceduresPageController = proceduresPageController;

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
            NewAffectedOrganPicker.SelectedIndex = 0;

            SummaryEntry.IsEnabled = true;
            DescriptionEntry.IsEnabled = true;
            DateDueEntry.IsEnabled = true;

            SummaryEntry.Placeholder = "Summary";
            DescriptionEntry.Placeholder = "Description";
            DateDueEntry.Date = DateTime.Today;

            AddProcedureButton.IsVisible = true;
        }

        /// <summary>
        /// When the Add Affected organ is clicked, the selected organ is added to affected organs
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
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

        /// <summary>
        /// When the Add Procedure button is clicked, this method is ran
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private async void AddProcedureClicked(object sender, EventArgs e)
        {
            string summaryInput = InputValidation.Trim(SummaryEntry.Text);
            string descriptionInput = InputValidation.Trim(DescriptionEntry.Text);

            if (!await CheckInputs(summaryInput, descriptionInput))
            {
                return;
            }


            Procedure newProcedure = new Procedure(summaryInput, descriptionInput, new CustomDate(DateDueEntry.Date),
                new List<Organ>(organsAffected.ToList().ConvertAll(OrganExtensions.ToOrgan)));

            if (newProcedure.date.ToDateTime() < DateTime.Today)
            {
                UserController.Instance.LoggedInUser.previousProcedures.Add(newProcedure);
                proceduresPageController.refreshProcedures(1);
            }
            else
            {
                UserController.Instance.LoggedInUser.pendingProcedures.Add(newProcedure);
                proceduresPageController.refreshProcedures(0);
            }

            await uploadUser();
        }

        private async Task<bool> CheckInputs(string summary, string description)
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

        /*
         * Converts any given string into having an uppercase first char with lowercase for the rest
         */
        private string FirstCharToUpper(string input)
        {
            if (String.IsNullOrEmpty(input))
                return "";
            input = input.ToLower();
            return input.First().ToString().ToUpper() + input.Substring(1);
        }


        private void EditProcedureButton_OnClicked(object sender, EventArgs e)
        {
            Title = "Edit Procedure";
            EditProcedureButton.IsVisible = false;

            List<string> allOrgans = new List<string>
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
            List<string> organsAffectedConv =
                currentProcedure.organsAffected.ConvertAll(OrganExtensions.ToString);

            foreach (string org in organsAffectedConv)
            {
                Console.WriteLine(org);
            }

            List<string> organsAvailableRaw = allOrgans.Except(organsAffectedConv).ToList();

            organsAvailable = new CustomObservableCollection<string>(organsAvailableRaw);
            organsAffected = new CustomObservableCollection<string>(currentProcedure.organsAffected.ConvertAll(OrganExtensions.ToString));

            organsAffectedList.ItemsSource = organsAffected;
            NewAffectedOrganPicker.ItemsSource = organsAvailable;
            NewAffectedOrganPicker.SelectedIndex = 0;

            EditProcedureButton.IsVisible = false;
            SaveProcedureButton.IsVisible = true;

            affectedOrganStack.IsVisible = true;
            SummaryEntry.IsEnabled = true;
            DescriptionEntry.IsEnabled = true;
            DateDueEntry.IsEnabled = true;

        }

        private async void SaveProcedureButton_OnClicked(object sender, EventArgs e)
        {
            string summaryInput = InputValidation.Trim(SummaryEntry.Text);
            string descriptionInput = InputValidation.Trim(DescriptionEntry.Text);

            if (!await CheckInputs(summaryInput, descriptionInput))
            {
                return;
            }

            currentProcedure.summary = summaryInput;
            currentProcedure.description = descriptionInput;
            currentProcedure.date = new CustomDate(DateDueEntry.Date);
            
            List<Organ> organsTrulyAffected = new List<Organ>();

            foreach (var organString in organsAffected)
            {
                organsTrulyAffected.Add(OrganExtensions.ToOrgan(organString));
            }

            currentProcedure.organsAffected = organsTrulyAffected;

            UserController.Instance.LoggedInUser.previousProcedures.Remove(currentProcedure);
            UserController.Instance.LoggedInUser.pendingProcedures.Remove(currentProcedure);

            if (currentProcedure.date.ToDateTime() < DateTime.Today)
            {
                UserController.Instance.LoggedInUser.previousProcedures.Add(currentProcedure);
                proceduresPageController.refreshProcedures(1);
            }
            else
            {
                UserController.Instance.LoggedInUser.pendingProcedures.Add(currentProcedure);
                proceduresPageController.refreshProcedures(0);
            }

            await uploadUser();
        }

        private async Task uploadUser()
        {
            UserAPI userAPI = new UserAPI();
            HttpStatusCode userUpdated = await userAPI.UpdateUser(true);
            Console.WriteLine(userUpdated);
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
            await proceduresPageController.Navigation.PopAsync();
        }
    }
}
